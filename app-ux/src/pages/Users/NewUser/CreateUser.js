import { useCallback, useEffect, useState } from "react";
import axios from "axios";
import useNotifications from "../../../hooks/useNotifications";
import get from 'lodash/get';
import { renderErrors } from "../../../common/formHelpers";
import { extractNameFromEmail } from "../helpers";
import { IconEye } from "@tabler/icons-react";
import { IconEyeOff } from "@tabler/icons-react";

const CreateUser = (props) => {
    const { onCancel, onSuccess, noShadow = false } = props;
    const { addNotification } = useNotifications();

    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [disabled, setDisabled] = useState(false);
    const [lockedOut, setLockedOut] = useState(false);
    const [showPassword, setShowPassword] = useState(false);


    const [executing, setExecuting] = useState(false);
    const [error, setError] = useState({ message: null, details: [] });

    const resetAll = () => {
        setFirstName('');
        setLastName('');
        setEmail('');
        setPassword('');
        setDisabled(false);
        setLockedOut(false);
        clearErrors();
    }

    const clearErrors = () => {
        setError({ message: null, details: [] });
    }


    const submit = (e) => {
        e.preventDefault();
        clearErrors();
        setExecuting(true);

        const payload = {
            firstName,
            lastName,
            email,
            password,
            lockedOut,
            disabled
        };
        axios.post('/api/user', payload).then(response => {
            resetAll();
            addNotification({
                message: 'User has been created',
                type: 'success'
            });
            onSuccess(response.data);
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while creating User'),
                type: 'error'
            });
            setError(err.response.data);
        }).finally(() => {
            setExecuting(false);
        })
    };

    const resolveNames = useCallback(() => {
        const name = extractNameFromEmail(email);
        if (!lastName) {
            setLastName(name.lastName);
        }

        if (!firstName) {
            setFirstName(name.firstName);
        }
    }, [email])

    return <form onSubmit={submit}>
        <div className={`sm:overflow-hidden sm:rounded-md ${noShadow ? '' : 'shadow-inner'}`}>
            <div className="space-y-3 px-4 py-5 sm:p-6">

                <div className="form-control w-full">
                    <label className="label">
                        <span className="label-text">Email Address</span>
                    </label>
                    <input type="email" autoComplete="username" placeholder="Email Address (Username)" value={email} className="input input-bordered w-full" onBlur={resolveNames} onChange={e => setEmail(e.target.value)} />
                    {renderErrors(error, 'email')}
                </div>
                <div className="form-control w-full">
                    <label className="label">
                        <span className="label-text">First Name</span>
                    </label>
                    <input autoComplete="given-name" type="text" placeholder="First Name" value={firstName} className="input input-bordered w-full" onChange={e => setFirstName(e.target.value)} />
                    {renderErrors(error, 'firstName')}
                </div>
                <div className="form-control w-full">
                    <label className="label">
                        <span className="label-text">Last Name</span>
                    </label>
                    <input autoComplete="family-name" type="text" placeholder="Last Name" value={lastName} className="input input-bordered w-full" onChange={e => setLastName(e.target.value)} />
                    {renderErrors(error, 'lastName')}
                </div>

                <div className="form-control w-full">
                    <label className="label">
                        <span className="label-text">Password</span>
                    </label>
                    <div className="input-group">
                        <input autoComplete="new-password" type={showPassword ? "text" : "password"} name="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="Account password" className="input input-bordered w-full" />
                        <button type="button" className="btn btn-square" onClick={(e) => setShowPassword(current => !current)}>
                            {!showPassword && <IconEye size="24" />}
                            {showPassword && <IconEyeOff size="24" />}
                        </button>
                    </div>
                    {renderErrors(error, 'password')}
                </div>

                <div className="form-control w-full">
                    <label className="label">
                        <span className="label-text">Disabled</span>
                    </label>
                    <input type="checkbox" className={`toggle toggle-lg ${disabled ? 'toggle-error' : ''}`} checked={disabled} onChange={(e) => setDisabled(e.target.checked)} />
                    {renderErrors(error, 'disabled')}
                </div>
                <div className="form-control w-full">
                    <label className="label">
                        <span className="label-text">Locked Out</span>
                    </label>
                    <input type="checkbox" className={`toggle toggle-lg ${lockedOut ? 'toggle-error' : ''}`} checked={lockedOut} onChange={(e) => setLockedOut(e.target.checked)} />
                    {renderErrors(error, 'lockedOut')}
                </div>

            </div>
            <div className="bg-base-200 px-4 py-3 text-right sm:px-6">
                <button onClick={onCancel} className="btn btn-ghost mr-2">Cancel</button>
                <button disabled={executing} type="submit" className="btn btn-primary">Submit</button>
            </div>
        </div>
    </form>
}

export default CreateUser;