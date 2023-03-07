import { Fragment, useEffect } from "react";

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

    const getContent = (item) => {
        return <Fragment>
            <div className="flex items-center"><span>
                {item.type === 'success' && <IconCircleCheck size={24} />}
                {item.type === 'info' && <IconInfoCircle size={24} />}
                {item.type === 'error' && <IconCircleX size={24} />}
                {item.type === 'warning' && <IconExclamationCircle size={24} />}</span>
                <span>{item.message}</span>
            </div>
            <button className="clear-btn btn-circle btn btn-ghost btn-sm" onClick={() => onClear(item.id)}><IconX size={16} /></button>
        </Fragment>
    }

    //TODO fix this weird issue that styles are not applied to notification when js expression are used for applying css classes
    if (item.type === 'error') {
        return <div className="alert alert-error shadow-lg">
            {getContent(item)}
        </div>
    } else if (item.type === 'warning') {
        return <div className="alert alert-warning shadow-lg">
            {getContent(item)}
        </div>
    } else if (item.type === 'info') {
        return <div className="alert alert-info shadow-lg">
            {getContent(item)}
        </div>
    } else if (item.type === 'success') {
        return <div className="alert alert-success shadow-lg">
            {getContent(item)}
        </div>
    } else {
        return <div className="alert shadow-lg">
            {getContent(item)}
        </div>
    }
}

export default Notification;