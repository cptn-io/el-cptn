import { Link } from "react-router-dom";

const Logo = ({ className }) => {
    return <Link to={window.location.pathname.startsWith("/app") ? "/home" : "/"} aria-current="page" aria-label="Homepage" className={`flex-0 btn btn-ghost px-2 ${className}`}>
        <div className="font-title text-primary inline-flex text-3xl">
            <span className="lowercase">CPTN</span> <span className="text-base-content lowercase">.io</span>
        </div>
    </Link>
}

export default Logo;