import { useState } from "react";
import axios from "axios";
import useNotifications from "../../../hooks/useNotifications";
import get from 'lodash/get';
import { renderErrors } from "../../../common/formHelpers";

const CreateSource = (props) => {
    const { onCancel, onSuccess, noShadow = false } = props;
    const { addNotification } = useNotifications();

    const [name, setName] = useState('');
    const [secured, setSecured] = useState(true);
    const [active, setActive] = useState(true);
    const [executing, setExecuting] = useState(false);
    const [error, setError] = useState({ message: null, details: [] });

    const resetAll = () => {
        setName('');
        setSecured(true);
        setActive(true);
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
            name,
            secured,
            active
        };
        axios.post('/api/source', payload).then(response => {
            resetAll();
            addNotification({
                message: 'Source has been created',
                type: 'success'
            });
            onSuccess(response.data);
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while creating Source'),
                type: 'error'
            });
            setError(err.response.data);
        }).finally(() => {
            setExecuting(false);
        })
    };

    return <form onSubmit={submit}>
        <div className={`sm:overflow-hidden sm:rounded-md ${noShadow ? '' : 'shadow-inner'}`}>
            <div className="space-y-3 px-4 py-5 sm:p-6">

                <div className="form-control w-full">
                    <label className="label">
                        <span className="label-text">Source Name</span>
                    </label>
                    <input type="text" placeholder="Provide a name for the Source" value={name} className="input input-bordered w-full" onChange={e => setName(e.target.value)} />
                    {renderErrors(error, 'name')}
                </div>
                <div className="form-control w-full">
                    <label className="label">
                        <span className="label-text">Secured</span>
                    </label>
                    <input type="checkbox" className={`toggle toggle-lg ${secured ? 'toggle-success' : ''}`} checked={secured} onChange={(e) => setSecured(e.target.checked)} />
                    {renderErrors(error, 'secured')}
                </div>
                <div className="form-control w-full">
                    <label className="label">
                        <span className="label-text">Active</span>
                    </label>
                    <input type="checkbox" className={`toggle toggle-lg ${active ? 'toggle-success' : ''}`} checked={active} onChange={(e) => setActive(e.target.checked)} />
                    {renderErrors(error, 'active')}
                </div>

            </div>
            <div className="bg-base-200 px-4 py-3 text-right sm:px-6">
                <button onClick={onCancel} className="btn btn-ghost mr-2">Cancel</button>
                <button disabled={executing} type="submit" className="btn btn-primary">Submit</button>
            </div>
        </div>
    </form>
}

export default CreateSource;