import PageTitle from "../../../components/Nav/PageTitle";
import { breadcrumbs } from "..";
import ReactFlow, { Controls, Background, useEdgesState, updateEdge, addEdge, useNodesState } from 'reactflow';
import 'reactflow/dist/style.css';
import { useCallback, useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import useNotifications from "../../../hooks/useNotifications";
import get from 'lodash/get';
import filter from 'lodash/filter';
import forEach from 'lodash/forEach';
import axios from "axios";
import Loading from "../../../components/Loading";
import TransformationList from "./TransformationList";
import ItemSelectorModal from "../../../components/ItemSelectorModal";
import { IconDeviceFloppy } from "@tabler/icons-react";
import ButtonEdge from './ButtonEdge.js';

const START_POS_X = 250;
const START_POS_Y = 50;
const edgeOptions = {
    animated: true,
    style: {
        stroke: 'hsl(var(--bc))',
    },
};

const edgeTypes = {
    buttonedge: ButtonEdge,
};

const proOptions = { hideAttribution: true };

const getTransformationNodes = (connectedNodes = [], unconnectedNodes = []) => {
    const nodes = [...connectedNodes.map((transformation, i) => ({
        id: transformation.id,
        data: { label: transformation.name },
        connectable: true,
        position: { x: (START_POS_X + (i + 1) * 50), y: (START_POS_Y + (i + 1) * 100) },
        style: { backgroundColor: `hsl(var(--in))`, fontSize: '1.25rem' },
    })), ...unconnectedNodes.map((transformation, i) => ({
        id: transformation.id,
        data: { label: transformation.name },
        connectable: true,
        position: { x: ((START_POS_X + 400) + (i + 1) * 50), y: (START_POS_Y + (i + 1) * 100) },
        style: { backgroundColor: `hsl(var(--in))`, fontSize: '1.25rem' },
    }))];
    return nodes;
}

const processConnectionMap = (data) => {
    const edges = [], connectedNodes = [], unconnectedNodes = [];

    const { transformations = [], transformationMap = {} } = data;
    forEach(transformations, transformation => {

        if (!transformationMap[transformation.id].head && !transformationMap[transformation.id].tail) {
            unconnectedNodes.push(transformation);
            return;
        }
        const headId = transformationMap[transformation.id].head,
            tailId = transformationMap[transformation.id].tail;

        if (headId) {
            edges.push({
                id: transformation.id + headId,
                source: headId,
                target: transformation.id
            })
            connectedNodes(transformation);
        }

        if (tailId) {
            edges.push({
                id: tailId + transformation.id,
                source: transformation.id,
                target: tailId
            })
            connectedNodes(transformation);
        }
    });

    return {
        edges, connectedNodes, unconnectedNodes
    };
}

/**
 * Ensure that there are edges connecting from source to destination node
 */
const isPipelineValid = (data, edges) => {
    const { source, destination, transformationMap } = data;
    let currentNode = source.id, isValid = false, i = 0;

    while (i < edges.length) {
        const filteredNodes = filter(edges, { source: currentNode });
        if (filteredNodes.length !== 1) {
            break;
        }
        const nextEdge = filteredNodes[0];

        if (nextEdge.target === destination.id) {
            isValid = true;
            break;
        } else {
            currentNode = nextEdge.target;
        }

        i++;
    }

    return isValid;
}

const PipelineDetails = () => {
    const edgeUpdateSuccessful = useRef(true);
    const navigate = useNavigate();
    const { addNotification } = useNotifications();
    const { id } = useParams();
    const [showTranformationSelector, setShowTransformationSelector] = useState(false);

    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);

    const [nodes, setNodes, onNodesChange] = useNodesState([]);
    const [edges, setEdges, onEdgesChange] = useEdgesState([]);


    useEffect(() => {
        axios.get(`/api/pipeline/${id}`).then(response => {
            setData(response.data);
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while fetching Pipeline'),
                type: 'error'
            });
            navigate('/pipelines');
        }).finally(() => {
            setLoading(false);
        })
    }, [id, addNotification, navigate]);

    const removeEdge = (evt, edgeId) => {
        evt.preventDefault();
        setEdges((current) => {
            return current.filter((e) => e.id !== edgeId);
        });
    }
    useEffect(() => {
        if (!data) {
            return;
        }

        const { edges, connectedNodes, unconnectedNodes } = processConnectionMap(data);
        const transformations = data?.transformations || [];

        setNodes(() => [{
            id: data.source.id,
            type: 'input',
            data: { label: data.source.name },
            connectable: true,
            deletable: false,
            position: { x: START_POS_X, y: START_POS_Y },
            style: { backgroundColor: `hsl(var(--a))`, fontSize: '1.25rem' },
        },
        ...getTransformationNodes(connectedNodes, unconnectedNodes),
        {
            id: data.destination.id,
            type: 'output',
            data: { label: data.destination.name },
            position: { x: START_POS_X + (transformations?.length + 1) * 50, y: (START_POS_Y + (transformations?.length + 1) * 100) },
            style: { backgroundColor: `hsl(var(--er))`, fontSize: '1.25rem' },
        }])

        setEdges(() => [

            { id: data.id, source: data.source.id, target: data.destination.id, deletable: true, type: 'buttonedge', data: { removeEdge } },
        ])
    }, [data])

    const onSelectTransformation = (item) => {
        linkTransformation(item.id);
    }

    const selectTransformation = () => {
        setShowTransformationSelector(true);
    }

    const linkTransformation = (transformationId) => {
        const payload = { transformationId };
        axios.post(`/api/pipeline/${id}/link/transformation`, payload)
            .then(response => {
                setData(response.data);
                addNotification({
                    message: 'Transformation is now associated to the Pipeline',
                    type: 'success'
                });
                setShowTransformationSelector(false);
            })
            .catch(err => {
                addNotification({
                    message: get(err, 'response.data.message', 'An error occurred while associating selected transformation to the Pipeline'),
                    type: 'error'
                });
            })
    }

    const unlinkTransformation = (transformationId) => {
        const payload = { transformationId };
        axios.post(`/api/pipeline/${id}/unlink/transformation`, payload)
            .then(response => {
                setData(response.data);
                addNotification({
                    message: 'Transformation is now removed from the Pipeline',
                    type: 'success'
                });
            })
            .catch(err => {
                addNotification({
                    message: get(err, 'response.data.message', 'An error occurred while removing selected transformation from the Pipeline'),
                    type: 'error'
                });
            })
    }

    const onConnect = (params) => {
        //check if there is an existing edge from the source
        const existingSources = filter(edges, { source: params.source });


        if (existingSources.length > 0) {
            addNotification({
                message: 'There is an existing edge from the same source node. Remove the existing edge for connecting to a new node.',
                type: 'error'
            });
            return;
        }

        const existingDestinations = filter(edges, { target: params.target });

        if (existingDestinations.length > 0) {
            addNotification({
                message: 'There is an existing edge to this destination node. Remove the existing edge for connecting to this node.',
                type: 'error'
            });
            return;
        }


        setEdges((current) => addEdge({ ...params, type: 'buttonedge', data: { removeEdge } }, current));
    }

    const onEdgeUpdateStart = useCallback(() => {
        edgeUpdateSuccessful.current = false;
    }, []);

    const onEdgeUpdate = useCallback((oldEdge, newConnection) => {
        edgeUpdateSuccessful.current = true;
        setEdges((els) => updateEdge(oldEdge, newConnection, els));
    }, []);

    const onEdgeUpdateEnd = useCallback((_, edge) => {
        if (!edgeUpdateSuccessful.current) {
            setEdges((eds) => eds.filter((e) => e.id !== edge.id));
        }

        edgeUpdateSuccessful.current = true;
    }, []);

    const savePipeline = (e) => {
        console.log(edges, nodes);
        e.preventDefault();
        console.log(isPipelineValid(data, edges));
    }

    if (loading) {
        return <Loading />
    }

    return <div><PageTitle itemKey="pipelines" label={data.name} breadcrumbs={breadcrumbs} >
        <button onClick={savePipeline} className="btn btn-primary btn-sm md:btn-md"><IconDeviceFloppy size={24} className="mr-2" />Save Changes</button>
    </PageTitle>
        <div className="grid lg:grid-cols-8 md:grid-cols-3 grid-cols-2 gap-2">
            <div className="lg:col-span-6 md:col-span-2 col-span-1" style={{ width: '100%', minHeight: 'calc(100vh - 200px)', height: '100%' }}>
                <ReactFlow fitView edgeTypes={edgeTypes} onNodesChange={onNodesChange} onEdgesChange={onEdgesChange} nodes={nodes} edges={edges} defaultEdgeOptions={edgeOptions} proOptions={proOptions} onConnect={onConnect} onEdgeUpdate={onEdgeUpdate}
                    onEdgeUpdateStart={onEdgeUpdateStart} onEdgeUpdateEnd={onEdgeUpdateEnd}>
                    <Background />
                    <Controls />
                </ReactFlow>
            </div>
            <div className="lg:col-span-2 md:col-span-1 col-span-1 mx-3" style={{ width: '100%', height: '100%' }}>
                <TransformationList selectTransformation={selectTransformation} transformations={data.transformations} unlinkTransformation={unlinkTransformation} />
            </div>
        </div>

        {showTranformationSelector && <ItemSelectorModal url="/api/transformation" title="Associate Transformation" cols={[{ "label": "Name", "datacss": "text-left", "css": "w-3/4", "name": "name" }, { "label": "Active", "datacss": "", "css": "text-center", "name": "active" }]}
            onCancel={() => setShowTransformationSelector(false)} onSelect={onSelectTransformation} />}
    </div>

}

export default PipelineDetails;