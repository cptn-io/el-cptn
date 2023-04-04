import { useNavigate, useParams } from "react-router-dom";
import PageTitle from "../../../components/Nav/PageTitle";
import { breadcrumbs } from "..";
import axios from "axios";
import Loading from "../../../components/Loading";
import { useEffect, useState } from "react";
import useNotifications from "../../../hooks/useNotifications";
import get from 'lodash/get';
import SourceDetailsCard from "./SourceDetailsCard";
import SourceMetrics from "./SourceMetrics";
import Tabs from "../../../components/Tabs";
import SourcePipelines from "./SourcePipelines";
import InboundEventList from "./InboundEventList";

const tabs = [{ 'key': 'overview', label: 'Overview' }, { 'key': 'pipelines', label: 'Pipelines' }, { 'key': 'events', label: 'Inbound Events' }];

const SourceDetails = (props) => {
    const navigate = useNavigate();
    const { addNotification } = useNotifications();
    const { id, tab } = useParams();
    const [loading, setLoading] = useState(true);
    const [data, setData] = useState(null);

    useEffect(() => {
        axios.get(`/api/source/${id}`).then(response => {
            setData(response.data);
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while fetching Source'),
                type: 'error'
            });
            navigate('/sources');
        }).finally(() => {
            setLoading(false);
        })
    }, [id, addNotification, navigate]);

    if (loading) {
        return <Loading />
    }

    const onTabChange = (tab) => {
        navigate(`/sources/${id}/${tab}`);
    }

    return <div>
        <PageTitle itemKey="sources" label={data.name} breadcrumbs={breadcrumbs} />
        <SourceMetrics sourceId={id} />
        <Tabs tabs={tabs} activeTab={tab} onTabChange={onTabChange} />
        {(!tab || tab === 'overview') &&
            <div className="grid grid-flow-row-dense grid-cols-1 xl:grid-cols-8 gap-4 ">
                <div className="xl:col-span-6">
                    <SourceDetailsCard data={data} onUpdate={setData} />
                </div>
                <div className="hidden xl:col-span-2 xl:block">
                    <div className="card bg-base-100 shadow">
                        <div className="card-body">
                            <h2 className="card-title">Helpful information</h2>
                            <div>
                                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        }
        {tab === 'pipelines' && <SourcePipelines sourceId={id} />}
        {tab === 'events' && <InboundEventList sourceId={id} />}
    </div>
}

export default SourceDetails;