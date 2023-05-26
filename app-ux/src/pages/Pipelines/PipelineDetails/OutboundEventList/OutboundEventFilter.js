import ListFilter from "../../../../components/ListFilter";
import { outboundEventStatusList } from "../../PipelineFilter";

const OutboundEventFilter = (props) => {
    return <ListFilter {...props} statusList={outboundEventStatusList} />
}

export default OutboundEventFilter;