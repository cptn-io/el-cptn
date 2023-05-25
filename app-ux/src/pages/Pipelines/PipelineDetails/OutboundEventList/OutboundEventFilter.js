import ListFilter from "../../../../components/ListFilter";

const statusList = [
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


const OutboundEventFilter = (props) => {
    return <ListFilter {...props} statusList={statusList} />
}

export default OutboundEventFilter;