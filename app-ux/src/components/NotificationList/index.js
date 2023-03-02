import { Fragment } from "react";
import useNotifications from "../../hooks/useNotifications";
import Notification from "./Notification";
import './index.scss';

const NotificationList = () => {
    const { notifications, clearNotification } = useNotifications();
    return <Fragment>
        <div className="mt-12 toast toast-top toast-end">
            {notifications.map(item => <Notification key={item.id} item={item} onClear={clearNotification} />)}
        </div>
    </Fragment>
}

export default NotificationList;