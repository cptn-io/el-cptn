import { Fragment, useEffect, useState } from "react";
import PageTitle from "../../components/Nav/PageTitle";
import { IconCirclePlus, IconCheck, IconX } from '@tabler/icons-react';
import { Link, useSearchParams } from "react-router-dom";
import axios from "axios";
import get from "lodash/get";
import useNotifications from "../../hooks/useNotifications";
import Pagination from "../../components/Pagination";
import Loading from "../../components/Loading";
import SourceFilter from "./SourceFilter";
import useStatusFilter from "../../hooks/useStatusFilter";


const Sources = () => {
    const { addNotification } = useNotifications();
    const [searchParams] = useSearchParams();
    const [page, setPage] = useState(searchParams.has('page') ? searchParams.get('page') * 1 : 0);
    const [status, setStatus] = useState(searchParams.has('status') ? searchParams.get('status') : null);

    const [loading, setLoading] = useState(true);
    const [data, setData] = useState([]);
    const [totalCount, setTotalCount] = useState(0);


    useEffect(() => {
        setPage(searchParams.has('page') ? searchParams.get('page') * 1 : 0);
    }, [searchParams]);

    useStatusFilter({ status });

    useEffect(() => {
        let url = `/api/source?page=${page}`;
        const filters = [];
        if (status) {
            filters.push(`state:eq:${status}`);
        }

        if (filters.length > 0) {
            url += `&filters=${filters.join()}`;
        }

        axios.get(url).then(response => {
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
    }, [page, status, addNotification]);

    return <Fragment>
        <PageTitle itemKey="sources">
            <Link to="/sources/new" className="btn btn-primary btn-sm md:btn-md"><IconCirclePlus size={24} className="mr-2" />New Source</Link>
        </PageTitle>
        {loading ? <Loading /> : <div className="overflow-x-auto">
            <SourceFilter status={status} setStatus={setStatus} />
            <div className="table-container">
                {totalCount > 0 ? <table className="table data-table table-zebra w-full">
                    <thead>
                        <tr>
                            <th className="bg-base-300 text-left w-1/2 md:w-2/3">Name</th>
                            <th className="bg-base-300 text-center">Secured</th>
                            <th className="bg-base-300 text-center">Active</th>
                        </tr>
                    </thead>
                    <tbody style={{ maxHeight: '600px' }}>
                        {data.map(source => <tr key={source.id}>
                            <td className="whitespace-pre-wrap break-words"><Link className="link link-hover" to={`/sources/${source.id}`}>{source.name}</Link></td>
                            <td className="text-center"><div className="flex justify-center">{source.secured ? <IconCheck className="text-success" size={24} /> : <IconX className="text-error" size={24} />}</div></td>
                            <td className="text-center"><div className="flex justify-center">{source.active ? <IconCheck className="text-success" size={24} /> : <IconX className="text-error" size={24} />}</div></td>
                        </tr>)}
                    </tbody>
                </table> : status ? renderNoRecords() : renderNoSources()}
            </div>
            {totalCount > 0 && <Pagination totalCount={totalCount} />}
        </div>
        }
    </Fragment>
}

const renderNoSources = () => {
    return <div className="flex flex-col justify-center my-5">
        <div className="flex justify-center mb-4 text-primary">
            <img alt="Create new Source" src="/undraw/road_sign.svg" className="w-3/6 max-w-3/5" />
        </div>
        <div className="flex justify-center mb-4 text-center">
            There are no Sources. Click the button below to add a new Data Source
        </div>
        <div className="flex justify-center">
            <Link to="/sources/new" className="btn btn-md md:btn-lg"><IconCirclePlus size={36} className="mr-2" />New Source</Link>
        </div>
    </div>
}

const renderNoRecords = () => {
    return <div className="flex flex-col justify-center my-5">
        <div className="flex justify-center mb-4 text-primary">
            <img alt="No matched event" src="/undraw/void.svg" className="w-3/6 max-w-3/5" />
        </div>
        <div className="flex justify-center mb-4 text-center">
            There are no events matching status.
        </div>
    </div>
}

export const breadcrumbs = [{ label: 'Sources', url: '/sources' }];

export default Sources;