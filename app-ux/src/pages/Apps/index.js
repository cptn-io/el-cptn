import { useNavigate, useSearchParams } from "react-router-dom";
import PageTitle from "../../components/Nav/PageTitle";
import { IconBox, IconInfoCircle, IconTransform } from "@tabler/icons-react";
import axios from "axios";
import useNotifications from "../../hooks/useNotifications";
import { useCallback, useEffect, useState } from "react";
import get from "lodash/get";
import Pagination from "../../components/Pagination";
import Loading from "../../components/Loading";
import ConfirmModal from "../../components/ConfirmModal";
import moment from "moment-timezone";
import './styles.scss';
import Modal from "../../components/Modal";
import AppDetails from "./AppDetails";

const getAppCornerTag = (app) => {
    const now = moment();
    if (moment.duration(now.diff(moment(app.createdAt))).asDays() <= 1) {
        return <div className="corner"><span className="text">New!</span></div>;
    } else if (moment.duration(now.diff(moment(app.updatedAt))).asDays() <= 1) {
        return <div className="corner info"><span className="text">Updated!</span></div>;
    } else {
        return null;
    }
}

const App = ({ data, executing, handleShowConfirmation, handleShowAppDetails }) => <div className="card app bg-neutral-50 border-solid border-x border-y border-base-200 shadow col-span-2 md:col-span-1 rounded-2xl">
    {getAppCornerTag(data)}
    <figure className="py-4" style={{ height: '160px', overflow: 'hidden' }}>{data.logoUrl ? <img title={data.name} className="text-base-300" src={data.logoUrl} height={128} width={128} alt={data.name} /> : (data.type === 'DESTINATION' ? <IconBox className="text-base-300" size={156} /> : <IconTransform className="text-base-300" size={156} />)}</figure>
    <div className="card-body bg-base-200 p-4 flex flex-col justify-between">
        <div className="text-xs">{data.type}</div>
        <h2 className="card-title">{data.name}</h2>
        <div className="card-actions mt-4 justify-end">
            <button disabled={executing} onClick={() => handleShowAppDetails(data)} className="btn btn-ghost"><IconInfoCircle size={24} /></button>
            <button disabled={executing} onClick={() => handleShowConfirmation(data)} className="btn btn-primary">{data.type === 'DESTINATION' ? `New Destination` : `New Transformation`}</button>
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

const Apps = () => {
    const { addNotification } = useNotifications();
    const navigate = useNavigate();

    const [searchParams] = useSearchParams();
    const [page, setPage] = useState(searchParams.has('page') ? searchParams.get('page') * 1 : 0);

    const [loading, setLoading] = useState(true);
    const [data, setData] = useState([]);
    const [totalCount, setTotalCount] = useState(0);
    const [contextApp, setContextApp] = useState(null);
    const [showConfirmation, setShowConfirmation] = useState(false);
    const [showAppDetails, setShowAppDetails] = useState(false);

    const [executing, setExecuting] = useState(false);



    useEffect(() => {
        setPage(searchParams.has('page') ? searchParams.get('page') * 1 : 0);
    }, [searchParams]);

    useEffect(() => {
        if (!contextApp) {
            setShowAppDetails(false);
            setShowConfirmation(false)
        }
    }, [contextApp]);

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

    const onConfirmAppUse = useCallback(() => {
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
    }, [contextApp, navigate, addNotification]);

    const handleShowConfirmation = useCallback((app) => {
        setContextApp(app);
        setShowConfirmation(true);
    }, []);

    const handleShowAppDetails = useCallback((app) => {
        setContextApp(app);
        setShowAppDetails(true);
    }, []);

    const clearContextApp = useCallback(() => {
        setContextApp(null);
    }, []);

    return <>
        <PageTitle itemKey="apps" />
        {loading ? <Loading /> : <div className="overflow-x-auto">
            <div className="table-container">
                {totalCount > 0 ? <div className="mt-4 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
                    {data.map((item) => <App key={item.id} data={item} executing={executing} handleShowConfirmation={handleShowConfirmation} handleShowAppDetails={handleShowAppDetails} />)}
                </div> : renderNoApps()}
            </div>
            {totalCount > 0 && <Pagination totalCount={totalCount} />}
        </div>
        }

        {contextApp && showConfirmation && <ConfirmModal {...getConfirmationMessage(contextApp)} onConfirm={onConfirmAppUse} onCancel={clearContextApp} />}
        {contextApp && showAppDetails && <Modal title="App Details" onCancel={clearContextApp} >
            <AppDetails app={contextApp} onCancel={clearContextApp} onUseApp={onConfirmAppUse} />
        </Modal>}
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

export default Apps;