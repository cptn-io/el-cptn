import { Fragment, useEffect, useState } from "react";
import PageTitle from "../../components/Nav/PageTitle";
import { IconCirclePlus } from '@tabler/icons-react';
import { Link, useSearchParams } from "react-router-dom";
import axios from "axios";
import get from "lodash/get";
import useNotifications from "../../hooks/useNotifications";
import Loading from "../../components/Loading";

import PipelineList from "./PipelineList";
import PipelineFilter from "./PipelineFilter";
import useStatusFilter from "../../hooks/useStatusFilter";

const Pipelines = () => {

    const { addNotification } = useNotifications();
    const [searchParams] = useSearchParams();
    const [page, setPage] = useState(searchParams.has('page') ? searchParams.get('page') * 1 : 0);
    const [status, setStatus] = useState(searchParams.has('status') ? searchParams.get('status') : null);

    useStatusFilter({ status });

    const [loading, setLoading] = useState(true);
    const [data, setData] = useState([]);
    const [totalCount, setTotalCount] = useState(0);

    useEffect(() => {
        setPage(searchParams.has('page') ? searchParams.get('page') * 1 : 0);
    }, [searchParams]);

    useEffect(() => {
        let url = `/api/pipeline?page=${page}`;
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
                message: get(err, 'response.data.message', 'An error occurred while fetching Pipelines'),
                type: 'error'
            });
        }).finally(() => {
            setLoading(false);
        });
    }, [page, status, addNotification]);

    return <Fragment>
        <PageTitle itemKey="pipelines">
            <Link to="/pipelines/new" className="btn btn-primary btn-sm md:btn-md"><IconCirclePlus size={24} className="mr-2" />New Pipeline</Link>
        </PageTitle>
        {loading ? <Loading /> : <div className="overflow-x-auto">
            <PipelineFilter status={status} setStatus={setStatus} />
            <PipelineList data={data} totalCount={totalCount} />
        </div>
        }
    </Fragment>
}

export const breadcrumbs = [{ label: 'Pipelines', url: '/pipelines' }];

export default Pipelines;