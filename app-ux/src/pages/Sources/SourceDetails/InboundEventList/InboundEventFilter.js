import ListFilter from "../../../../components/ListFilter";
import { inboundEventStatusList } from "../../SourceFilter";


const InboundEventFilter = (props) => {

    return <ListFilter {...props} statusList={inboundEventStatusList} />
}

export default InboundEventFilter;