import { IconCheck, IconClipboard, IconEye, IconEyeOff, IconX } from '@tabler/icons-react';
import { useCallback, useEffect, useState } from "react";
import useNotifications from '../../../hooks/useNotifications';
import { renderErrors } from "../../../common/formHelpers";
import keys from 'lodash/keys';
import fromPairs from 'lodash/fromPairs';
import toPairs from 'lodash/toPairs';
import differenceWith from 'lodash/differenceWith';
import isEqual from 'lodash/isEqual';
import get from 'lodash/get';
import filter from 'lodash/filter';
import axios from 'axios';
import ConfirmModal from '../../../components/ConfirmModal';
import { useNavigate } from 'react-router-dom';
import HeaderBuilder from '../../../components/HeaderBuilder';
import cloneDeep from 'lodash/cloneDeep';

const SourceDetailsCard = (props) => {
    const { data: { id, name, secured, active, primaryKey, secondaryKey, headers, captureRemoteIP }, onUpdate = () => { }, readOnly = false } = props;
    const navigate = useNavigate();
    const [showPrimary, setShowPrimary] = useState(false);
    const [showSecondary, setShowSecondary] = useState(false);
    const { addNotification } = useNotifications();
    const [editMode, setEditMode] = useState(false);
    const [error, setError] = useState({ message: null, details: [] });
    const [changes, setChanges] = useState({});
    const [executing, setExecuting] = useState(false);
    const [showConfirmation, setShowConfirmation] = useState(false);
    const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);
    const [headerChanges, setHeaderChanges] = useState(headers ? cloneDeep(headers) : []);

    const copyToClipboard = (key, message) => {
        navigator.clipboard.writeText(key).then(() => {
            addNotification({
                message,
                type: 'info'
            })
        }).catch((error) => {
            addNotification({
                message: "Error writing to clipboard"
            })
        });
    }

    useEffect(() => {
        setChanges(current => ({
            ...current,
            headers: filter(headerChanges, item => item.key)
        }));
    }, [headerChanges]);

    const saveChanges = (e) => {
        e.preventDefault();

        const payload = fromPairs(differenceWith(toPairs(changes), toPairs(props.data), isEqual));

        if (changes.headers && changes.headers.length > 0) {
            payload.headers = changes.headers;
        }

        if (keys(payload).length === 0) {
            setEditMode(false);
            return;
        }
        setExecuting(true);
        axios.put(`/api/source/${id}`, payload).then(response => {
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

    const rotateKeys = useCallback(() => {
        setExecuting(true);
        axios.put(`/api/source/${id}/rotateKeys`).then(response => {
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
    }, [id, addNotification, onUpdate]);


    const deleteSource = (e) => {
        e.preventDefault();
        setExecuting(true);
        axios.delete(`/api/source/${id}`).then(response => {
            addNotification({
                message: 'Source has been deleted',
                type: 'success'
            });
            navigate('/sources');
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while deleting Source'),
                type: 'error'
            });
            setError(err.response.data);
        }).finally(() => {
            setExecuting(false);
            setShowDeleteConfirmation(false);
        })
    }

    const cancelChanges = (e) => {
        e.preventDefault();
        setChanges({});
        setHeaderChanges(headers ? cloneDeep(headers) : []);
        setEditMode(false);
    }

    const cancelDialog = useCallback(() => {
        setShowConfirmation(false)
    }, []);

    const renderCommonDetails = () => {
        return <><div className="form-control w-full">
            <label className="label">
                <span className="label-text">Event URL</span>
            </label>
            <div className="p-1 break-all">{`${window.location.origin}/event/source/${id}`}
                <button title="Copy to Clipboard" className="ml-2 btn btn-ghost btn-xs"
                    onClick={() => copyToClipboard(`${window.location.origin}/event/source/${id}`, "URL has been copied to Clipboard")}>
                    <IconClipboard size={16} /></button>
            </div>
        </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Batch Event URL</span>
                </label>
                <div className="p-1 break-all">{`${window.location.origin}/event/source/${id}/batch`}
                    <button title="Copy to Clipboard" className="ml-2 btn btn-ghost btn-xs"
                        onClick={() => copyToClipboard(`${window.location.origin}/event/source/${id}/batch`, "URL has been copied to Clipboard")}>
                        <IconClipboard size={16} /></button>
                </div>
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Primary key</span>
                </label>
                <div className="p-1">
                    {secured ? <div>{showPrimary ? primaryKey : '********************'}
                        <button title="Show key" disabled={!secured} className="ml-2 btn btn-ghost btn-xs"
                            onClick={() => setShowPrimary(current => !current)}>{showPrimary ?
                                <IconEyeOff size={16} /> : <IconEye size={16} />}</button>
                        <button title="Copy to Clipboard" disabled={!secured} className="ml-2 btn btn-ghost btn-xs"
                            onClick={() => copyToClipboard(primaryKey, "Key has been copied to Clipboard")}>
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
                    {secured ? <div>{showSecondary ? secondaryKey : '********************'}
                        <button title="Show key" disabled={!secured} className="ml-2 btn btn-ghost btn-xs"
                            onClick={() => setShowSecondary(current => !current)}>{showSecondary ?
                                <IconEyeOff size={16} /> : <IconEye size={16} />}</button>
                        <button title="Copy to Clipboard" disabled={!secured} className="ml-2 btn btn-ghost btn-xs"
                            onClick={() => copyToClipboard(secondaryKey, "Key has been copied to Clipboard")}>
                            <IconClipboard size={16} /></button>
                    </div>
                        : <div>Disabled</div>}
                </div>
            </div></>
    };

    const renderEditableForm = () => {
        return <>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Source Name</span>
                </label>
                <input type="text" placeholder="Provide a name for the Source" defaultValue={name}
                    className="input input-bordered w-full"
                    onChange={e => setChanges(current => ({
                        ...current,
                        name: e.target.value
                    }))} />
                {renderErrors(error, 'name')}
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Response Headers</span>
                </label>
                <HeaderBuilder headers={headerChanges} setHeaders={setHeaderChanges} readOnly={false} />
                {renderErrors(error, 'headers')}
            </div>
            <div className="flex">
                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Secured</span>
                    </label>
                    <input type="checkbox" className={`toggle toggle-lg ${secured ? 'toggle-success' : ''}`} defaultChecked={secured}
                        onChange={e => setChanges(current => ({
                            ...current,
                            secured: e.target.checked
                        }))} />
                    {renderErrors(error, 'secured')}
                </div>
                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Active</span>
                    </label>
                    <input type="checkbox" className={`toggle toggle-lg ${(changes.active || active) ? 'toggle-success' : ''}`} defaultChecked={active}
                        onChange={e => setChanges(current => ({
                            ...current,
                            active: e.target.checked
                        }))} />
                    {renderErrors(error, 'active')}
                </div>

                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Capture Remote IP</span>
                    </label>
                    <input type="checkbox" className={`toggle toggle-lg ${(changes.captureRemoteIP || captureRemoteIP) ? 'toggle-success' : ''}`} defaultChecked={captureRemoteIP}
                        onChange={e => setChanges(current => ({
                            ...current,
                            captureRemoteIP: e.target.checked
                        }))} />
                    {renderErrors(error, 'captureRemoteIP')}
                </div>
            </div>
            {renderCommonDetails()}
            {!readOnly && <div className="card-actions mt-2 justify-between">
                <div><button className="btn btn-error" type="button" disabled={executing} onClick={() => setShowDeleteConfirmation(true)}>Delete</button></div>
                <div className="flex justify-end">

                    <button className="btn mr-2" onClick={cancelChanges}>Cancel</button>
                    <button className="btn btn-primary" disabled={executing} onClick={saveChanges}>Save
                        Changes</button>
                </div>
            </div>}

        </>
    }

    const renderReadOnlyForm = () => {

        return <>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Source Name</span>
                </label>
                <div className="p-1 text-lg">{name}</div>
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Response Headers</span>
                </label>
                <HeaderBuilder headers={headerChanges} setHeaders={setHeaderChanges} readOnly={true} />
            </div>
            <div className="flex">
                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Secured</span>
                    </label>

                    <div className="p-1">{secured ? <IconCheck className="text-success" size={24} /> : <IconX className="text-error" size={24} />}</div>
                </div>
                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Active</span>
                    </label>

                    <div className="p-1">{active ? <IconCheck className="text-success" size={24} /> : <IconX className="text-error" size={24} />}</div>
                </div>
                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Capture Remote IP</span>
                    </label>

                    <div className="p-1">{captureRemoteIP ? <IconCheck className="text-success" size={24} /> : <IconX className="text-error" size={24} />}</div>
                </div>
            </div>
            {renderCommonDetails()}
            {!readOnly && <div className="card-actions mt-2 justify-between">
                <div></div>
                <div className="flex justify-end">
                    <button disabled={!secured || executing} className="btn btn-warning mr-2"
                        onClick={() => setShowConfirmation(true)}>Rotate keys</button>
                    <button className="btn" onClick={() => setEditMode(true)}>Edit Source</button>
                </div>
            </div>}
        </>
    }

    return <div className="card bg-base-100 mb-4">
        <div className="card-body p-4">
            <div className="text-lg font-bold bg-base-200 p-2 rounded-md">Source Details</div>
            {editMode ? renderEditableForm() : renderReadOnlyForm()}
        </div>
        {showConfirmation && <ConfirmModal title="Are you sure?"
            message="This destination will generate a new primary key and sets current primary key as secondary key. The current secondary key will no longer work once the keys are rotated. This destination cannot be reversed once confirmed."
            onConfirm={rotateKeys} onCancel={cancelDialog} />}

        {showDeleteConfirmation && <ConfirmModal title="Delete Source" message="Are you sure you want to delete this Source? All associated events related to this Source will be deleted." onConfirm={deleteSource} onCancel={() => setShowDeleteConfirmation(false)} />}
    </div>
}

export default SourceDetailsCard;