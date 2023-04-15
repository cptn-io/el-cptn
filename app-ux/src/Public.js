import { Outlet } from "react-router-dom";
import './Public.scss';
import { ThemeContextProvider } from "./context/ThemeContext";
import Logo from "./components/Nav/Logo";

const Public = () => {
    return <ThemeContextProvider>
        <div className="navbar">
            <Logo />
        </div>
        <div className="public grid place-items-center h-screen">
            <Outlet />
        </div>

    </ThemeContextProvider>
}

export default Public;