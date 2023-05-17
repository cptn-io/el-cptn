import { Fragment, useEffect, useState } from "react";
import ReactDOM from "react-dom";

const Modal = (props) => {

    const { title = '', onCancel, large = false } = props;
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
            <div className={`modal-box relative ${large ? 'w-11/12 max-w-screen-2xl' : 'w-full md:w-1/2 max-w-3xl'} px-0 pb-0 pt-4`}>
                <label htmlFor="my-modal-4" className="btn btn-sm btn-circle absolute right-2 top-2">✕</label>
                <h3 className="font-bold text-lg px-4">{title}</h3>
                {props.children}
            </div>
        </div>
    </Fragment>, document.querySelector("#modal"));
}

export default Modal;