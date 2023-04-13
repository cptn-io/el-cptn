import { Fragment, useState } from "react";
import useNotifications from '../../../hooks/useNotifications';
import { renderErrors } from "../../../common/formHelpers";
import get from 'lodash/get';
import axios from 'axios';

const UserPasswordCard = (props) => {
    const { data: { id } } = props;

    const { addNotification } = useNotifications();
    const [editMode, setEditMode] = useState(false);
    const [error, setError] = useState({ message: null, details: [] });
    const [changes, setChanges] = useState({});
    const [executing, setExecuting] = useState(false);



    const saveChanges = (e) => {
        e.preventDefault();

        if (changes.password.trim().length === 0) {
            setEditMode(false);
            return;
        }
        setExecuting(true);
        const payload = {
            password: changes.password
        };

        axios.put(`/api/user/${id}`, payload).then(response => {
            setEditMode(false);
            addNotification({
                message: 'User password has been updated',
                type: 'success'
            });
            setError({ message: null, details: [] });
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while updating User password'),
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
            <div className="text-lg font-bold bg-base-200 p-2 rounded-md">Change Password</div>

            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Password</span>
                </label>
                {editMode ? <Fragment><input autoComplete="new-password" type="password" placeholder="New password" defaultValue={''}
                    className="input input-bordered w-full"
                    onChange={e => setChanges(current => ({
                        ...current,
                        password: e.target.value
                    }))} />
                    {renderErrors(error, 'password')}</Fragment> : <div className="p-1 text-lg">{'***********'}</div>}
            </div>


            <div className="card-actions justify-end mt-2">
                {!editMode && <button className="btn" onClick={() => setEditMode(true)}>Change Password</button>}
                {editMode && <button className="btn" onClick={cancelChanges}>Cancel</button>}
                {editMode && <button className="btn btn-primary" disabled={executing} onClick={saveChanges}>Save
                    Changes</button>}
            </div>
        </div>
    </div>
}

export default UserPasswordCard;