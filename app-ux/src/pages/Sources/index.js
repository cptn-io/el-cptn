import { Fragment, useEffect, useState } from "react";
import PageTitle from "../../components/Nav/PageTitle";
import { IconCirclePlus, IconCheck, IconX } from '@tabler/icons-react';
import { Link, useSearchParams } from "react-router-dom";
import axios from "axios";
import get from "lodash/get";
import useNotifications from "../../hooks/useNotifications";
import Pagination from "../../components/Pagination";
import Loading from "../../components/Loading";


const Sources = () => {
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
        axios.get(`/api/source?page=${page}`).then(response => {
            setTotalCount(response.headers['x-total-count'] || 0);
            setData(response.data);
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while fetching Sources'),
                type: 'error'
            });
        }).finally(() => {
            setLoading(false);
        });
    }, [page, addNotification]);

    return <Fragment>
        <PageTitle itemKey="sources">
            <Link to="/sources/new" className="btn btn-primary btn-sm md:btn-md"><IconCirclePlus size={24} className="mr-2" />New Source</Link>
        </PageTitle>
        {loading ? <Loading /> : <div className="overflow-x-auto">
            <div className="table-container">
                <table className="table data-table table-zebra w-full">
                    <thead>
                        <tr>
                            <th className="bg-base-300 text-left w-1/2 md:w-2/3">Name</th>
                            <th className="bg-base-300 text-center">Secured</th>
                            <th className="bg-base-300 text-center">Active</th>
                        </tr>
                    </thead>
                    <tbody style={{ maxHeight: '600px' }}>
                        {data.map(source => <tr key={source.id}>
                            <td className="whitespace-pre-wrap break-words"><Link to={`/sources/${source.id}`}>{source.name}</Link></td>
                            <td className="text-center"><div className="flex justify-center">{source.secured ? <IconCheck className="text-success" size={24} /> : <IconX className="text-error" size={24} />}</div></td>
                            <td className="text-center"><div className="flex justify-center">{source.active ? <IconCheck className="text-success" size={24} /> : <IconX className="text-error" size={24} />}</div></td>
                        </tr>)}
                    </tbody>
                </table>
            </div>
            <Pagination totalCount={totalCount} />
        </div>
        }
    </Fragment>
}

export const breadcrumbs = [{ label: 'Sources', url: '/sources' }];

export default Sources;