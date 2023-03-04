import { IconCheck, IconX, IconEye, IconEyeOff, IconClipboard } from '@tabler/icons-react';
import { Fragment, useState } from "react";
import useNotifications from '../../../hooks/useNotifications';
import { renderErrors } from "../../../common/formHelpers";
import keys from 'lodash/keys';
import fromPairs from 'lodash/fromPairs';
import toPairs from 'lodash/toPairs';
import differenceWith from 'lodash/differenceWith';
import isEqual from 'lodash/isEqual';
import get from 'lodash/get';
import axios from 'axios';
import ConfirmModal from '../../../components/ConfirmModal';

const BasicInformation = (props) => {
    const { data, onUpdate } = props;
    const [showPrimary, setShowPrimary] = useState(false);
    const [showSecondary, setShowSecondary] = useState(false);
    const { addNotification } = useNotifications();
    const [editMode, setEditMode] = useState(false);
    const [error, setError] = useState({ message: null, details: [] });
    const [changes, setChanges] = useState({});
    const [executing, setExecuting] = useState(false);
    const [showConfirmation, setShowConfirmation] = useState(false);

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
        axios.put(`/api/source/${data.id}`, payload).then(response => {
            onUpdate(response.data);
            setEditMode(false);
            addNotification({
                message: 'Source has been updated',
                type: 'success'
            });
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while updating Source'),
                type: 'error'
            });
            setError(err.response.data);
        }).finally(() => {
            setExecuting(false);
        })
    }

    const rotateKeys = () => {
        setExecuting(true);
        axios.put(`/api/source/${data.id}/rotateKeys`).then(response => {
            onUpdate(response.data);

            addNotification({
                message: 'Keys have been rotated. Ensure events are sent with the newly generated primary key',
                type: 'success'
            });
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while rotating keys'),
                type: 'error'
            });
        }).finally(() => {
            setExecuting(false);
            setShowConfirmation(false);
        });
    }
    return <div className="card bg-base-100 mb-4">
        <div className="card-body">
            <h2 className="card-title">Source Details</h2>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Source Name</span>
                </label>
                {editMode ? <Fragment><input type="text" placeholder="Provide a name for the Source" defaultValue={data.name} className="input input-bordered w-full" onChange={e => setChanges(current => ({ ...current, name: e.target.value }))} />
                    {renderErrors(error, 'name')}</Fragment> : <div className="p-1 text-lg">{data.name}</div>}
            </div>
            <div className="flex">
                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Secured</span>
                    </label>
                    {editMode ? <Fragment><input type="checkbox" className="toggle toggle-lg" defaultChecked={data.secured} onChange={e => setChanges(current => ({ ...current, secured: e.target.checked }))} />
                        {renderErrors(error, 'secured')}</Fragment> : <div className="p-1">{data.secured ? <IconCheck size={24} /> : <IconX size={24} />}</div>}
                </div>
                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Active</span>
                    </label>
                    {editMode ? <Fragment><input type="checkbox" className="toggle toggle-lg" defaultChecked={data.active} onChange={e => setChanges(current => ({ ...current, active: e.target.checked }))} />
                        {renderErrors(error, 'active')}</Fragment> : <div className="p-1">{data.active ? <IconCheck size={24} /> : <IconX size={24} />}</div>}
                </div>
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Event URL</span>
                </label>
                <div className="p-1 break-all">{`${window.location.origin}/api/source/${data.id}/event`}
                    <button title="Copy to Clipboard" className="ml-2 btn btn-ghost btn-xs" onClick={() => copyToClipboard(`${window.location.origin}/api/source/${data.id}/event`, "URL has been copied to Clipboard")}><IconClipboard size={16} /></button></div>
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Primary key</span>
                </label>
                <div className="p-1">
                    {data.secured ? <div>{showPrimary ? data.primaryKey : '********************'}
                        <button title="Show key" disabled={!data.secured} className="ml-2 btn btn-ghost btn-xs" onClick={() => setShowPrimary(current => !current)}>{showPrimary ? <IconEyeOff size={16} /> : <IconEye size={16} />}</button>
                        <button title="Copy to Clipboard" disabled={!data.secured} className="ml-2 btn btn-ghost btn-xs" onClick={() => copyToClipboard(data.primaryKey, "Key has been copied to Clipboard")}><IconClipboard size={16} /></button></div>
                        : <div>Disabled</div>}
                </div>
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Secondary key</span>
                </label>
                <div className="p-1">
                    {data.secured ? <div>{showSecondary ? data.secondaryKey : '********************'}
                        <button title="Show key" disabled={!data.secured} className="ml-2 btn btn-ghost btn-xs" onClick={() => setShowSecondary(current => !current)}>{showSecondary ? <IconEyeOff size={16} /> : <IconEye size={16} />}</button>
                        <button title="Copy to Clipboard" disabled={!data.secured} className="ml-2 btn btn-ghost btn-xs" onClick={() => copyToClipboard(data.secondaryKey, "Key has been copied to Clipboard")}><IconClipboard size={16} /></button></div>
                        : <div>Disabled</div>}
                </div>
            </div>
            <div className="card-actions justify-end">
                {!editMode && <button disabled={!data.secured || executing} className="btn btn-warning" onClick={() => setShowConfirmation(true)}>Rotate keys</button>}
                {!editMode && <button className="btn" onClick={() => setEditMode(true)}>Edit Source</button>}
                {editMode && <button className="btn btn-primary" disabled={executing} onClick={saveChanges}>Save changes</button>}
            </div>
        </div>
        {showConfirmation && <ConfirmModal title="Are you sure?"
            message="This action will generate a new primary key and sets current primary key as secondary key. The current secondary key will no longer work once the keys are rotated. This action cannot be reversed once confirmed."
            onConfirm={rotateKeys} onCancel={() => setShowConfirmation(false)} />}
    </div>
}

export default BasicInformation;