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


const ListFilter = (props) => {
    const { status, setStatus } = props;

    return <div className="flex flex-row-reverse">
        <div className="flex items-center">
            <div className="font-bold">Filters:</div>
            <div className="ml-2 btn-group">
                {statusList.map(item => <button key={item.label} onClick={() => setStatus(item.value)} className={`btn btn-ghost btn-xs ${status === item.value ? 'text-primary' : ''}`}>{item.label}</button>)}
            </div>
        </div>
    </div>
}

export default ListFilter;