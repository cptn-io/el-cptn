import { content } from "./content";

const ContextHelp = ({ page }) => {
    const contentObj = content[page];
    if (!contentObj) {
        return null;
    }

    return <div className="px-4 sm:px-0">
        <h3 className="text-base font-semibold leading-6">{contentObj.title}</h3>
        <div className="text-base-content mt-1 whitespace-pre-line">
            {contentObj.body}
        </div>
    </div>
}

export default ContextHelp;