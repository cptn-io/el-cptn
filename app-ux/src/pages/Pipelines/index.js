import { Fragment, useEffect, useState } from "react";
import PageTitle from "../../components/Nav/PageTitle";
import { IconCirclePlus, IconCheck, IconX } from '@tabler/icons-react';
import { Link, useSearchParams } from "react-router-dom";
import axios from "axios";
import get from "lodash/get";
import useNotifications from "../../hooks/useNotifications";
import Loading from "../../components/Loading";
import Pagination from "../../components/Pagination";

const Pipelines = () => {

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
        axios.get(`/api/pipeline?page=${page}`).then(response => {
            setTotalCount(response.headers['x-total-count'] || 0);
            setData(response.data);
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while fetching Pipelines'),
                type: 'error'
            });
        }).finally(() => {
            setLoading(false);
        });
    }, [page, addNotification]);

    return <Fragment>
        <PageTitle itemKey="pipelines">
            <Link to="/pipelines/new" className="btn btn-primary btn-sm md:btn-md"><IconCirclePlus size={24} className="mr-2" />New Pipeline</Link>
        </PageTitle>
        {loading ? <Loading /> : <div className="overflow-x-auto">
            <div className="table-container">
                <table className="table data-table table-zebra w-full">
                    <thead>
                        <tr>
                            <th className="bg-base-300 text-left w-1/3">Name</th>
                            <th className="bg-base-300 text-left w-1/4">Source</th>
                            <th className="bg-base-300 text-left w-1/4">Destination</th>
                            <th className="bg-base-300 text-center">Active</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.map(pipeline => <tr key={pipeline.id}>
                            <td className="whitespace-pre-wrap break-words">{pipeline.name}</td>
                            <td className="whitespace-pre-wrap break-words">{pipeline.source.name}</td>
                            <td className="whitespace-pre-wrap break-words">{pipeline.destination.name}</td>
                            <td className="text-center"><div className="flex justify-center">{pipeline.active ? <IconCheck size={24} /> : <IconX size={24} />}</div></td>
                        </tr>)}
                    </tbody>
                </table>
            </div>
            <Pagination totalCount={totalCount} />
        </div>
        }
    </Fragment>
}

export const breadcrumbs = [{ label: 'Pipelines', url: '/pipelines' }];

export default Pipelines;