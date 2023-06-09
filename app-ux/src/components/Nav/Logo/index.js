import { Link } from "react-router-dom";

const Logo = ({ className }) => {
    return <Link to={window.location.pathname.startsWith("/app") ? "/home" : "/"} aria-current="page" aria-label="Homepage" className={`flex-0 btn btn-ghost px-2 ${className || ''}`}>
        <img alt="Logo" src="/logo.svg" className="nav-logo" />
        <img alt="Logo" src="/logo-darkbg.svg" className="nav-logo darkbg" />
    </Link>
}

export default Logo;