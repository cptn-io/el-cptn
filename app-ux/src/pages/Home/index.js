import axios from "axios";
import { Fragment, useEffect, useState } from "react";
import Loading from "../../components/Loading";
import PageTitle from "../../components/Nav/PageTitle";
import useNotifications from "../../hooks/useNotifications";
import find from 'lodash/find';
import get from 'lodash/get';
import pluralize from "pluralize";
import { IconTimelineEventExclamation, IconTimelineEventPlus, IconCircleCheck, IconCloudComputing, IconDatabaseExport, IconDatabaseImport, IconTransform } from '@tabler/icons-react';

const Home = () => {
    const { addNotification } = useNotifications();
    const [loading, setLoading] = useState(true);

    const [metrics, setMetrics] = useState(null);

    useEffect(() => {
        axios.get('/api/dashboard/metrics').then(response => {
            const inboundTotal = response.data.inbound.reduce((sum, status) => sum + status.count, 0);
            const inboundProcessed = find(response.data.inbound, { 'state': 'COMPLETED' })?.count || 0;
            const inboundPercentComplete = inboundTotal > 0 ? Math.floor(inboundProcessed / inboundTotal * 100) : 100;
            const inboundFailed = find(response.data.inbound, { 'state': 'FAILED' })?.count || 0;

            setMetrics({
                entities: response.data?.entities,
                inboundTotal,
                inboundProcessed,
                inboundPercentComplete,
                inboundFailed
            });
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while fetching dashboard metrics'),
                type: 'error'
            });
        }).finally(() => {
            setLoading(false);
        })
    }, [addNotification]);

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
        <div className="bg-base-200 p-3 rounded-box mb-4 shadow">
            <div className="text-xl font-extrabold mb-4">Source metrics</div>
            <div className="grid grid-cols-4 gap-2">
                <div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
                    <div className="stat-figure text-primary hidden xl:block">
                        <IconTimelineEventPlus size={64} />
                    </div>
                    <div className="stat-title ">Events Received</div>
                    <div className="stat-value text-base-content">{metrics.inboundTotal}</div>
                    <div className="stat-desc">Last 24 hours</div>
                </div>
                <div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
                    <div className="stat-figure text-success hidden xl:block">
                        <IconCircleCheck size={64} />
                    </div>
                    <div className="stat-title">Events Processed</div>
                    <div className="stat-value text-base-content">{metrics.inboundProcessed}</div>
                    <div className="stat-desc">Last 24 hours</div>
                </div>
                <div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
                    <div className="stat-figure text-primary hidden xl:block">
                        <div className="radial-progress text-primary" style={{ "--value": metrics.inboundPercentComplete, "--size": "4.2rem" }}>{`${metrics.inboundPercentComplete}%`}</div>
                    </div>
                    <div className="stat-title ">Events processed</div>
                    <div className="stat-value text-base-content">{`${metrics.inboundPercentComplete}%`}</div>
                    <div className="stat-desc">{`${pluralize('event', metrics.inboundTotal - metrics.inboundProcessed, true)} remaining`}</div>
                </div>
                <div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
                    <div className="stat-figure text-error hidden xl:block">
                        <IconTimelineEventExclamation size={64} />
                    </div>
                    <div className="stat-title">Events Failed</div>
                    <div className="stat-value text-base-content">{metrics.inboundFailed}</div>
                    <div className="stat-desc">Last 24 hours</div>
                </div>
            </div>
        </div>



    </Fragment>
}

export default Home;