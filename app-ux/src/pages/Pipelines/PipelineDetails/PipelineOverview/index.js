import ContextHelp from "../../../../components/ContextHelp";
import PipelineDetailsCard from "./PipelineDetailsCard";

const PipelineOverview = (props) => {
    const { data, onUpdate } = props;
    return <div className="grid grid-flow-row-dense grid-cols-1 xl:grid-cols-8 gap-4 ">
        <div className="xl:col-span-6">
            <PipelineDetailsCard data={data} onUpdate={onUpdate} />
        </div>
        <div className="hidden xl:col-span-2 xl:block mt-4">
            <ContextHelp page="pipeline-details" />
        </div>
    </div>
}

export default PipelineOverview;