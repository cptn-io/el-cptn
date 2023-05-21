import { useEffect, useState } from "react";
import { renderErrors } from "../../../common/formHelpers";
import useNotifications from "../../../hooks/useNotifications";
import axios from "axios";
import { IconClipboard, IconEye, IconEyeOff } from "@tabler/icons-react";
import get from "lodash/get";
import Loading from "../../../components/Loading";

const SSO = () => {
    const { addNotification } = useNotifications();
    const [loading, setLoading] = useState(true);
    const [clientId, setClientId] = useState('');
    const [clientSecret, setClientSecret] = useState('');
    const [wellKnownUrl, setWellKnownUrl] = useState('');
    const [active, setActive] = useState(true);
    const [ssoOnly, setSsoOnly] = useState(false);
    const [enableCreateUser, setEnableCreateUser] = useState(false);
    const [showClientSecret, setShowClientSecret] = useState(false);

    const [executing, setExecuting] = useState(false);
    const [error, setError] = useState({ message: null, details: [] });
    const clearErrors = () => {
        setError({ message: null, details: [] });
    }

    const copyToClipboard = (e, key, message) => {
        e.preventDefault();
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
        setExecuting(true);
        axios.get('/api/ssoProfile').then(response => {
            const data = response.data;
            setClientId(data.clientId);
            setClientSecret(data.clientSecret);
            setWellKnownUrl(data.wellKnownUrl);
            setActive(data.active);
            setSsoOnly(data.ssoOnly);
            setEnableCreateUser(data.enableCreateUser);
        }).catch(err => {
            if (err.response.status !== 404) {
                addNotification({
                    title: 'Error',
                    message: 'Failed to load SSO configuration.',
                    type: 'error'
                });
            }
        }).finally(() => {
            setExecuting(false);
            setLoading(false);
        });
    }, [addNotification]);

    const submit = (e) => {
        e.preventDefault();
        clearErrors();
        setExecuting(true);

        const payload = {
            clientId,
            clientSecret,
            wellKnownUrl,
            active,
            ssoOnly,
            enableCreateUser
        };

        axios.post('/api/ssoProfile', payload).then(() => {
            addNotification({
                message: 'SSO configuration has been saved.',
                type: 'success'
            });
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while saving SSO Configuration'),
                type: 'error'
            });
            setError(err.response.data);
        }).finally(() => {
            setExecuting(false);
        });
    }

    const toggleClientSecretVisibility = (e) => {
        e.preventDefault();
        setShowClientSecret(current => !current);
    }

    if (loading) {
        return <Loading />
    }

    return <form onSubmit={submit}>
        <div className={`sm:overflow-hidden sm:rounded-md shadow-inner`}>
            <div className="space-y-3 px-4 py-5 sm:p-6">
                <div className="text-lg font-bold bg-base-200 p-2 rounded-md">OIDC Provider Configuration</div>
                <div className="form-control w-full">
                    <label className="label">
                        <span className="label-text">Redirect URI</span>
                    </label>
                    <div className="p-1">
                        <div>{`${window.location.origin}/login/oauth2/code/oidc`}
                            <button title="Copy to Clipboard" className="ml-2 btn btn-ghost btn-xs"
                                onClick={(e) => copyToClipboard(e, window.location.origin + '/login/oauth2/code/oidc', "Redirect URI has been copied to Clipboard")}>
                                <IconClipboard size={16} /></button>
                        </div>
                    </div>
                </div>
                <div className="form-control w-full">
                    <label className="label">
                        <span className="label-text">Client ID</span>
                    </label>
                    <input type="text" placeholder="Provide the Client ID" value={clientId} className="input input-bordered w-full" onChange={e => setClientId(e.target.value)} />
                    {renderErrors(error, 'clientId')}
                </div>

                <div className="form-control w-full">
                    <label className="label">
                        <span className="label-text">Client Secret</span>
                    </label>

                    <div className="input-group">
                        <input type={showClientSecret ? "text" : "password"} name="value" value={clientSecret} onChange={(e) => setClientSecret(e.target.value)} placeholder="Provide the Client Secret" className="input input-bordered w-full" />
                        <button className="btn btn-square" onClick={toggleClientSecretVisibility}>
                            {!showClientSecret && <IconEye size="24" />}
                            {showClientSecret && <IconEyeOff size="24" />}
                        </button>
                    </div>
                    {renderErrors(error, 'clientSecret')}
                </div>

                <div className="form-control w-full">
                    <label className="label">
                        <span className="label-text">Well-Known URL</span>
                    </label>
                    <input type="text" placeholder="Well-Known URL provided by your authentication provider" value={wellKnownUrl} className="input input-bordered w-full" onChange={e => setWellKnownUrl(e.target.value)} />
                    {renderErrors(error, 'wellKnownUrl')}
                </div>

                <div className="form-control w-full">
                    <label className="label">
                        <span className="label-text">Auto Create User on Login</span>
                    </label>
                    <input type="checkbox" className={`toggle toggle-lg ${enableCreateUser ? 'toggle-success' : ''}`} checked={enableCreateUser} onChange={(e) => setEnableCreateUser(e.target.checked)} />
                    {renderErrors(error, 'enableCreateUser')}
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
                        <span className="label-text">Allow login only with SSO</span>
                    </label>
                    <input type="checkbox" className={`toggle toggle-lg ${ssoOnly ? 'toggle-success' : ''}`} checked={ssoOnly} onChange={(e) => setSsoOnly(e.target.checked)} />
                    {renderErrors(error, 'ssoOnly')}
                </div>
            </div>
            <div className="bg-base-200 px-4 py-3 text-right sm:px-6">
                <button disabled={executing} type="submit" className="btn btn-primary">Save Changes</button>
            </div>
        </div>
    </form >
}

export default SSO;