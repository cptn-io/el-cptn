import { navItems } from "../data";
import find from 'lodash/find';
import { Link } from "react-router-dom";

const PageTitle = (props) => {
    const navItem = find(navItems, ['key', props.itemKey])
    return <div>
        {props.breadcrumbs && <div>
            <div className="text-sm breadcrumbs">
                <ul>
                    {
                        props.breadcrumbs?.map(item => <li key={item.label}><Link to={item.url}>{item.label}</Link></li>)
                    }
                    <li>{props.label || navItem.label}</li>
                </ul>
            </div>
        </div>}
        <div className="flex justify-between mb-4">
            <div className="flex items-center">
                <navItem.Icon size={36} />
                <div className="text-xl transition-all duration-200 md:text-3xl font-bold ml-2">{props.label || navItem.label}</div>
            </div>
            <div>
                {props.children}
            </div>
        </div></div>
}

export default PageTitle;