import { useNavigate, useParams } from "react-router-dom";
import PageTitle from "../../../components/Nav/PageTitle";
import { breadcrumbs } from "..";
import axios from "axios";
import Loading from "../../../components/Loading";
import { useEffect, useState } from "react";
import useNotifications from "../../../hooks/useNotifications";
import get from 'lodash/get';
import { IconCheck, IconX } from '@tabler/icons-react';
import SetupInstructions from "./SetupInstructions";

const SourceDetails = (props) => {
    const navigate = useNavigate();
    const { addNotification } = useNotifications();
    const { id } = useParams();
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

    return <div>
        <PageTitle itemKey="sources" label="Source Details" breadcrumbs={breadcrumbs} />
        <div className="grid grid-flow-row-dense grid-cols-1 md:grid-cols-8 gap-4 ">
            <div className="md:col-span-6">
                <div className="card bg-base-100 shadow">
                    <div className="card-body">
                        <h2 className="card-title">Basic information</h2>
                        <div className="form-control w-full">
                            <label className="label">
                                <span className="label-text">Source Name</span>
                            </label>
                            <div className="label text-lg">{data.name}</div>
                        </div>
                        <div className="flex">
                            <div className="form-control w-6/12">
                                <label className="label">
                                    <span className="label-text">Secured</span>
                                </label>
                                <div className="label">{data.secured ? <IconCheck size={24} /> : <IconX size={24} />}</div>
                            </div>
                            <div className="form-control w-6/12">
                                <label className="label">
                                    <span className="label-text">Active</span>
                                </label>
                                <div className="label">{data.active ? <IconCheck size={24} /> : <IconX size={24} />}</div>
                            </div>
                        </div>
                    </div>
                </div>
                <SetupInstructions data={data} />
            </div>
            <div className="md:col-span-2">
                <div className="stats stats-vertical shadow w-full">

                    <div className="stat">
                        <div className="stat-title">Events Received</div>
                        <div className="stat-value text-primary">31K</div>
                        <div className="stat-desc">Jan 1st - Feb 1st</div>
                    </div>

                    <div className="stat">
                        <div className="stat-title">Events Pending</div>
                        <div className="stat-value text-primary">1K</div>
                        <div className="stat-desc">↗︎ 400 (22%)</div>
                    </div>

                    <div className="stat">
                        <div className="stat-title">Events Failed</div>
                        <div className="stat-value text-error">12</div>
                        <div className="stat-desc">↘︎ 90 (14%)</div>
                    </div>

                </div>
            </div>
        </div>
    </div>
}

export default SourceDetails;