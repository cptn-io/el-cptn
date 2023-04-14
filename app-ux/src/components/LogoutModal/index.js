import { Fragment, useEffect, useState } from "react";
import ReactDOM from "react-dom";

const LogoutModal = (props) => {
    const { onStayLoggedIn, onLogout, getRemainingTime } = props;
    const [opened, setOpened] = useState(true);
    const [remaining, setRemaining] = useState(Math.ceil(getRemainingTime() / 1000));

    useEffect(() => {
        const interval = setInterval(() => {
            setRemaining(Math.ceil(getRemainingTime() / 1000))
        }, 1000)

        return () => {
            clearInterval(interval)
        }
    });

    useEffect(() => {
        const close = (e) => {
            if (e.key === 'Escape') {
                onStayLoggedIn();
            }
        }
        window.addEventListener('keydown', close)
        return () => window.removeEventListener('keydown', close)
    }, [onStayLoggedIn])

    useEffect(() => {
        if (!opened) {
            onStayLoggedIn();
        }
    }, [opened, onStayLoggedIn])

    return ReactDOM.createPortal(<Fragment><input type="checkbox" id="my-modal-4" defaultChecked={opened}
        onChange={(e) => setOpened(e.target.checked)}
        className="modal-toggle" />
        <div className="modal">
            <div className="modal-box relative">
                <label htmlFor="my-modal-4" className="btn btn-sm btn-circle absolute right-2 top-2">✕</label>
                <h3 className="font-bold text-lg">Auto-Logout</h3>
                <p className="mt-4">For security purposes, you will be automatically logged out after 15 minutes of inactivity. You will be automatically logged out in {remaining} seconds</p>
                <div className="modal-action">
                    <button className="btn" onClick={onLogout}>Logout</button>
                    <button className="btn btn-primary" onClick={onStayLoggedIn}>Stay Logged In</button>
                </div>
            </div>
        </div>
    </Fragment>, document.querySelector("#modal"));
}

export default LogoutModal;