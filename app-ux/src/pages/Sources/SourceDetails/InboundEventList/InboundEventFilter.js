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
        label: 'Sent to Pipelines',
        value: 'COMPLETED'
    },
    {
        label: 'Failed',
        value: 'FAILED'
    }
]


const InboundEventFilter = (props) => {

    return <ListFilter {...props} statusList={statusList} />
}

export default InboundEventFilter;