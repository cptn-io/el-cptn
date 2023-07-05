import { ReUnixCron } from "@sbzen/re-cron";
import { useEffect, useState } from "react";
import useNotifications from "../../../../../../hooks/useNotifications";
import axios from "axios";
import moment from "moment-timezone";
import { renderErrors } from "../../../../../../common/formHelpers";
import fromPairs from 'lodash/fromPairs';
import toPairs from 'lodash/toPairs';
import differenceWith from 'lodash/differenceWith';
import isEqual from 'lodash/isEqual';
import get from 'lodash/get';
import Loading from "../../../../../../components/Loading";


const ExtractorSchedule = (props) => {
    const { extractorId } = props;
    const { addNotification } = useNotifications();
    const [error, setError] = useState({ message: null, details: [] });
    const [loading, setLoading] = useState(true);

    const [schedule, setSchedule] = useState(null);

    const [timeZone, setTimeZone] = useState(moment.tz.guess());
    const [cronExpression, setCronExpression] = useState('*/30 * * * *');
    const [active, setActive] = useState(true);
    const [executing, setExecuting] = useState(false);

    const clearErrors = () => {
        setError({ message: null, details: [] });
    };

    useEffect(() => {
        axios.get(`/api/extractor_schedule/extractor/${extractorId}`).then(response => {
            setSchedule(response.data[0]);
            if (response.data[0]) {
                setTimeZone(response.data[0].timeZone);
                setCronExpression(response.data[0].cronExpression);
                setActive(response.data[0].active);
            }
        }).catch(error => {
            addNotification({
                message: get(error, 'response.data.message', 'An error occurred while fetching Extractor Schedule'),
                type: 'error'
            });
        }).finally(() => {
            setLoading(false);
        });
    }, [extractorId, addNotification])

    const setupSchedule = () => {
        setSchedule(() => ({}));
    };

    const discardChanges = () => {
        setTimeZone(schedule.timeZone);
        setCronExpression(schedule.cronExpression);
        setActive(schedule.active);
    };
    const saveSchedule = () => {
        clearErrors();
        const changes = {
            timeZone,
            cronExpression,
            active
        }
        const payload = fromPairs(differenceWith(toPairs(changes), toPairs(schedule), isEqual));
        setExecuting(true);
        if (schedule.id) {
            //update
            axios.put(`/api/extractor_schedule/${schedule.id}`, payload).then(response => {
                setSchedule(response.data);
                addNotification({
                    message: 'Extractor Schedule has been updated successfully',
                    type: 'success'
                });
            }).catch(error => {
                addNotification({
                    message: get(error, 'response.data.message', 'An error occurred while saving Extractor Schedule'),
                    type: 'error'
                });
                setError(error.response.data);
            }).finally(() => {
                setExecuting(false);
            })
        } else {
            payload.extractor = {};
            payload.extractor.id = extractorId;

            axios.post(`/api/extractor_schedule`, payload).then(response => {
                setSchedule(response.data);
                addNotification({
                    message: 'Extractor Schedule has been created successfully',
                    type: 'success'
                });
            }).catch(error => {
                addNotification({
                    message: get(error, 'response.data.message', 'An error occurred while saving Extractor Schedule'),
                    type: 'error'
                });
                setError(error.response.data);
            }).finally(() => {
                setExecuting(false);
            })
        }
    };


    if (loading) {
        return <Loading />
    }

    return <div className="card bg-base-100 mb-4">
        <div className="card-body p-4">
            <div className="text-lg font-bold bg-base-200 p-2 rounded-md">Extractor Schedule</div>
            {!schedule && <>
                <div className="m-2">This Extractor does not have an associated Schedule. Extraction script can only be executed on-demand.</div>
                <div className="text-right">
                    <button type="button" onClick={setupSchedule} className="btn btn-primary">Setup a Schedule</button>
                </div>
            </>
            }

            {schedule && <>
                <div className="form-control w-full mb-2">
                    <label className="label">
                        <span className="label-text">Time zone</span>
                    </label>
                    <select value={timeZone} onChange={e => setTimeZone(e.target.value)} className="select select-bordered w-full max-w-xs font-normal text-base">
                        {moment.tz.names().map(tz => <option key={tz} value={tz}>{tz}</option>)}
                    </select>
                    {renderErrors(error, 'timezone')}
                </div>
                <div className="form-control w-full">
                    <label className="label">
                        <span className="label-text">Run Schedule (Current Selection: {cronExpression})</span>
                    </label>
                    <div className="flex justify-between items-center mx-2 my-4">
                        <div>Popular Schedules:</div>
                        <button className="btn btn-sm btn-outline btn-secondary" onClick={() => setCronExpression("*/15 * * * *")}>Every 15 mins</button>
                        <button className="btn btn-sm btn-outline btn-secondary" onClick={() => setCronExpression("*/30 * * * *")}>Every 30 mins</button>
                        <button className="btn btn-sm btn-outline btn-secondary" onClick={() => setCronExpression("0 * * * *")}>Every hour</button>
                        <button className="btn btn-sm btn-outline btn-secondary" onClick={() => setCronExpression("0 */6 * * *")}>Every 6 hrs</button>
                        <button className="btn btn-sm btn-outline btn-secondary" onClick={() => setCronExpression("0 */12 * * * *")}>Every 12 hrs</button>
                        <button className="btn btn-sm btn-outline btn-secondary" onClick={() => setCronExpression("0 0 * * *")}>Every day</button>
                    </div>
                    <div className="cron-section my-2">
                        <ReUnixCron value={cronExpression} onChange={setCronExpression} cssClassPrefix="cptn-" />
                    </div>
                    {renderErrors(error, 'cronExpression')}
                </div>
                <div className="form-control w-full">
                    <label className="label">
                        <span className="label-text">Active</span>
                    </label>
                    <input type="checkbox" className={`toggle toggle-lg ${active ? 'toggle-success' : ''}`} checked={active} onChange={(e) => setActive(e.target.checked)} />
                    {renderErrors(error, 'active')}
                </div>
                <div className="form-control w-full">
                    <label className="label">
                        <span className="label-text">Next Execution at</span>
                    </label>
                    <div className="mx-1">{schedule?.active ? moment(schedule.nextRunAt).format('LLLL') : 'Schedule is inactive'}</div>
                </div>
                <div className="ng-base-200 px-4 py-3 text-right sm:px-6">
                    {schedule?.id && <button type="button" disabled={executing} onClick={discardChanges} className="btn mr-2">Discard Changes</button>}
                    <button type="button" disabled={executing} onClick={saveSchedule} className="btn btn-primary">Save Schedule</button>
                </div>
            </>}
        </div>


    </div>
}

export default ExtractorSchedule;