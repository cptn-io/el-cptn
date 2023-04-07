import { ReUnixCron } from "@sbzen/re-cron";
import axios from "axios";
import moment from "moment-timezone";
import { useEffect, useState } from "react";
import { renderErrors } from "../../../../common/formHelpers";
import Loading from "../../../../components/Loading";
import useNotifications from "../../../../hooks/useNotifications";

import fromPairs from 'lodash/fromPairs';
import toPairs from 'lodash/toPairs';
import differenceWith from 'lodash/differenceWith';
import isEqual from 'lodash/isEqual';
import get from 'lodash/get';

const Scheduler = (props) => {
    const { pipelineId } = props;
    const { addNotification } = useNotifications();

    const [error, setError] = useState({ message: null, details: [] });
    const [loading, setLoading] = useState(true);

    const [schedule, setSchedule] = useState(null);

    const [timeZone, setTimeZone] = useState(moment.tz.guess());
    const [cronExpression, setCronExpression] = useState('*/30 * * * *');
    const [active, setActive] = useState(true);
    const [executing, setExecuting] = useState(false);


    useEffect(() => {
        axios.get(`/api/pipeline_schedule/pipeline/${pipelineId}`).then(response => {
            setSchedule(response.data[0]);
            if (response.data[0]) {
                setTimeZone(response.data[0].timeZone);
                setCronExpression(response.data[0].cronExpression);
                setActive(response.data[0].active);
            }
        }).catch(error => {
            addNotification({
                message: get(error, 'response.data.message', 'An error occurred while fetching Pipeline Schedule'),
                type: 'error'
            });
        }).finally(() => {
            setLoading(false);
        });
    }, [pipelineId, addNotification])

    if (loading) {
        return <Loading />
    }

    const setupSchedule = () => {
        setSchedule(() => ({}));
    };

    const discardChanges = () => {
        setTimeZone(schedule.timeZone);
        setCronExpression(schedule.cronExpression);
        setActive(schedule.active);
    };
    const saveSchedule = () => {
        const changes = {
            timeZone,
            cronExpression,
            active
        }
        const payload = fromPairs(differenceWith(toPairs(changes), toPairs(schedule), isEqual));
        setExecuting(true);
        if (schedule.id) {
            //update
            axios.put(`/api/pipeline_schedule/${schedule.id}`, payload).then(response => {
                setSchedule(response.data);
                addNotification({
                    message: 'Pipeline Schedule has been updated successfully',
                    type: 'success'
                });
            }).catch(error => {
                addNotification({
                    message: get(error, 'response.data.message', 'An error occurred while saving Pipeline Schedule'),
                    type: 'error'
                });
                setError(error.response.data);
            }).finally(() => {
                setExecuting(false);
            })
        } else {
            payload.pipeline = {};
            payload.pipeline.id = pipelineId;

            axios.post(`/api/pipeline_schedule`, payload).then(response => {
                setSchedule(response.data);
                addNotification({
                    message: 'Pipeline Schedule has been created successfully',
                    type: 'success'
                });
            }).catch(error => {
                addNotification({
                    message: get(error, 'response.data.message', 'An error occurred while saving Pipeline Schedule'),
                    type: 'error'
                });
                setError(error.response.data);
            }).finally(() => {
                setExecuting(false);
            })
        }
    };

    if (!schedule) {
        return <>
            <div className="m-2">This Pipeline does not have an associated Schedule. Events in this Pipeline can only be processed on-demand.</div>
            <div className="px-4 py-3 text-right sm:px-6">
                <button type="button" onClick={setupSchedule} className="btn btn-primary">Setup a Schedule</button>
            </div>
        </>
    }

    return <>
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
                <div className="mx-1">{moment(schedule.nextRunAt).format('LLLL')}</div>
            </div>
            <div className="ng-base-200 px-4 py-3 text-right sm:px-6">
                <button type="button" disabled={executing} onClick={discardChanges} className="btn mr-2">Discard Changes</button>
                <button type="button" disabled={executing} onClick={saveSchedule} className="btn btn-primary">Save Schedule</button>
            </div>
        </>}
    </>
};

export default Scheduler;