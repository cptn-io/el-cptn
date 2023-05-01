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
import ContextHelp from "../../../components/ContextHelp";

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
                <ContextHelp page='user-details' />
            </div>
        </div>
    </div>

}

export default UserDetails;