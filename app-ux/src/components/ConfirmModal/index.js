import { Fragment, useEffect, useState } from "react";
import ReactDOM from "react-dom";

const ConfirmModal = (props) => {

    const { title = 'Confirm', message = 'Are you sure?', onConfirm, onCancel } = props;
    const [opened, setOpened] = useState(true);
    useEffect(() => {
        const close = (e) => {
            if (e.key === 'Escape') {
                onCancel();
            }
        }
        window.addEventListener('keydown', close)
        return () => window.removeEventListener('keydown', close)
    }, [onCancel])

    useEffect(() => {
        if (!opened) {
            onCancel();
        }
    }, [opened, onCancel])

    return ReactDOM.createPortal(<Fragment><input type="checkbox" id="my-modal-4" defaultChecked={opened}
        onChange={(e) => setOpened(e.target.checked)}
        className="modal-toggle" />
        <div className="modal">
            <div className="modal-box relative">
                <label htmlFor="my-modal-4" className="btn btn-sm btn-circle absolute right-2 top-2">✕</label>
                <h3 className="font-bold text-lg">{title}</h3>
                <p className="mt-4">{message}</p>
                <div className="modal-action">
                    <button className="btn" onClick={onCancel}>No</button>
                    <button className="btn btn-primary" onClick={onConfirm}>Yes</button>
                </div>
            </div>
        </div>
    </Fragment>, document.querySelector("#modal"));
}

export default ConfirmModal;