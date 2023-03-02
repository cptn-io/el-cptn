import { Link } from "react-router-dom";

const Logo = ({ className }) => {
    return <Link to="/app/home" aria-current="page" aria-label="Homepage" className={`flex-0 btn btn-ghost px-2 ${className}`}>
        <div className="font-title text-primary inline-flex text-lg transition-all duration-200 md:text-3xl">
            <span className="lowercase">El</span> <span className="text-base-content lowercase">cptn</span>
        </div>
    </Link>
}

export default Logo;