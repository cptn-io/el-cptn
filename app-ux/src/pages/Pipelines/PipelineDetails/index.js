import PageTitle from "../../../components/Nav/PageTitle";
import { breadcrumbs } from "..";
import 'reactflow/dist/style.css';
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import useNotifications from "../../../hooks/useNotifications";
import get from 'lodash/get';
import axios from "axios";
import Loading from "../../../components/Loading";
import Tabs from "../../../components/Tabs";
import PipelineEditor from "./PipelineEditor";
import PipelineOverview from "./PipelineOverview";
import PipelineMetrics from "./PipelineMetrics";
import OutboundEventList from "./OutboundEventList";
import PipelineSchedule from "./PipelineSchedule";


const tabs = [{ 'key': 'overview', label: 'Overview' }, { 'key': 'schedule', label: 'Schedule' }, { 'key': 'editor', label: 'Editor' }, { 'key': 'events', label: 'Outbound Events' }];


const PipelineDetails = () => {

    const navigate = useNavigate();
    const { addNotification } = useNotifications();
    const { id, tab } = useParams();

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

    const discardChanges = (e) => {
        e.preventDefault();
        setData(() => ({
            ...data
        }));
        setDraft(() => ({
            ...data
        }));
    };

    if (loading) {
        return <Loading />
    }

    const onTabChange = (tab) => {
        navigate(`/pipelines/${id}/${tab}`);
    }

    return <div><PageTitle itemKey="pipelines" label={data.name} breadcrumbs={breadcrumbs} />
        <PipelineMetrics pipelineId={id} />
        <Tabs tabs={tabs} activeTab={tab} onTabChange={onTabChange} />
        {(!tab || tab === 'overview') && <PipelineOverview data={data} onUpdate={setData} />}
        {tab === 'editor' &&
            <PipelineEditor id={id} draft={draft} data={data} setData={setData} setDraft={setDraft} discardChanges={discardChanges} />
        }
        {tab === 'events' && <OutboundEventList pipelineId={id} />}
        {tab === 'schedule' && <PipelineSchedule pipeline={data} />}
    </div>
}

export default PipelineDetails;