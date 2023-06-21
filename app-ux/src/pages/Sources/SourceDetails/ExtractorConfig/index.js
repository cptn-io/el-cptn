import ContextHelp from "../../../../components/ContextHelp";
import Extractor from "./Extractor";

const ExtractorConfig = ({ sourceId }) => {

    return <div className="grid grid-flow-row-dense grid-cols-1 xl:grid-cols-8 gap-4 ">
        <div className="xl:col-span-6">
            <Extractor sourceId={sourceId} />
        </div>
        <div className="hidden xl:col-span-2 xl:block mt-4">
            <ContextHelp page="extractor-config" />
        </div>
    </div>

}

export default ExtractorConfig;