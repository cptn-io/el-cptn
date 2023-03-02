import { useContext } from 'react';
import NotificationContext from '../context/NotificationContext';

const useNotifications = () => useContext(NotificationContext);

export default useNotifications;