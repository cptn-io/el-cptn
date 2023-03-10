import { Link } from "react-router-dom";

const Logo = ({ className }) => {
    return <Link to="/home" aria-current="page" aria-label="Homepage" className={`flex-0 btn btn-ghost px-2 ${className}`}>
        <div className="font-title text-primary inline-flex text-3xl">
            <span className="lowercase">Data</span> <span className="text-base-content lowercase">pipelines</span>
        </div>
    </Link>
}

export default Logo;