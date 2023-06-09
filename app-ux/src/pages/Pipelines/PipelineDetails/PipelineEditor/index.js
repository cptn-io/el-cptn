import { ReactFlowProvider } from "reactflow";
import PipelineView from "./PipelineView";
import TransformationList from "./TransformationList";
import filter from 'lodash/filter';
import { useRef, useState } from "react";
import axios from "axios";
import useNotifications from "../../../../hooks/useNotifications";
import ItemSelectorModal from "../../../../components/ItemSelectorModal";
import get from 'lodash/get';
import { IconArrowBackUp, IconDeviceFloppy } from "@tabler/icons-react";
/**
 * Ensure that there are edges connecting from source to destination node
 */
const isPipelineValid = (data, edgeMap) => {
    const { source, destination } = data;
    const visitedNodes = {};

    let currentNode = source.id, isValid = false;
    while (currentNode && !visitedNodes[currentNode]) {

        if (currentNode === destination.id) {
            isValid = true;
            break;
        }

        visitedNodes[currentNode] = true;
        const currentNodeEdges = filter(edgeMap, { source: currentNode });
        //there should be exactly one edge from the node.
        if (currentNodeEdges.length !== 1) {
            break;
        }
        const currentEdge = currentNodeEdges[0];
        currentNode = currentEdge.target;
    }
    return isValid;
}


const PipelineEditor = (props) => {
    const ref = useRef();
    const { id, draft, data, setDraft, setData, discardChanges } = props;
    const { addNotification } = useNotifications();
    const [showTranformationSelector, setShowTransformationSelector] = useState(false);

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
        } else {
            addNotification({
                message: 'The selected transformation is already in use in this Pipeline',
                type: 'error'
            });
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
                addNotification({
                    message: 'The Pipeline changes have been saved.',
                    type: 'success'
                });
            }).catch(err => {
                addNotification({
                    message: get(err, 'response.data.message', 'An error occurred while updating the Pipeline'),
                    type: 'error'
                });
            })
    }

    return <div className="grid lg:grid-cols-12 md:grid-cols-3 grid-cols-2 gap-2">
        <ReactFlowProvider>
            <div className="xl:col-span-9 lg:col-span-8 md:col-span-2 col-span-1" style={{ width: '100%', minHeight: 'calc(100vh - 250px)', height: '100%' }}>
                <PipelineView ref={ref} draft={draft} />
            </div>
        </ReactFlowProvider>
        <div className="xl:col-span-3 lg:col-span-4 md:col-span-1 col-span-1 overflow-x-scroll">
            <div className="my-2 flex justify-center">
                <div className="join">
                    <button onClick={discardChanges} className="btn join-item"><IconArrowBackUp size={24} className="mr-2" />Reset</button>
                    <button onClick={savePipeline} className="btn btn-primary join-item"><IconDeviceFloppy size={24} className="mr-2" />Update</button>
                </div>
            </div>
            <TransformationList selectTransformation={selectTransformation} transformations={draft.transformations} unlinkTransformation={unlinkTransformation} />
        </div>
        {showTranformationSelector && <ItemSelectorModal url="/api/transformation" title="Associate Transformation" cols={[{ "label": "Name", "datacss": "text-left", "css": "w-3/4", "name": "name" }, { "label": "Active", "datacss": "", "css": "text-center", "name": "active" }]}
            onCancel={() => setShowTransformationSelector(false)} onSelect={onSelectTransformation} />}
    </div>
}

export default PipelineEditor;