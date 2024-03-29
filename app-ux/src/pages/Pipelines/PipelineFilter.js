import ListFilter from "../../components/ListFilter";

export const outboundEventStatusList = [
    {
        label: 'None',
        value: null
    },
    {
        label: 'Queued',
        value: 'QUEUED'
    },
    {
        label: 'Completed',
        value: 'COMPLETED'
    },
    {
        label: 'Failed',
        value: 'FAILED'
    }
]


const PipelineFilter = (props) => {
    return <ListFilter {...props} label="Filter by event status:" statusList={outboundEventStatusList} />
}

export default PipelineFilter;