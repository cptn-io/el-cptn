import PageTitle from "../../../components/Nav/PageTitle";
import { breadcrumbs } from "..";
import 'reactflow/dist/style.css';
import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import useNotifications from "../../../hooks/useNotifications";
import get from 'lodash/get';
import filter from 'lodash/filter';
import axios from "axios";
import Loading from "../../../components/Loading";
import TransformationList from "./TransformationList";
import ItemSelectorModal from "../../../components/ItemSelectorModal";
import { IconDeviceFloppy } from "@tabler/icons-react";
import PipelineEditor from "./PipelineEditor";
import { ReactFlowProvider } from "reactflow";


/**
 * Ensure that there are edges connecting from source to destination node
 */
const isPipelineValid = (data, edgeMap) => {
    const { source, destination } = data;
    const visitedNodes = {};

    let currentNode = source.id, isValid = false;
    while (!visitedNodes[currentNode]) {
        visitedNodes[currentNode] = true;
        const currentNodeEdges = filter(edgeMap, { source: currentNode });
        //there should be exactly one edge from the node.
        if (currentNodeEdges.length !== 1) {
            break;
        }
        const currentEdge = currentNodeEdges[0];
        currentNode = currentEdge.target;

        if (currentNode === destination.id) {
            isValid = true;
            break;
        }
    }

    return isValid;
}

const PipelineDetails = () => {
    const ref = useRef();
    const navigate = useNavigate();
    const { addNotification } = useNotifications();
    const { id } = useParams();
    const [showTranformationSelector, setShowTransformationSelector] = useState(false);

    const [data, setData] = useState(null);
    const [draft, setDraft] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        axios.get(`/api/pipeline/${id}`).then(response => {
            setData(response.data);
            setDraft(response.data);
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

    const onSelectTransformation = (item) => {
        linkTransformation(item.id, item.name);
    }

    const selectTransformation = () => {
        setShowTransformationSelector(true);
    }

    const linkTransformation = (id, name) => {
        const transformations = draft?.transformations || [];
        if (filter(transformations, { id }).length === 0) {
            setDraft(current => ({
                ...current,
                transformations: [
                    ...transformations,
                    { id, name }
                ]
            }))
        }
        setShowTransformationSelector(false);
    }

    const unlinkTransformation = (id) => {
        const transformations = draft?.transformations || [];
        setDraft(current => ({
            ...current,
            transformations: transformations.filter(e => e.id !== id)
        }))
    }



    const savePipeline = (e) => {
        const transformationMap = ref.current.getCurrentTransformMap();

        if (!isPipelineValid(data, transformationMap.edgeMap)) {
            addNotification({
                message: 'The pipeline does not have a valid route to destination. Changes are not saved.',
                type: 'error'
            });
            return;
        }

        const payload = { transformationMap, transformations: draft.transformations };


        axios.put(`/api/pipeline/${id}`, payload)
            .then(response => {
                setData(response.data);
                setDraft(response.data)
            }).catch(err => {
                addNotification({
                    message: get(err, 'response.data.message', 'An error occurred while updating the Pipeline'),
                    type: 'error'
                });
            })
    }

    if (loading) {
        return <Loading />
    }

    return <div><PageTitle itemKey="pipelines" label={data.name} breadcrumbs={breadcrumbs} >
        <button onClick={savePipeline} className="btn btn-primary btn-sm md:btn-md"><IconDeviceFloppy size={24} className="mr-2" />Save Changes</button>
    </PageTitle>
        <div className="grid lg:grid-cols-8 md:grid-cols-3 grid-cols-2 gap-2">
            <ReactFlowProvider>
                <div className="lg:col-span-6 md:col-span-2 col-span-1" style={{ width: '100%', minHeight: 'calc(100vh - 200px)', height: '100%' }}>
                    <PipelineEditor ref={ref} draft={draft} />
                </div>
            </ReactFlowProvider>
            <div className="lg:col-span-2 md:col-span-1 col-span-1 mx-3" style={{ width: '100%', height: '100%' }}>
                <TransformationList selectTransformation={selectTransformation} transformations={draft.transformations} unlinkTransformation={unlinkTransformation} />
            </div>
        </div>

        {showTranformationSelector && <ItemSelectorModal url="/api/transformation" title="Associate Transformation" cols={[{ "label": "Name", "datacss": "text-left", "css": "w-3/4", "name": "name" }, { "label": "Active", "datacss": "", "css": "text-center", "name": "active" }]}
            onCancel={() => setShowTransformationSelector(false)} onSelect={onSelectTransformation} />}
    </div>

}

export default PipelineDetails;