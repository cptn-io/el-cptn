import axios from 'axios';
import { useEffect, useState } from 'react';
import Loading from '../../../components/Loading';
import useNotifications from '../../../hooks/useNotifications';
import get from 'lodash/get';
import { processInboundMetrics } from '../../../common/metricHelpers';
import { SourceMetricsRenderer } from '../../../components/Metrics';
import IntervalSelector from '../../../components/IntervalSelector';

const SourceMetrics = (props) => {
    const { sourceId } = props;
    const { addNotification } = useNotifications();
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);
    const [interval, setInterval] = useState(1440);
    const [data, setData] = useState(null);

    useEffect(() => {
        setRefreshing(true);
        axios.get(`/api/dashboard/source/${sourceId}/metrics?interval=${interval}`).then(response => {
            const inboundMetrics = processInboundMetrics(response.data);
            setData({
                entities: response.data?.entities,
                inboundMetrics
            });
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while fetching source metrics'),
                type: 'error'
            });
        }).finally(() => {
            setLoading(false);
            setRefreshing(false);
        })
    }, [addNotification, sourceId, interval]);

    if (loading) {
        return <Loading />
    }

    return <div className="mb-4">
        <div className="flex flex-row-reverse">
            <IntervalSelector interval={interval} setInterval={setInterval} />
        </div>
        <SourceMetricsRenderer metrics={data.inboundMetrics} interval={interval} refreshing={refreshing} />
    </div>

}

export default SourceMetrics;