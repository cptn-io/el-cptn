import Pagination from "../../components/Pagination";
import { IconCheck, IconCirclePlus, IconX } from '@tabler/icons-react';
import { Link } from "react-router-dom";

const renderNoPipelines = (isRelatedList) => {
    return <div className="flex flex-col justify-center my-5">
        <div className="flex justify-center mb-4 text-primary">
            <img alt="Create new Pipeline" src="/undraw/thoughts.svg" className="max-w-3/5" />
        </div>
        <div className="flex justify-center mb-4 text-center">
            {isRelatedList ? 'There are no associated Pipelines. Click the button below to start building a new Pipeline' : 'There are no pipelines. Click the button below to start building a new Pipeline'}
        </div>
        <div className="flex justify-center">
            <Link to="/pipelines/new" className="btn btn-md md:btn-lg"><IconCirclePlus size={36} className="mr-2" />New Pipeline</Link>
        </div>
    </div>
}
const PipelineList = (props) => {
    const { data, totalCount, isRelatedList = false } = props;
    return <>
        <div className="table-container">
            {totalCount > 0 ?
                <table className={`table data-table table-zebra w-full ${!isRelatedList ? '' : 'drop-shadow-sm'}`}>
                    <thead>
                        <tr>
                            <th className={`${!isRelatedList ? 'bg-base-300' : 'bg-base-200'} text-left w-1/3`}>Name</th>
                            <th className={`${!isRelatedList ? 'bg-base-300' : 'bg-base-200'} text-left w-1/4`}>Source</th>
                            <th className={`${!isRelatedList ? 'bg-base-300' : 'bg-base-200'} text-left w-1/4`}>Destination</th>
                            <th className={`${!isRelatedList ? 'bg-base-300' : 'bg-base-200'} text-center w-1/3`}>Active</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.map(pipeline => <tr key={pipeline.id}>
                            <td className="whitespace-pre-wrap break-words"><Link to={`/pipelines/${pipeline.id}`}>{pipeline.name}</Link></td>
                            <td className="whitespace-pre-wrap break-words">{pipeline.source.name}</td>
                            <td className="whitespace-pre-wrap break-words">{pipeline.destination.name}</td>
                            <td className="text-center"><div className="flex justify-center">{pipeline.active ? <IconCheck size={24} /> : <IconX size={24} />}</div></td>
                        </tr>)}
                    </tbody>
                </table> : renderNoPipelines(isRelatedList)}
        </div>
        {totalCount > 0 && <Pagination totalCount={totalCount} />}
    </>
}

export default PipelineList;