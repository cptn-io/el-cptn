import { useEffect } from "react";
import { CheckCircleIcon, ExclamationTriangleIcon, InformationCircleIcon, XCircleIcon, XMarkIcon } from "@heroicons/react/24/solid";
const Notification = (props) => {
    const { item, onClear } = props;

    useEffect(() => {
        let timeoutId;
        if (item.id) {
            timeoutId = setTimeout(() => {
                onClear(item.id);
            }, item.type === 'error' ? 5000 : 3000);
        }
        return () => {
            if (timeoutId) {
                clearTimeout(timeoutId);
            }
        }
    }, [item, onClear]);



    return <div className={`alert shadow-lg alert-` + item.type}>
        <div className="flex items-center"><span>
            {item.type === 'success' && <CheckCircleIcon className="h-6 w-6 mr-1" />}
            {item.type === 'info' && <InformationCircleIcon className="h-6 w-6 mr-1" />}
            {item.type === 'error' && <XCircleIcon className="h-6 w-6 mr-1" />}
            {item.type === 'warning' && <ExclamationTriangleIcon className="h-6 w-6 mr-1" />}</span>
            <span>{item.message}</span>
        </div>
        <button className="clear-btn btn-circle btn btn-ghost btn-sm" onClick={() => onClear(item.id)}><XMarkIcon className="h-4 w-4" /></button>
    </div>
}

export default Notification;