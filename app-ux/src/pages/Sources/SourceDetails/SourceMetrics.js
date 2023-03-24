import { IconCircleCheck, IconTimelineEventPlus, IconCloudComputing } from '@tabler/icons-react';
import axios from 'axios';
import { useEffect, useState } from 'react';
import Loading from '../../../components/Loading';
import useNotifications from '../../../hooks/useNotifications';
import get from 'lodash/get';
import { processInboundMetrics } from '../../../common/metricHelpers';
import pluralize from 'pluralize';

const SourceMetrics = (props) => {
    const { sourceId } = props;
    const { addNotification } = useNotifications();
    const [loading, setLoading] = useState(true);

    const [metrics, setMetrics] = useState(null);

    useEffect(() => {
        axios.get(`/api/dashboard/source/${sourceId}/metrics`).then(response => {

            const inboundData = processInboundMetrics(response.data);

            setMetrics({
                entities: response.data?.entities,
                ...inboundData
            });
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while fetching source metrics'),
                type: 'error'
            });
        }).finally(() => {
            setLoading(false);
        })
    }, [addNotification]);

    if (loading) {
        return <Loading />
    }

    return <div className="grid grid-cols-4 gap-4 mb-4">
        <div className="stat place-items-center shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-primary hidden xl:block">
                <IconTimelineEventPlus size={64} />
            </div>
            <div className="stat-title">Events Ingested</div>
            <div className="stat-value text-primary">{metrics.inboundTotal}</div>
            <div className="stat-desc">last 24 hours</div>
        </div>
        <div className="stat place-items-center shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-success  hidden xl:block">
                <IconCircleCheck size={64} />
            </div>
            <div className="stat-title">Events Processed</div>
            <div className="stat-value text-success">{metrics.inboundProcessed}</div>
            <div className="stat-desc">last 24 hours</div>
        </div>
        <div className="stat place-items-center shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-primary hidden xl:block">
                <div className="radial-progress text-primary" style={{ "--value": metrics.inboundPercentComplete, "--size": "4.2rem" }}>{`${metrics.inboundPercentComplete}%`}</div>
            </div>
            <div className="stat-title ">Events processed</div>
            <div className="stat-value text-base-content">{`${metrics.inboundPercentComplete}%`}</div>
            <div className="stat-desc">{`${pluralize('event', metrics.inboundTotal - metrics.inboundProcessed, true)} remaining`}</div>
        </div>
        <div className="stat place-items-center shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure  hidden xl:block">
                <IconCloudComputing size={64} />
            </div>
            <div className="stat-title">Pipelines</div>
            <div className="stat-value">{metrics.entities.pipelines}</div>
            <div className="stat-desc">Current</div>
        </div>
    </div>
}

export default SourceMetrics;