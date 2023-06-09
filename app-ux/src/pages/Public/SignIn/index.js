import { IconCircleCheck, IconCircleX, IconInfoCircle } from "@tabler/icons-react";
import axios from "axios";
import { useEffect, useState } from "react";
import { Helmet } from "react-helmet";
import { useSearchParams } from "react-router-dom";
import Loading from "../../../components/Loading";

const getErrorMessage = (error) => {
    const errors = {
        'generic': 'An error occurred while trying to sign in.',
        'bad_credentials': 'Invalid email or password.',
        'locked': 'Your account is locked. Please contact your administrator.',
        'disabled': 'Your account is disabled. Please contact your administrator.',
        'csrf': 'An error occurred while trying to get CSRF token. Please try again.',
        'demo_user': 'Login with demo user is not allowed for this instance.',
        'user_not_found': 'User not found with this email.',
        'passwordauth_disabled': 'Password authentication is disabled for this instance.'
    }
    return errors[error] || errors['generic'];
}

const SignIn = () => {
    const [loading, setLoading] = useState(true);
    const [csrf, setCsrf] = useState('');
    const [showSSO, setShowSSO] = useState(false);
    const [ssoOnly, setSSOOnly] = useState(false);
    const [searchParams] = useSearchParams();
    const [error, setError] = useState(searchParams.has("error") ? searchParams.get("error") : null);
    const [logout] = useState(searchParams.has("logout") ? searchParams.get("logout") : null);


    useEffect(() => {
        Promise.all([
            axios.get("/api/csrf"),
            axios.get('/api/checksso')
        ]).then((responses) => {
            setCsrf(responses[0].data.token);
            setShowSSO(responses[1].data.ssoEnabled);
            setSSOOnly(responses[1].data.ssoEnabled && responses[1].data.ssoOnly);
        }).catch((error) => {
            setError('csrf');
            setShowSSO(false);
        }).finally(() => {
            setLoading(false);
        });
    }, []);

    const setToken = (e) => {
        const match = /XSRF-TOKEN=(.*)/.exec(document.cookie);
        setCsrf(match?.[1]);
    }
    return <div className="w-4/5 mx-auto bg-base-300 rounded-lg overflow-hidden md:w-2/4 lg:w-1/4">
        <Helmet>
            <title>Sign In | cptn.io</title>
        </Helmet>
        <div className="p-5">
            {logout &&
                <div className="my-2 alert alert-success">
                    <IconCircleCheck size={24} /><span>{logout === 'idle' ? 'You have been logged out for inactivity.' : 'You are now logged out.'}</span>
                </div>
            }
            {error &&
                <div className="my-2 alert alert-error">
                    <IconCircleX size={24} /><span>{getErrorMessage(error)}</span>
                </div>
            }
            <div className="mb-4">
                <h2 className="text-lg font-semibold uppercase">Sign In</h2>
            </div>
            {loading && <div className="flex items-center justify-center"><Loading /></div>}
            {!loading && <div>
                {ssoOnly &&
                    <div className="my-2 alert alert-info">
                        <IconInfoCircle size={24} /><span>Password based auth is disabled.</span>
                    </div>
                }
                {!ssoOnly && <form method="POST" onSubmit={setToken} action="/login">
                    <div className="mb-4">
                        <label htmlFor="username" className="block text-content font-bold mb-2">Email</label>
                        <input
                            type="email"
                            id="username"
                            name="username"
                            className="w-full px-3 py-2 border border-gray-300 rounded-md"
                            autoComplete="username"
                            required
                        />
                    </div>
                    <div className="mb-4">
                        <label htmlFor="password" className="block text-content font-bold mb-2">Password</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            autoComplete="current-password"
                            className="w-full px-3 py-2 border border-gray-300 rounded-md"
                            required
                        />
                    </div>
                    <input name="_csrf" type="hidden" value={csrf} />
                    <div>
                        <button
                            type="submit"
                            className="w-full btn btn-primary rounded-md"
                        >
                            Sign In
                        </button>
                    </div>
                </form>
                }
                {showSSO && <div className="mt-2">
                    <a className="w-full btn btn-accent rounded-md" alt="Login with SSO" href="/login?sso=true">
                        Sign In with SSO
                    </a>
                </div>}
            </div>}

        </div>
    </div>
}

export default SignIn;