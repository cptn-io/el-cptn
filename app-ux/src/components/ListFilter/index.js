const ListFilter = (props) => {
    const { status, setStatus, statusList, label = "Filters" } = props;

    return <div className="flex flex-row-reverse mb-1">
        <div className="flex items-center">
            <div className="font-bold">{label}</div>
            <div className="ml-2 join">
                {statusList.map(item => <button key={item.label} onClick={() => setStatus(item.value)} className={`btn btn-ghost join-item btn-xs ${status === item.value ? 'text-secondary' : ''}`}>{item.label}</button>)}
            </div>
        </div>
    </div>
}

export default ListFilter;