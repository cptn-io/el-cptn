import { Link, useLocation } from "react-router-dom";
import Logo from "../Logo";
import { navItems } from "../data";

const LeftNav = () => {
    const location = useLocation();
    return <div className="drawer-side ">
        <label htmlFor="page-drawer" className="drawer-overlay"></label>
        <aside className="bg-base-200 w-80 hidden md:block">
            <div className="z-20 backdrop-blur sticky top-0 items-center gap-2 px-4 py-2 hidden lg:flex ">
                <Logo />
            </div>
            <ul className="menu flex flex-col p-0 px-4">
                {navItems.map((item) => <li key={item.label} className="flex gap-4">
                    <Link to={item.url} className={location.pathname.startsWith(item.url) ? 'active' : ''}><item.Icon size={24} />{item.label}</Link>
                </li>)}
            </ul>
        </aside>

    </div>
}

export default LeftNav;