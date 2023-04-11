import axios from "axios";
import { useEffect, useState } from "react";

const SignIn = () => {
    const [csrf, setCsrf] = useState('');
    useEffect(() => {
        axios.get("/api/csrf").then((response) => {
            setCsrf(response.data.token);
        }).catch((error) => {
            console.log(error);
        });
    }, [])
    return <div className="w-4/5 mx-auto bg-white rounded-lg overflow-hidden md:w-2/4 lg:w-1/4">
        <div className="p-4">
            <div className="mb-4">
                <h2 className="text-lg font-semibold uppercase">Sign In</h2>
            </div>
            <div>
                <form method="POST" action="/login">
                    <div className="mb-4">
                        <label htmlFor="username" className="block text-gray-700 font-bold mb-2">Email</label>
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
                        <label htmlFor="password" className="block text-gray-700 font-bold mb-2">Password</label>
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