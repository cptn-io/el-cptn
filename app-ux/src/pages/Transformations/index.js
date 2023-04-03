import { Fragment, useEffect, useState } from "react";
import PageTitle from "../../components/Nav/PageTitle";
import { IconCirclePlus, IconCheck, IconX } from '@tabler/icons-react';
import { Link, useSearchParams } from "react-router-dom";
import axios from "axios";
import get from "lodash/get";
import useNotifications from "../../hooks/useNotifications";
import Pagination from "../../components/Pagination";
import Loading from "../../components/Loading";

const Transformations = () => {
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
        axios.get(`/api/transformation?page=${page}`).then(response => {
            setTotalCount(response.headers['x-total-count'] || 0);
            setData(response.data);
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while fetching Transformations'),
                type: 'error'
            });
        }).finally(() => {
            setLoading(false);
        });
    }, [page, addNotification]);

    return <Fragment>
        <PageTitle itemKey="transformations">
            <Link to="/transformations/new" className="btn btn-primary btn-sm md:btn-md"><IconCirclePlus size={24} className="mr-2" />New Transformation</Link>
        </PageTitle>
        {loading ? <Loading /> : <div className="overflow-x-auto">
            <div className="table-container">
                {totalCount > 0 ? <table className="table data-table table-zebra w-full">
                    <thead>
                        <tr>
                            <th className="bg-base-300 text-left w-2/3">Name</th>
                            <th className="bg-base-300 text-center">Active</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.map(transformation => <tr key={transformation.id}>
                            <td className="whitespace-pre-wrap break-words"><Link to={`/transformations/${transformation.id}`}>{transformation.name}</Link></td>
                            <td className="text-center"><div className="flex justify-center">{transformation.active ? <IconCheck className="text-success" size={24} /> : <IconX className="text-error" size={24} />}</div></td>
                        </tr>)}
                    </tbody>
                </table> : renderNoTransformations()}
            </div>
            {totalCount > 0 && <Pagination totalCount={totalCount} />}
        </div>
        }
    </Fragment>
}

const renderNoTransformations = () => {
    return <div className="flex flex-col justify-center my-5">
        <div className="flex justify-center mb-4 text-primary">
            <img alt="Create new Transformation" src="/undraw/coding.svg" className="w-3/6 max-w-3/5" />
        </div>
        <div className="flex justify-center mb-4 text-center">
            There are no Transformations. Click the button below to add a new Transformation
        </div>
        <div className="flex justify-center">
            <Link to="/transformations/new" className="btn btn-md md:btn-lg"><IconCirclePlus size={36} className="mr-2" />New Transformation</Link>
        </div>
    </div>
}


export const breadcrumbs = [{ label: 'Transformations', url: '/transformations' }];

export default Transformations;