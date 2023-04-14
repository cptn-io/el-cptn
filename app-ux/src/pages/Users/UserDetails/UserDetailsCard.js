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
import moment from 'moment-timezone';

const UserDetailsCard = (props) => {
    const { data: { id, firstName, lastName, email, disabled, lockedOut, lastLoginAt }, onUpdate = () => { } } = props;

    const { addNotification } = useNotifications();
    const [editMode, setEditMode] = useState(false);
    const [error, setError] = useState({ message: null, details: [] });
    const [changes, setChanges] = useState({});
    const [executing, setExecuting] = useState(false);



    const saveChanges = (e) => {
        e.preventDefault();

        const payload = fromPairs(differenceWith(toPairs(changes), toPairs(props.data), isEqual));
        if (keys(payload).length === 0) {
            setEditMode(false);
            return;
        }
        setExecuting(true);
        axios.put(`/api/user/${id}`, payload).then(response => {
            onUpdate(response.data);
            setEditMode(false);
            addNotification({
                message: 'User has been updated',
                type: 'success'
            });
            setError({ message: null, details: [] });
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while updating User'),
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
        <div className="card-body p-0">
            <div className="text-lg font-bold bg-base-200 p-2 rounded-md">User Details</div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Email Address</span>
                </label>
                {editMode ? <Fragment><input autoComplete="email" type="email" placeholder="Email address" defaultValue={email}
                    className="input input-bordered w-full"
                    onChange={e => setChanges(current => ({
                        ...current,
                        email: e.target.value
                    }))} />
                    {renderErrors(error, 'email')}</Fragment> : <div className="p-1 text-lg">{email}</div>}
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">First Name</span>
                </label>
                {editMode ? <Fragment><input autoComplete="given-name" type="text" placeholder="First Name" defaultValue={firstName}
                    className="input input-bordered w-full"
                    onChange={e => setChanges(current => ({
                        ...current,
                        firstName: e.target.value
                    }))} />
                    {renderErrors(error, 'firstName')}</Fragment> : <div className="p-1 text-lg">{firstName}</div>}
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Last Name</span>
                </label>
                {editMode ? <Fragment><input type="text" autoComplete="family-name" placeholder="Last Name" defaultValue={lastName}
                    className="input input-bordered w-full"
                    onChange={e => setChanges(current => ({
                        ...current,
                        lastName: e.target.value
                    }))} />
                    {renderErrors(error, 'lastName')}</Fragment> : <div className="p-1 text-lg">{lastName}</div>}
            </div>
            <div className="flex">
                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Disabled</span>
                    </label>
                    {editMode ? <Fragment><input type="checkbox" className={`toggle toggle-lg ${(changes.disabled || disabled) ? 'toggle-error' : ''}`} defaultChecked={disabled}
                        onChange={e => setChanges(current => ({
                            ...current,
                            disabled: e.target.checked
                        }))} />
                        {renderErrors(error, 'disabled')}</Fragment> :
                        <div className="p-1">{disabled ? 'Yes' : 'No'}</div>}
                </div>
                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Locked Out</span>
                    </label>
                    {editMode ? <Fragment><input type="checkbox" className={`toggle toggle-lg ${(changes.lockedOut || lockedOut) ? 'toggle-error' : ''}`} defaultChecked={lockedOut}
                        onChange={e => setChanges(current => ({
                            ...current,
                            lockedOut: e.target.checked
                        }))} />
                        {renderErrors(error, 'lockedOut')}</Fragment> :
                        <div className="p-1">{lockedOut ? 'Yes' : 'No'}</div>}
                </div>
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Last Login at</span>
                </label>
                <div className="p-1 text-lg">{lastLoginAt ? moment(lastLoginAt).format('LLLL') : 'Never'}</div>
            </div>

            <div className="card-actions justify-end">
                {!editMode && <button className="btn" onClick={() => setEditMode(true)}>Edit User</button>}
                {editMode && <button className="btn" onClick={cancelChanges}>Cancel</button>}
                {editMode && <button className="btn btn-primary" disabled={executing} onClick={saveChanges}>Save
                    Changes</button>}
            </div>
        </div>
    </div>
}

export default UserDetailsCard;