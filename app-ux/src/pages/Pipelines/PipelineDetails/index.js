import PageTitle from "../../../components/Nav/PageTitle";
import { breadcrumbs } from "..";
import ReactFlow, { Controls, Background } from 'reactflow';
import 'reactflow/dist/style.css';
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import useNotifications from "../../../hooks/useNotifications";
import get from 'lodash/get';
import axios from "axios";
import Loading from "../../../components/Loading";

const edgeOptions = {
    animated: true,
    style: {
        stroke: 'black',
    },
};

const PipelineDetails = () => {
    const navigate = useNavigate();
    const { addNotification } = useNotifications();
    const { id } = useParams();

    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);

    const [nodes, setNodes] = useState([]);
    const [edges, setEdges] = useState([]);


    useEffect(() => {
        axios.get(`/api/pipeline/${id}`).then(response => {
            setData(response.data);

            setNodes(() => [{
                id: response.data.source.id,
                type: 'input',
                data: { label: response.data.source.name },
                connectable: true,
                position: { x: 250, y: 25 },
            },
            {
                id: 'filter',
                data: { label: 'Filter data' },
                connectable: true,
                position: { x: 300, y: 125 },
            },
            {
                id: 'enrich',
                data: { label: 'Enrich data' },
                connectable: true,
                position: { x: 350, y: 225 },
            },
            {
                id: response.data.destination.id,
                type: 'output',
                data: { label: response.data.destination.name },
                position: { x: 400, y: 425 },
            }])

            setEdges(() => [
                { id: response.data.id, source: response.data.source.id, target: 'filter' },
                { id: response.data.id + "f", source: 'filter', target: 'enrich' },
                { id: response.data.id + "e", source: 'enrich', target: response.data.destination.id }
            ])
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

    if (loading) {
        return <Loading />
    }

    return <div><PageTitle itemKey="pipelines" label={data.name} breadcrumbs={breadcrumbs} />
        <div style={{ width: '100%', height: 'calc(100vh - 300px)' }}>
            <ReactFlow nodes={nodes} edges={edges} defaultEdgeOptions={edgeOptions}>
                <Background />
                <Controls />
            </ReactFlow>
        </div>
    </div>
}

export default PipelineDetails;