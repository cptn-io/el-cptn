import { IconArrowRight, IconCheck, IconClipboard, IconEye, IconEyeOff, IconX } from "@tabler/icons-react";
import { useState } from "react";
import { Link } from "react-router-dom";
import useNotifications from "../../../../hooks/useNotifications";
import keys from 'lodash/keys';
import fromPairs from 'lodash/fromPairs';
import toPairs from 'lodash/toPairs';
import differenceWith from 'lodash/differenceWith';
import isEqual from 'lodash/isEqual';
import get from 'lodash/get';
import axios from 'axios';
import { renderErrors } from "../../../../common/formHelpers";

const PipelineDetailsCard = (props) => {
    const { data, onUpdate } = props;
    const { addNotification } = useNotifications();

    const [editMode, setEditMode] = useState(false);
    const [error, setError] = useState({ message: null, details: [] });
    const [changes, setChanges] = useState({});
    const [executing, setExecuting] = useState(false);

    const [showPrimary, setShowPrimary] = useState(false);
    const [showSecondary, setShowSecondary] = useState(false);

    const copyToClipboard = (key, message) => {
        navigator.clipboard.writeText(key)
        addNotification({
            message,
            type: 'info'
        })
    }

    const saveChanges = (e) => {
        e.preventDefault();
        const payload = fromPairs(differenceWith(toPairs(changes), toPairs(data), isEqual));
        if (keys(payload).length === 0) {
            setEditMode(false);
            return;
        }
        setExecuting(true);
        axios.put(`/api/pipeline/${data.id}`, payload).then(response => {
            onUpdate(response.data);
            setEditMode(false);
            addNotification({
                message: 'Pipeline has been updated',
                type: 'success'
            });
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while updating Pipeline'),
                type: 'error'
            });
            setError(err.response.data);
        }).finally(() => {
            setExecuting(false);
        })
    }

    const cancelChanges = (e) => {
        e.preventDefault();
        setChanges({});
        setEditMode(false);
    }

    return <div className="card bg-base-100 mb-4">
        <div className="card-body p-4">
            <div className="text-lg font-bold bg-base-200 p-2 rounded-md">Pipeline Details</div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Pipeline Name</span>
                </label>
                {editMode ? <><input type="text" placeholder="Provide a name for the Pipeline" defaultValue={data.name}
                    className="input input-bordered w-full"
                    onChange={e => setChanges(current => ({
                        ...current,
                        name: e.target.value
                    }))} />
                    {renderErrors(error, 'name')}</> : <div className="p-1 text-lg">{data.name}</div>}
            </div>
            <div className="flex">
                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Active</span>
                    </label>
                    {editMode ? <><input type="checkbox" className={`toggle toggle-lg ${changes.active || data.active ? 'toggle-success' : ''}`} defaultChecked={data.active}
                        onChange={e => setChanges(current => ({
                            ...current,
                            active: e.target.checked
                        }))} />
                        {renderErrors(error, 'active')}</> :
                        <div className="p-1">{data.active ? <IconCheck className="text-success" size={24} /> : <IconX className="text-error" size={24} />}</div>}
                </div>

                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Scheduled Batch Processing</span>
                    </label>
                    {editMode ? <><input type="checkbox" className={`toggle toggle-lg ${changes.active || data.active ? 'toggle-success' : ''}`} defaultChecked={data.batchProcess}
                        onChange={e => setChanges(current => ({
                            ...current,
                            batchProcess: e.target.checked
                        }))} />
                        {renderErrors(error, 'batchProcess')}</> :
                        <div className="p-1 text-md">{data.batchProcess ? 'Enabled' : 'Disabled'}</div>}
                </div>
            </div>
            <div className="card-actions justify-end mb-3">
                {!editMode && <button className="btn" onClick={() => setEditMode(true)}>Edit Pipeline</button>}
                {editMode && <button className="btn" onClick={cancelChanges}>Cancel</button>}
                {editMode && <button className="btn btn-primary" disabled={executing} onClick={saveChanges}>Save
                    Changes</button>}
            </div>
            <div className="text-lg font-bold bg-base-200 p-2 rounded-md flex justify-between items-center">
                <span>Data Source</span>
                <Link className="btn btn-sm btn-ghost px-2" to={`/sources/${data.source.id}`}><IconArrowRight className="text-primary" size={24}></IconArrowRight></Link>
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Source</span>
                </label>
                <div className="p-1 text-lg">{data.source.name}</div>
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Event URL</span>
                </label>
                <div className="p-1 break-all">{`${window.location.origin}/api/source/${data.source.id}/event`}
                    <button title="Copy to Clipboard" className="ml-2 btn btn-ghost btn-xs"
                        onClick={() => copyToClipboard(`${window.location.origin}/api/source/${data.source.id}/event`, "URL has been copied to Clipboard")}>
                        <IconClipboard size={16} /></button>
                </div>
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Primary key</span>
                </label>
                <div className="p-1">
                    {data.source.secured ? <div>{showPrimary ? data.source.primaryKey : '********************'}
                        <button title="Show key" disabled={!data.source.secured} className="ml-2 btn btn-ghost btn-xs"
                            onClick={() => setShowPrimary(current => !current)}>{showPrimary ?
                                <IconEyeOff size={16} /> : <IconEye size={16} />}</button>
                        <button title="Copy to Clipboard" disabled={!data.source.secured} className="ml-2 btn btn-ghost btn-xs"
                            onClick={() => copyToClipboard(data.source.primaryKey, "Key has been copied to Clipboard")}>
                            <IconClipboard size={16} /></button>
                    </div>
                        : <div>Disabled</div>}
                </div>
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Secondary key</span>
                </label>
                <div className="p-1">
                    {data.source.secured ? <div>{showSecondary ? data.source.secondaryKey : '********************'}
                        <button title="Show key" disabled={!data.source.secured} className="ml-2 btn btn-ghost btn-xs"
                            onClick={() => setShowSecondary(current => !current)}>{showSecondary ?
                                <IconEyeOff size={16} /> : <IconEye size={16} />}</button>
                        <button title="Copy to Clipboard" disabled={!data.source.secured} className="ml-2 btn btn-ghost btn-xs"
                            onClick={() => copyToClipboard(data.source.secondaryKey, "Key has been copied to Clipboard")}>
                            <IconClipboard size={16} /></button>
                    </div>
                        : <div>Disabled</div>}
                </div>
            </div>
            <div className="flex">
                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Active</span>
                    </label>
                    <div className="p-1">{data.source.active ? <IconCheck className="text-success" size={24} /> : <IconX className="text-error" size={24} />}</div>
                </div>
            </div>

            <div className="text-lg font-bold bg-base-200 p-2 rounded-md flex justify-between items-center">
                <span>Data Destination</span>
                <Link className="btn btn-sm btn-ghost px-2" to={`/destinations/${data.destination.id}`}><IconArrowRight className="text-primary" size={24}></IconArrowRight></Link>
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Destination Name</span>
                </label>
                <div className="p-1 text-lg">{data.destination.name}</div>
            </div>
            <div className="flex">
                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Active</span>
                    </label>
                    <div className="p-1">{data.destination.active ? <IconCheck className="text-success" size={24} /> : <IconX size={24} />}</div>
                </div>
            </div>
        </div>
    </div>
}

export default PipelineDetailsCard;