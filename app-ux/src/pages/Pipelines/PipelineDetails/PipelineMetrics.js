import { useEffect, useState } from "react";
import useNotifications from "../../../hooks/useNotifications";
import { processOutboundMetrics } from '../../../common/metricHelpers';
import Loading from "../../../components/Loading";
import axios from "axios";
import get from 'lodash/get';
import IntervalSelector from "../../../components/IntervalSelector";
import { PipelineMetricsRenderer } from "../../../components/Metrics";

const PipelineMetrics = ({ pipelineId }) => {
    const { addNotification } = useNotifications();
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);
    const [interval, setInterval] = useState(1440);
    const [data, setData] = useState(null);

    useEffect(() => {
        setRefreshing(true);
        axios.get(`/api/dashboard/pipeline/${pipelineId}/metrics?interval=${interval}`).then(response => {
            const outboundMetrics = processOutboundMetrics(response.data);
            setData({
                entities: response.data?.entities,
                outboundMetrics
            });
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while fetching pipeline metrics'),
                type: 'error'
            });
        }).finally(() => {
            setLoading(false);
            setRefreshing(false);
        })
    }, [addNotification, pipelineId, interval]);

    if (loading) {
        return <Loading />
    }

    return <div className="mb-4">
        <div className="flex flex-row-reverse">
            <IntervalSelector interval={interval} setInterval={setInterval} />
        </div>
        <PipelineMetricsRenderer metrics={data.outboundMetrics} interval={interval} refreshing={refreshing} />
    </div>
}

export default PipelineMetrics;