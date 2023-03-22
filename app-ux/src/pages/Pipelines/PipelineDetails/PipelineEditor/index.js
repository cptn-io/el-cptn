import ReactFlow, { ReactFlowProvider, Controls, Background, useReactFlow, useEdgesState, updateEdge, addEdge, useNodesState, MarkerType } from 'reactflow';
import filter from 'lodash/filter';
import ButtonEdge from './ButtonEdge.js';
import { forwardRef, useCallback, useEffect, useImperativeHandle, useRef } from "react";
import useNotifications from '../../../../hooks/useNotifications.js';
import { processConnectionMap, getTransformationNodes } from './helpers.js';

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

const PipelineEditor = forwardRef((props, ref) => {
    const { draft } = props;
    const { addNotification } = useNotifications();

    const reactFlowInstance = useReactFlow();

    const edgeUpdateSuccessful = useRef(true);
    const [nodes, setNodes, onNodesChange] = useNodesState([]);
    const [edges, setEdges, onEdgesChange] = useEdgesState([]);

    useImperativeHandle(ref, () => ({
        getCurrentTransformMap() {
            const positions = {};
            nodes.forEach(({ id, position }) => positions[id] = position);

            const edgeMap = edges.map(({ source, target }) => ({
                source,
                target
            }))

            return {
                positions,
                edgeMap
            }
        }
    }));

    const removeEdge = (evt, edgeId) => {
        evt.preventDefault();
        setEdges((current) => {
            return current.filter((e) => e.id !== edgeId);
        });
    }

    useEffect(() => {
        if (!draft) {
            return;
        }

        const { edges, connectedNodes, unconnectedNodes } = processConnectionMap(draft);

        setNodes(() => getTransformationNodes(draft, connectedNodes, unconnectedNodes))

        const edgesWithButtons = edges.map(edge => ({ ...edge, type: 'buttonedge', data: { removeEdge } }));

        setEdges(() => edgesWithButtons);

        setTimeout(reactFlowInstance.fitView, 100);
    }, [draft.transformations, draft.transformationMap])

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

    return <ReactFlow fitView edgeTypes={edgeTypes} onNodesChange={onNodesChange} onEdgesChange={onEdgesChange} nodes={nodes} edges={edges} defaultEdgeOptions={edgeOptions} proOptions={proOptions} onConnect={onConnect} onEdgeUpdate={onEdgeUpdate}
        onEdgeUpdateStart={onEdgeUpdateStart} onEdgeUpdateEnd={onEdgeUpdateEnd}>
        <Background />
        <Controls />
    </ReactFlow>
});

export default PipelineEditor;