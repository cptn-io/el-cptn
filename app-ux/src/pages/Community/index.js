import { useNavigate, useSearchParams } from "react-router-dom";
import PageTitle from "../../components/Nav/PageTitle";
import { IconBox } from "@tabler/icons-react";
import axios from "axios";
import useNotifications from "../../hooks/useNotifications";
import { useCallback, useEffect, useState } from "react";
import get from "lodash/get";
import Pagination from "../../components/Pagination";
import Loading from "../../components/Loading";
import ConfirmModal from "../../components/ConfirmModal";

const App = ({ data, setContextApp }) => <div className="card bg-base-100 border-solid border-x border-y border-base-200 shadow col-span-2 md:col-span-1 rounded-2xl">
    <figure className="py-4" style={{ height: '160px', overflow: 'hidden' }}>{data.logoUrl ? <img src={data.logoUrl} height={128} width={128} alt={data.name} /> : <IconBox className="text-base-300" size={128} />}</figure>
    <div className="card-body bg-base-200 p-4 flex flex-col justify-between">
        <h2 className="card-title">{data.name}</h2>
        <div className="card-actions mt-4 justify-end">
            <button onClick={() => setContextApp(data)} className="btn btn-primary">{data.type === 'DESTINATION' ? `New Destination` : `New Transformation`}</button>
        </div>
    </div>
</div>

const getConfirmationMessage = (app) => {
    if (app.type === 'DESTINATION') {
        return {
            title: 'Create Destination',
            message: `Are you sure you want to create a new Destination from ${app.name}?`
        }
    } else {
        return {
            title: 'Create Transformation',
            message: `Are you sure you want to create a new Transformation from ${app.name}?`
        }
    }
}

const Community = () => {
    const { addNotification } = useNotifications();
    const navigate = useNavigate();

    const [searchParams] = useSearchParams();
    const [page, setPage] = useState(searchParams.has('page') ? searchParams.get('page') * 1 : 0);

    const [loading, setLoading] = useState(true);
    const [data, setData] = useState([]);
    const [totalCount, setTotalCount] = useState(0);
    const [contextApp, setContextApp] = useState(null);
    const [executing, setExecuting] = useState(false);

    useEffect(() => {
        setPage(searchParams.has('page') ? searchParams.get('page') * 1 : 0);
    }, [searchParams]);


    useEffect(() => {
        axios.get(`/api/app?page=${page}`).then(response => {
            setTotalCount(response.headers['x-total-count'] || 0);
            setData(response.data);
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while fetching Apps'),
                type: 'error'
            });
        }).finally(() => {
            setLoading(false);
        });
    }, [page, addNotification]);

    const onConfirm = useCallback(() => {
        setExecuting(true);
        axios.post(`/api/app/${contextApp.id}/use`).then(response => {
            const data = response.data;
            if (data.type === 'DESTINATION') {
                navigate(`/destinations/${data.id}`);
            } else {
                navigate(`/transformations/${data.id}`);
            }

            addNotification({
                message: `Successfully created ${data.type === 'DESTINATION' ? 'Destination' : 'Transformation'}`,
                type: 'success'
            });
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while creating App'),
                type: 'error'
            });
        }).finally(() => {
            setExecuting(false);
            setContextApp(null);
        });
    }, [contextApp, addNotification])

    return <>
        <PageTitle itemKey="community" />
        {loading ? <Loading /> : <div className="overflow-x-auto">
            <div className="table-container">
                {totalCount > 0 ? <div className="mt-4 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
                    {data.map((item) => <App key={item.id} data={item} setContextApp={setContextApp} />)}
                </div> : renderNoApps()}
            </div>
            {totalCount > 0 && <Pagination totalCount={totalCount} />}
        </div>
        }

        {contextApp && <ConfirmModal {...getConfirmationMessage(contextApp)} onConfirm={onConfirm} onCancel={() => setContextApp(null)} />}
    </>
}

const renderNoApps = () => {
    return <div className="flex flex-col justify-center my-5">
        <div className="flex justify-center mb-4 text-primary">
            <img alt="Create new Destination" src="/undraw/apps.svg" className="w-3/6 max-w-3/5" />
        </div>
        <div className="flex justify-center mb-4 text-center">
            There are no Appps. Please wait until the instance syncs with the server to load available apps.
        </div>
    </div>
}

export default Community;