import ListFilter from "../../components/ListFilter";

const statusList = [
    {
        label: 'None',
        value: null
    },
    {
        label: 'Queued events',
        value: 'QUEUED'
    },
    {
        label: 'Completed events',
        value: 'COMPLETED'
    },
    {
        label: 'Failed events',
        value: 'FAILED'
    }
]


const PipelineFilter = (props) => {
    return <ListFilter {...props} statusList={statusList} />
}

export default PipelineFilter;