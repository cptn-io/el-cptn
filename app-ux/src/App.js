import LeftNav from "./components/Nav/LeftNav"
import NavBar from "./components/Nav/NavBar"
import { Outlet } from "react-router-dom"
import { ThemeContextProvider } from "./context/ThemeContext"
import { NotificationContextProvider } from "./context/NotificationContext"
import NotificationList from "./components/NotificationList"
import './App.scss';

export default function App() {
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
      </NotificationContextProvider>
    </ThemeContextProvider>
  )
}