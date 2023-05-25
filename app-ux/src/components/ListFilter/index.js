const ListFilter = (props) => {
    const { status, setStatus, statusList } = props;

    return <div className="flex flex-row-reverse mb-1">
        <div className="flex items-center">
            <div className="font-bold">Filters:</div>
            <div className="ml-2 btn-group">
                {statusList.map(item => <button key={item.label} onClick={() => setStatus(item.value)} className={`btn btn-ghost btn-xs ${status === item.value ? 'text-secondary' : ''}`}>{item.label}</button>)}
            </div>
        </div>
    </div>
}

export default ListFilter;