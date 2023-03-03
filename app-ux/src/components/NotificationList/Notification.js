import { useEffect } from "react";

import { IconCircleX, IconExclamationCircle, IconInfoCircle, IconCircleCheck, IconX } from '@tabler/icons-react';

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

    return <div className={`alert alert-${item.type} shadow-lg`}>
        <div className="flex items-center"><span>
            {item.type === 'success' && <IconCircleCheck width={24} />}
            {item.type === 'info' && <IconInfoCircle width={24} />}
            {item.type === 'error' && <IconCircleX width={24} />}
            {item.type === 'warning' && <IconExclamationCircle width={24} />}</span>
            <span>{item.message}</span>
        </div>
        <button className="clear-btn btn-circle btn btn-ghost btn-sm" onClick={() => onClear(item.id)}><IconX width={16} /></button>
    </div>
}

export default Notification;