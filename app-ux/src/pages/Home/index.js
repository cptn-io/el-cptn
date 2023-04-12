import axios from "axios";
import { Fragment, useEffect, useState } from "react";
import Loading from "../../components/Loading";
import PageTitle from "../../components/Nav/PageTitle";
import useNotifications from "../../hooks/useNotifications";
import get from 'lodash/get';
import { IconCloudComputing, IconDatabaseExport, IconDatabaseImport, IconTransform } from '@tabler/icons-react';
import { processInboundMetrics, processOutboundMetrics } from "../../common/metricHelpers";
import IntervalSelector from "../../components/IntervalSelector";
import { PipelineMetricsRenderer, SourceMetricsRenderer } from "../../components/Metrics";


const Home = () => {
    const { addNotification } = useNotifications();
    const [loading, setLoading] = useState(true);
    const [interval, setInterval] = useState(1440);
    const [refreshing, setRefreshing] = useState(false);
    const [data, setData] = useState(null);

    useEffect(() => {
        setRefreshing(true);
        axios.get(`/api/dashboard/metrics?interval=${interval}`).then(response => {
            const inboundMetrics = processInboundMetrics(response.data);
            const outboundMetrics = processOutboundMetrics(response.data);

            setData({
                entities: response.data?.entities,
                inboundMetrics,
                outboundMetrics
            });
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while fetching dashboard metrics'),
                type: 'error'
            });
        }).finally(() => {
            setLoading(false);
            setRefreshing(false);
        })
    }, [addNotification, interval]);

    if (loading) {
        return <Loading />
    }

    return <Fragment>
        <PageTitle itemKey="home" />
        <div className="grid grid-cols-4 gap-2 mb-4">
            <div className="stats shadow bg-primary text-primary-content col-span-2 md:col-span-1 rounded-2xl">
                <div className="stat text-center">
                    <div className="flex justify-center my-4"><IconCloudComputing size={64} /></div>
                    <div className="stat-title text-primary-content">Data Pipelines</div>
                    <div className="stat-value text-6xl">{data?.entities?.pipelines || 0}</div>
                </div>
            </div>
            <div className="stats shadow bg-secondary text-secondary-content col-span-2 md:col-span-1 rounded-2xl">
                <div className="stat text-center">
                    <div className="flex justify-center my-4"><IconDatabaseExport size={64} /></div>
                    <div className="stat-title text-secondary-content">Data Sources</div>
                    <div className="stat-value text-6xl">{data?.entities?.sources || 0}</div>
                </div>
            </div>
            <div className="stats shadow bg-info text-info-content col-span-2 md:col-span-1 rounded-2xl">
                <div className="stat text-center">
                    <div className="flex justify-center my-4"><IconDatabaseImport size={64} /></div>
                    <div className="stat-title text-info-content">Data Destinations</div>
                    <div className="stat-value text-6xl">{data?.entities?.destinations || 0}</div>
                </div>
            </div>
            <div className="stats shadow bg-accent text-accent-content  col-span-2 md:col-span-1 rounded-2xl">
                <div className="stat text-center">
                    <div className="flex justify-center my-4"><IconTransform size={64} /></div>
                    <div className="stat-title text-accent-content">Transformations</div>
                    <div className="stat-value text-6xl">{data?.entities?.transformations || 0}</div>
                </div>
            </div>
        </div>
        <div className="flex flex-row-reverse">
            <IntervalSelector interval={interval} setInterval={setInterval} />
        </div>
        <div className="bg-base-200 p-3 rounded-box mb-4 shadow">
            <div className="text-xl font-extrabold mb-4">Inbound events</div>
            <SourceMetricsRenderer metrics={data.inboundMetrics} interval={interval} refreshing={refreshing} />
            <div className="text-xl font-extrabold mt-4 mb-4">Outbound events</div>
            <PipelineMetricsRenderer metrics={data.outboundMetrics} interval={interval} refreshing={refreshing} />
        </div>
    </Fragment>
}

export default Home;