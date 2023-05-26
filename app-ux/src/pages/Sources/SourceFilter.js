import ListFilter from "../../components/ListFilter";

export const inboundEventStatusList = [
    {
        label: 'None',
        value: null
    },
    {
        label: 'Queued',
        value: 'QUEUED'
    },
    {
        label: 'Sent to Pipelines',
        value: 'COMPLETED'
    },
    {
        label: 'Failed',
        value: 'FAILED'
    }
]


const SourceFilter = (props) => {
    return <ListFilter {...props} label="Filter by event status:" statusList={inboundEventStatusList} />
}

export default SourceFilter;