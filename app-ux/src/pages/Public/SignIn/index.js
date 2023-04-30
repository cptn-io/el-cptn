import { IconCircleCheck, IconCircleX } from "@tabler/icons-react";
import axios from "axios";
import { useEffect, useState } from "react";
import { Helmet } from "react-helmet";
import { useSearchParams } from "react-router-dom";

const getErrorMessage = (error) => {
    const errors = {
        'generic': 'An error occurred while trying to sign in.',
        'bad_credentials': 'Invalid email or password.',
        'locked': 'Your account is locked. Please contact your administrator.',
        'disabled': 'Your account is disabled. Please contact your administrator.',
        'csrf': 'An error occurred while trying to get CSRF token. Please try again.'
    }
    return errors[error] || errors['generic'];
}

const SignIn = () => {
    const [csrf, setCsrf] = useState('');
    const [searchParams] = useSearchParams();
    const [error, setError] = useState(searchParams.has("error") ? searchParams.get("error") : null);
    const [logout] = useState(searchParams.has("logout") ? searchParams.get("logout") : null);


    useEffect(() => {
        axios.get("/api/csrf").then((response) => {
            setCsrf(response.data.token);
        }).catch((error) => {
            setError('csrf');
        });
    }, [])

    const setToken = (e) => {
        setCsrf(document.cookie.match(/XSRF-TOKEN=(.*)/)?.[1]);
    }
    return <div className="w-4/5 mx-auto bg-base-300 rounded-lg overflow-hidden md:w-2/4 lg:w-1/4">
        <Helmet>
            <title>Sign In | cptn.io</title>
        </Helmet>
        <div className="p-5">
            {logout &&
                <div className="my-2 alert alert-success">
                    <span><IconCircleCheck className="mr-2" size={24} />{logout === 'idle' ? 'You have been logged out for inactivity.' : 'You are now logged out.'}</span>
                </div>
            }
            {error &&
                <div className="my-2 alert alert-error">
                    <span><IconCircleX className="mr-2" size={24} />{getErrorMessage(error)}</span>
                </div>
            }
            <div className="mb-4">
                <h2 className="text-lg font-semibold uppercase">Sign In</h2>
            </div>
            <div>
                <form method="POST" onSubmit={setToken} action="/login">
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
                    <button
                        type="submit"
                        className="w-full btn-primary py-2 px-4 rounded-md"
                    >
                        Sign In
                    </button>
                </form>
            </div>
        </div>
    </div>
}

export default SignIn;