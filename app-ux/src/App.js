import LeftNav from "./components/Nav/LeftNav"
import NavBar from "./components/Nav/NavBar"
import { Outlet } from "react-router-dom"
import { ThemeContextProvider } from "./context/ThemeContext"
import { NotificationContextProvider } from "./context/NotificationContext"
import NotificationList from "./components/NotificationList"
import './App.scss';
import { useCallback, useEffect, useState } from "react"
import axios from "axios"
import Loading from "./components/Loading";
import { useIdleTimer, workerTimers } from 'react-idle-timer';
import LogoutModal from "./components/LogoutModal"

const PROMPT_DURATION = 2 * 60 * 1000; //2 min prompt
const TIMEOUT_INTERVAL = 15 * 60 * 1000; //15 min timeout

export default function App() {
  const [loading, setLoading] = useState(true);
  const [showIdleModal, setShowIdleModal] = useState(false);

  const logout = useCallback(() => {
    window.location.href = "/logout?reason=idle";
  }, []);


  const onIdle = useCallback(() => {
    logout();
  }, [logout]);

  const onPrompt = useCallback(() => {
    setShowIdleModal(true);
  }, []);



  const idleTimer = useIdleTimer({ timers: workerTimers, crossTab: true, syncTimers: 2 * 60 * 1000, onIdle, onPrompt, promptBeforeIdle: PROMPT_DURATION, timeout: TIMEOUT_INTERVAL, debounce: 500 })

  const stayLoggedIn = useCallback(() => {
    idleTimer.reset();
    setShowIdleModal(false);
  }, [idleTimer]);

  useEffect(() => {
    axios.interceptors.response.use((response) => {
      return response;
    }, (error) => {
      if (error.response.status === 401) {
        window.location.href = "/signin";
      }
      return Promise.reject(error);
    });

    axios.defaults.xsrfCookieName = "XSRF-TOKEN"
    axios.defaults.xsrfHeaderName = "X-XSRF-TOKEN"

    axios.get("/api/csrf").then((response) => {
      //axios.defaults.headers.common['X-XSRF-TOKEN'] = response.data.token;
    }).catch((error) => {
      window.location.href = "/signin?error=csrf";
    }).finally(() => {
      setLoading(false);
    });

  }, [])

  if (loading) {
    return <Loading />
  }

  return (
    <ThemeContextProvider>
      <NotificationContextProvider>
        <div className="drawer drawer-mobile">
          <input id="page-drawer" type="checkbox" className="drawer-toggle" />
          <div className="drawer-content">
            <NavBar />
            <div className="p-4 pt-6">
              <Outlet />
            </div>
            <NotificationList />
          </div>
          <LeftNav />
        </div>
        {showIdleModal && <LogoutModal onStayLoggedIn={stayLoggedIn} onLogout={logout} getRemainingTime={idleTimer.getRemainingTime} />}
        <div id="modal">
        </div>
      </NotificationContextProvider>
    </ThemeContextProvider>
  )
}