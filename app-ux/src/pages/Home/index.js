import axios from "axios";
import { Fragment, useEffect, useState } from "react";
import Loading, { Refreshing } from "../../components/Loading";
import PageTitle from "../../components/Nav/PageTitle";
import useNotifications from "../../hooks/useNotifications";
import find from 'lodash/find';
import get from 'lodash/get';
import pluralize from "pluralize";
import { IconTimelineEventExclamation, IconTimelineEventPlus, IconCircleCheck, IconCloudComputing, IconDatabaseExport, IconDatabaseImport, IconTransform, IconRotateClockwise, IconFidgetSpinner, IconRefresh } from '@tabler/icons-react';
import { processInboundMetrics } from "../../common/metricHelpers";
import IntervalSelector from "../../components/IntervalSelector";
import { parseInterval } from "../../components/IntervalSelector";


const Home = () => {
    const { addNotification } = useNotifications();
    const [loading, setLoading] = useState(true);
    const [interval, setInterval] = useState(1440);
    const [refreshing, setRefreshing] = useState(false);
    const [metrics, setMetrics] = useState(null);

    useEffect(() => {
        setRefreshing(true);
        axios.get(`/api/dashboard/metrics?interval=${interval}`).then(response => {
            const inboundMetrics = processInboundMetrics(response.data);

            setMetrics({
                entities: response.data?.entities,
                ...inboundMetrics
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
                    <div className="stat-value text-6xl">{metrics.entities.pipelines}</div>
                </div>
            </div>
            <div className="stats shadow bg-secondary text-secondary-content col-span-2 md:col-span-1 rounded-2xl">
                <div className="stat text-center">
                    <div className="flex justify-center my-4"><IconDatabaseExport size={64} /></div>
                    <div className="stat-title text-secondary-content">Data Sources</div>
                    <div className="stat-value text-6xl">{metrics.entities.sources}</div>
                </div>
            </div>
            <div className="stats shadow bg-info text-info-content col-span-2 md:col-span-1 rounded-2xl">
                <div className="stat text-center">
                    <div className="flex justify-center my-4"><IconDatabaseImport size={64} /></div>
                    <div className="stat-title text-info-content">Data Destinations</div>
                    <div className="stat-value text-6xl">{metrics.entities.destinations}</div>
                </div>
            </div>
            <div className="stats shadow bg-accent text-accent-content  col-span-2 md:col-span-1 rounded-2xl">
                <div className="stat text-center">
                    <div className="flex justify-center my-4"><IconTransform size={64} /></div>
                    <div className="stat-title text-accent-content">Transformations</div>
                    <div className="stat-value text-6xl">{0}</div>
                </div>
            </div>
        </div>
        <div className="flex flex-row-reverse">
            <IntervalSelector interval={interval} setInterval={setInterval} />
        </div>
        <div className="bg-base-200 p-3 rounded-box mb-4 shadow">
            <div className="text-xl font-extrabold mb-4">Source metrics</div>
            <div className="grid grid-cols-4 gap-2">
                <div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
                    <div className="stat-figure text-primary hidden xl:block">
                        <IconTimelineEventPlus size={64} />
                    </div>
                    <div className="stat-title ">Events Received</div>
                    <div className="stat-value text-base-content">{refreshing ? <Refreshing /> : metrics.inboundTotal}</div>
                    <div className="stat-desc">{parseInterval(interval)}</div>
                </div>
                <div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
                    <div className="stat-figure text-success hidden xl:block">
                        <IconCircleCheck size={64} />
                    </div>
                    <div className="stat-title">Events Processed</div>
                    <div className="stat-value text-base-content">{refreshing ? <Refreshing /> : metrics.inboundProcessed}</div>
                    <div className="stat-desc">{parseInterval(interval)}</div>
                </div>
                <div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
                    <div className="stat-figure text-primary hidden xl:block">
                        {!refreshing && <div className="radial-progress text-primary" style={{ "--value": metrics.inboundPercentComplete, "--size": "4.2rem" }}>{metrics.inboundPercentComplete}%</div>}
                    </div>
                    <div className="stat-title ">Events processed</div>
                    <div className="stat-value text-base-content">{refreshing ? <Refreshing /> : `${metrics.inboundPercentComplete}%`}</div>
                    <div className="stat-desc">{!refreshing ? `${pluralize('event', metrics.inboundTotal - metrics.inboundProcessed, true)} remaining` : ''}</div>
                </div>
                <div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
                    <div className="stat-figure text-error hidden xl:block">
                        <IconTimelineEventExclamation size={64} />
                    </div>
                    <div className="stat-title">Events Failed</div>
                    <div className="stat-value text-base-content">{refreshing ? <Refreshing /> : metrics.inboundFailed}</div>
                    <div className="stat-desc">{parseInterval(interval)}</div>
                </div>
            </div>
        </div>



    </Fragment>
}

export default Home;