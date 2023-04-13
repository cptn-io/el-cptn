import { useNavigate, useParams } from "react-router-dom";
import { breadcrumbs } from "..";
import PageTitle from "../../../components/Nav/PageTitle";
import { useEffect, useState } from "react";
import axios from "axios";
import Loading from "../../../components/Loading";
import useNotifications from "../../../hooks/useNotifications";
import get from 'lodash/get';
import UserDetailsCard from "./UserDetailsCard";
import UserPasswordCard from "./UserPasswordCard";

const UserDetails = () => {
    const navigate = useNavigate();
    const { addNotification } = useNotifications();
    const { id } = useParams();
    const [loading, setLoading] = useState(true);
    const [data, setData] = useState(null);

    useEffect(() => {
        axios.get(`/api/user/${id}`).then(response => {
            setData(response.data);
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while fetching User'),
                type: 'error'
            });
            navigate('/users');
        }).finally(() => {
            setLoading(false);
        })
    }, [id, addNotification, navigate]);

    if (loading) {
        return <Loading />
    }

    return <div>
        <PageTitle itemKey="users" label={data.email} breadcrumbs={breadcrumbs} />
        <div className="grid grid-flow-row-dense grid-cols-1 xl:grid-cols-8 gap-4 ">
            <div className="xl:col-span-6">
                <UserDetailsCard data={data} onUpdate={setData} />
                <UserPasswordCard data={data} />
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
    </div>

}

export default UserDetails;