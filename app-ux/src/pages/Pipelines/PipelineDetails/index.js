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

const defaultNodes = [
    {
        id: 'a',
        type: 'input',
        data: { label: 'Node A' },
        connectable: true,
        position: { x: 250, y: 25 },
    },

    {
        id: 'b',
        data: { label: 'Node B' },
        position: { x: 100, y: 125 },
    },
    {
        id: 'c',
        type: 'output',
        data: { label: 'Node C' },
        connectable: true,
        position: { x: 250, y: 250 },
    },
];

const defaultEdges = [{ id: 'a-b', source: 'a', target: 'b' }, { id: 'b-c', source: 'b', target: 'c' }];

const connectionLineStyle = { stroke: 'white' };
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
                id: response.data.destination.id,
                type: 'output',
                data: { label: response.data.destination.name },
                position: { x: 350, y: 325 },
            }])

            setEdges(() => [
                { id: response.data.id, source: response.data.source.id, target: response.data.destination.id }
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