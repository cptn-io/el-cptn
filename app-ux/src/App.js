import LeftNav from "./components/Nav/LeftNav"
import NavBar from "./components/Nav/NavBar"
import { Outlet } from "react-router-dom"
import { ThemeContextProvider } from "./context/ThemeContext"
import { NotificationContextProvider } from "./context/NotificationContext"
import NotificationList from "./components/NotificationList"
import './App.scss';
import { useEffect, useState } from "react"
import axios from "axios"
import Loading from "./components/Loading"

export default function App() {
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axios.interceptors.response.use((response) => {
      return response;
    }, (error) => {
      if (error.response.status === 401) {
        window.location.href = "/signin";
      }
      return Promise.reject(error);
    });

    axios.get("/api/csrf").then((response) => {
      axios.defaults.headers.common['X-XSRF-TOKEN'] = response.data.token;
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
        <div id="modal">
        </div>
      </NotificationContextProvider>
    </ThemeContextProvider>
  )
}