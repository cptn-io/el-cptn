import axios from "axios";
import { useCallback, useEffect, useState } from "react";
import Loading from "../../../../components/Loading";
import useNotifications from "../../../../hooks/useNotifications";
import get from 'lodash/get';
import moment from "moment-timezone";
import { IconCircleCheckFilled, IconCircleDashed, IconCircleXFilled, IconClockPause, IconFileDescription, IconRefresh, IconSend } from "@tabler/icons-react";
import Modal from "../../../../components/Modal";
import EventDetails from "./EventDetails";
import Pagination from "../../../../components/Pagination";
import { useSearchParams } from "react-router-dom";
import InboundEventFilter from "./InboundEventFilter";
import useStatusFilter from "../../../../hooks/useStatusFilter";

export const resolveState = (state) => {
    switch (state) {
        case 'COMPLETED':
            return <><IconCircleCheckFilled size={24} className="text-success mr-1" />Sent to Pipelines</>
        case 'PROCESSING':
            return <><IconCircleDashed size={24} className="text-warning mr-1" />Processing</>
        case 'FAILED':
            return <><IconCircleXFilled size={24} className="text-error mr-1" />Failed</>
        case 'QUEUED':
            return <><IconClockPause size={24} className="text-info mr-1" />Queued</>;
        default:
            return state;
    }
};
const InboundEventList = ({ sourceId }) => {
    const { addNotification } = useNotifications();
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [event, setEvent] = useState(null);
    const [executing, setExecuting] = useState(false);
    const [totalCount, setTotalCount] = useState(0);
    const [searchParams] = useSearchParams();
    const [page, setPage] = useState(searchParams.has('page') ? searchParams.get('page') * 1 : 0);
    const [status, setStatus] = useState(searchParams.has('status') ? searchParams.get('status') : null);

    useEffect(() => {
        setPage(searchParams.has('page') ? searchParams.get('page') * 1 : 0);
    }, [searchParams]);

    useStatusFilter({ status });

    const refreshList = useCallback(() => {
        setExecuting(true);

        const filters = [];
        filters.push(`sourceId:eq:${sourceId}`);
        if (status) {
            filters.push(`state:eq:${status}`);
        }
        axios.get(`/api/inbound_event?filters=${filters.join()}&page=${page}`)
            .then(res => {
                setTotalCount((res.headers['x-total-count'] || 0) * 1);
                setData(res.data);
            }).catch(err => {
                addNotification({
                    message: get(err, 'response.data.message', 'An error occurred while fetching Inbound Events'),
                    type: 'error'
                });
            }).finally(() => {
                setExecuting(false)
                setLoading(false);
            });
    }, [sourceId, page, status, addNotification]);

    useEffect(() => {
        refreshList();
    }, [refreshList]);

    const showDetails = (event) => {
        setEvent(event);
    };

    const refreshEvent = (eventId) => {
        setExecuting(true);
        axios.get(`/api/inbound_event/${eventId}`)
            .then(res => {
                const updatedEvent = res.data;
                //update the event object in the list
                setData(current => {
                    const index = current.findIndex(e => e.id === updatedEvent.id);
                    if (index > -1) {
                        current[index] = updatedEvent;
                    }
                    return [...current];
                });
            }).catch(err => {
                addNotification({
                    message: get(err, 'response.data.message', 'An error occurred while fetching Inbound Event'),
                    type: 'error'
                });
            }).finally(() => {
                setExecuting(false);
            });
    }

    const resendEvent = (eventId) => {
        setExecuting(true);
        axios.post(`/api/inbound_event/${eventId}/resend`)
            .then(res => {
                setTotalCount(current => current + 1);
                setData(current => [res.data, ...current]);
                addNotification({
                    message: 'Event resent successfully.',
                    type: 'success'
                });
            }).catch(err => {
                addNotification({
                    message: get(err, 'response.data.message', 'An error occurred while fetching Inbound Events'),
                    type: 'error'
                });
            }).finally(() => {
                setExecuting(false);
            });
    }

    const renderNoRecordsToShow = () => {
        if (status) {
            return <tr><td colSpan={4} className="text-center">No events found matching the current filter</td></tr>
        }

        return <tr><td colSpan={4} className="text-center">No events found associated with the Source.</td></tr>
    }

    if (loading) {
        return <Loading />
    }

    return <div className="p-4">
        <div className="mb-4 text-lg font-bold flex justify-between items-center">
            <span>Received Events ({totalCount})</span>
            <span>
                <button disabled={executing} className="btn btn-ghost gap-2" onClick={refreshList}>
                    <IconRefresh size={24} />Refresh
                </button>
            </span>
        </div>
        <InboundEventFilter status={status} setStatus={setStatus} />
        <div className="table-container">
            <table className="table data-table table-zebra w-full">
                <thead>
                    <tr>
                        <th className="bg-base-300 text-left w-1/3 md:w-1/3">Event ID</th>
                        <th className="bg-base-300 text-center">Received At</th>
                        <th className="bg-base-300 text-center">Status</th>
                        <th className="bg-base-300 text-center">Actions</th>
                    </tr>
                </thead>
                <tbody style={{ maxHeight: '600px' }}>
                    {data.map(event => <tr key={event.id}>
                        <td className="whitespace-pre-wrap break-words"><button onClick={() => showDetails(event)}>{event.id}</button></td>
                        <td className="text-center"><div className="flex justify-center">{moment(event.createdAt).format('LLL')}</div></td>
                        <td className="text-center"><div className="flex justify-center">{resolveState(event.state)}</div></td>
                        <td className="text-center">
                            <div className="join">
                                <button onClick={() => showDetails(event)} className="btn join-item btn-sm btn-ghost tooltip" data-tip="Show Details"><IconFileDescription size={24} /></button>
                                <button disabled={executing} onClick={() => refreshEvent(event.id)} className="btn join-item btn-sm btn-ghost tooltip" data-tip="Refresh"><IconRefresh size={24} /></button>
                                <button disabled={executing} onClick={() => resendEvent(event.id)} className="btn join-item btn-sm btn-ghost tooltip" data-tip="Resend Event"><IconSend size={24} /></button>
                            </div>
                        </td>
                    </tr>)}
                    {totalCount === 0 && renderNoRecordsToShow()}
                </tbody>
            </table>
        </div>
        {totalCount > 0 && <Pagination totalCount={totalCount} />}
        {event && <Modal title="Event Details" onCancel={() => setEvent(null)}>
            <EventDetails event={event} onCancel={() => setEvent(null)} onSendEvent={resendEvent} />
        </Modal>}
    </div>
}

export default InboundEventList;