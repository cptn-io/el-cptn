import axios from "axios";
import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import useNotifications from "../../../hooks/useNotifications";
import get from 'lodash/get';
import PipelineList from "../../Pipelines/PipelineList";
import Loading from "../../../components/Loading";

const TransformationPipelines = (props) => {
    const { transformationId } = props;
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
        axios.get(`/api/transformation/${transformationId}/pipeline?page=${page}`).then(response => {
            setTotalCount(response.headers['x-total-count'] || 0);
            setData(response.data);
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while fetching Transformation related Pipelines'),
                type: 'error'
            });
        }).finally(() => {
            setLoading(false);
        });
    }, [page, addNotification, transformationId]);

    if (loading) {
        return <Loading />
    }
    return <div className="p-4">
        <div className="mb-4 text-lg font-bold">Related Pipelines</div>
        <PipelineList data={data} totalCount={totalCount} isRelatedList={true} />
    </div>
}

export default TransformationPipelines;