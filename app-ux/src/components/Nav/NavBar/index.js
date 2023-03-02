import { ChevronDownIcon, MoonIcon, UserIcon, SunIcon } from '@heroicons/react/24/solid'
import { navItems } from "../data";
import Logo from '../Logo';
import { Link, useLocation } from 'react-router-dom';
import useThemes from '../../../hooks/useThemes';

const NavBar = () => {
    const { currentTheme, changeTheme } = useThemes();

    const location = useLocation();
    return <div className="sticky top-0 z-30 flex h-16 w-full justify-center bg-opacity-90 backdrop-blur transition-all duration-100 
    bg-base-100 text-base-content shadow-sm">
        <div className="navbar bg-base-100">
            <div className="navbar-start">
                <div className="dropdown">
                    <label tabIndex={0} className="btn btn-ghost p-2 lg:hidden">
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 6h16M4 12h8m-8 6h16" /></svg>
                    </label>
                    <ul tabIndex={0} className="menu menu-compact dropdown-content mt-3 p-2 shadow bg-base-100 rounded-box w-52">
                        {navItems.map(item => <li key={item.label} className="flex gap-4"><Link to={item.url} className={location.pathname.startsWith(item.url) ? 'active' : ''}>{item.icon()}{item.label}</Link></li>)}
                    </ul>
                </div>
                <Logo className="lg:hidden" />
            </div>
            <div className="navbar-center hidden lg:flex">
            </div>
            <div className="navbar-end">
                <button className="btn btn-ghost" onClick={() => changeTheme(currentTheme === 'dark' ? 'light' : 'dark')}>
                    {currentTheme === 'dark' ? <SunIcon className="h-4 w-4 transition-all duration-200 md:h-6 md:w-6" />
                        : <MoonIcon className="h-4 w-4 transition-all duration-200 md:h-6 md:w-6" />}
                </button>

                <div className="dropdown dropdown-end">
                    <label tabIndex={0} className="btn btn-ghost p-2">
                        <UserIcon className="h-4 w-4 transition-all duration-200 md:h-6 md:w-6" />
                        <ChevronDownIcon className="h-4 mx-2 w-4" />
                    </label>
                    <ul tabIndex={0} className="dropdown-content menu p-2 shadow bg-base-100 rounded-box w-52">
                        <li><button>Profile</button></li>
                        <li><button>Logout</button></li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
}

export default NavBar;