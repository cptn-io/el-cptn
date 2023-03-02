import { createContext, useCallback, useState } from "react";
import remove from 'lodash/remove';

const NotificationContext = createContext()

export const NotificationContextProvider = (props) => {
    const [notifications, setNotifications] = useState([]);

    const addNotification = useCallback((notification) => {
        notification.type = notification.type || 'error';
        notification.id = Math.random().toString(36).substring(2);
        setNotifications(current => [notification, ...current]);
    }, []);

    const clearNotification = id => {
        setNotifications(remove(notifications, item => item.id !== id));
    };

    return <NotificationContext.Provider value={{
        addNotification,
        clearNotification,
        notifications
    }}>{props.children}</NotificationContext.Provider>
};

export default NotificationContext;