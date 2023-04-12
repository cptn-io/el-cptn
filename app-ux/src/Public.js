import { Outlet } from "react-router-dom";
import './Public.scss';
import { ThemeContextProvider } from "./context/ThemeContext";

const Public = () => {
    return <ThemeContextProvider>
        <div className="public grid place-items-center h-screen">
            <Outlet />
        </div>

    </ThemeContextProvider>
}

export default Public;