import { useNavigate, useParams } from "react-router-dom";
import PageTitle from "../../../components/Nav/PageTitle";
import { breadcrumbs } from "..";
import axios from "axios";
import Loading from "../../../components/Loading";
import { useEffect, useState } from "react";
import useNotifications from "../../../hooks/useNotifications";
import get from 'lodash/get';
import DestinationDetailsCard from "./DestinationDetailsCard";
import Tabs from "../../../components/Tabs";
import DestinationPipelines from "./DestinationPipelines";
import ContextHelp from "../../../components/ContextHelp";

const tabs = [{ 'key': 'overview', label: 'Overview' }, { 'key': 'pipelines', label: 'Pipelines' }];

const DestinationDetails = (props) => {
    const navigate = useNavigate();
    const { addNotification } = useNotifications();
    const { id, tab } = useParams();
    const [loading, setLoading] = useState(true);
    const [data, setData] = useState(null);

    useEffect(() => {
        axios.get(`/api/destination/${id}`).then(response => {
            setData(response.data);
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while fetching Destination'),
                type: 'error'
            });
            navigate('/destinations');
        }).finally(() => {
            setLoading(false);
        })
    }, [id, addNotification, navigate]);

    if (loading) {
        return <Loading />
    }

    const onTabChange = (tab) => {
        navigate(`/destinations/${id}/${tab}`);
    }

    return <div>
        <PageTitle itemKey="sources" label={data.name} breadcrumbs={breadcrumbs} />
        <Tabs tabs={tabs} activeTab={tab} onTabChange={onTabChange} />
        {(!tab || tab === 'overview') &&
            <div className="grid grid-flow-row-dense grid-cols-1 xl:grid-cols-8 gap-4 ">
                <div className="xl:col-span-6">
                    <DestinationDetailsCard data={data} onUpdate={setData} />
                </div>
                <div className="hidden xl:col-span-2 xl:block pt-4">
                    <ContextHelp page="destination-details" />
                </div>
            </div>
        }
        {tab === 'pipelines' && <DestinationPipelines destinationId={id} />}
    </div>
}

export default DestinationDetails;