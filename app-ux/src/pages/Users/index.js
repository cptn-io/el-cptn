import { IconUserPlus } from "@tabler/icons-react";
import PageTitle from "../../components/Nav/PageTitle";
import { Link, useSearchParams } from "react-router-dom";
import useNotifications from "../../hooks/useNotifications";
import { useEffect, useState } from "react";
import axios from "axios";
import get from "lodash/get";
import Loading from "../../components/Loading";
import Pagination from "../../components/Pagination";
import moment from "moment-timezone";

const Users = () => {
    const { addNotification } = useNotifications();
    const [searchParams] = useSearchParams();
    const [page, setPage] = useState(searchParams.has('page') ? searchParams.get('page') * 1 : 0);

    const [loading, setLoading] = useState(true);
    const [data, setData] = useState([]);
    const [totalCount, setTotalCount] = useState(0);

    useEffect(() => {
        setPage(searchParams.has('page') ? searchParams.get('page') * 1 : 0);
    }, [searchParams]);

    useEffect(() => {
        axios.get(`/api/user?page=${page}`).then(response => {
            setTotalCount(response.headers['x-total-count'] || 0);
            setData(response.data);
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while fetching Users'),
                type: 'error'
            });
        }).finally(() => {
            setLoading(false);
        });
    }, [page, addNotification]);

    return <>
        <PageTitle itemKey="users">
            <Link to="/users/new" className="btn btn-primary btn-sm md:btn-md"><IconUserPlus size={24} className="mr-2" />New User</Link>
        </PageTitle>
        {loading ? <Loading /> : <div className="overflow-x-auto">
            <div className="table-container">
                {totalCount > 0 ? <table className="table data-table table-zebra w-full">
                    <thead>
                        <tr>
                            <th className={`bg-base-300 text-left w-3/12`}>Email</th>
                            <th className={`bg-base-300 text-left w-3/12`}>First Name</th>
                            <th className={`bg-base-300 text-left w-3/12`}>Last Name</th>
                            <th className={`bg-base-300 text-left w-3/12`}>Last Login</th>
                            <th className={`bg-base-300 text-center w-2/12`}>Locked Out</th>
                            <th className={`bg-base-300 text-center w-2/12`}>Disabled</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.map(user => <tr key={user.id}>
                            <td className="whitespace-pre-wrap break-words"><Link className="link link-hover" to={`/users/${user.id}`}>{user.email}</Link></td>
                            <td className="whitespace-pre-wrap break-words">{user.firstName}</td>
                            <td className="whitespace-pre-wrap break-words">{user.lastName}</td>
                            <td className="whitespace-pre-wrap break-words">{user.lastLoginAt ? moment(user.lastLoginAt).format('lll') : 'Never'}</td>
                            <td className="text-center"><div className="flex justify-center">{user.lockedOut ? 'Yes' : 'No'}</div></td>
                            <td className="text-center"><div className="flex justify-center">{user.disabled ? 'Yes' : 'No'}</div></td>
                        </tr>)}
                    </tbody>
                </table> : renderNoUsers()}
            </div>
            {totalCount > 0 && <Pagination totalCount={totalCount} />}
        </div>
        }
    </>
}


const renderNoUsers = () => {
    return <div className="flex flex-col justify-center my-5">
        <div className="flex justify-center mb-4 text-primary">
            <img alt="Create new Transformation" src="/undraw/coding.svg" className="w-3/6 max-w-3/5" />
        </div>
        <div className="flex justify-center mb-4 text-center">
            There are no Users. Click the button below to add a new User
        </div>
        <div className="flex justify-center">
            <Link to="/users/new" className="btn btn-md md:btn-lg"><IconUserPlus size={36} className="mr-2" />New User</Link>
        </div>
    </div>
}

export const breadcrumbs = [{ label: 'Users', url: '/users' }];

export default Users;