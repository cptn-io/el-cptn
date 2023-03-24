import Pagination from "../../components/Pagination";
import { IconCheck, IconX } from '@tabler/icons-react';
import { Link } from "react-router-dom";

const PipelineList = (props) => {
    const { data, totalCount, isRelatedList = false } = props;
    return <>
        <div className="table-container">
            <table className={`table data-table table-zebra w-full ${!isRelatedList ? '' : 'drop-shadow-sm'}`}>
                <thead>
                    <tr>
                        <th className={`${!isRelatedList ? 'bg-base-300' : 'bg-base-200'} text-left w-1/3`}>Name</th>
                        <th className={`${!isRelatedList ? 'bg-base-300' : 'bg-base-200'} text-left w-1/4`}>Source</th>
                        <th className={`${!isRelatedList ? 'bg-base-300' : 'bg-base-200'} text-left w-1/4`}>Destination</th>
                        <th className={`${!isRelatedList ? 'bg-base-300' : 'bg-base-200'} text-left w-1/3`}>Active</th>
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
            </table>
        </div>
        <Pagination totalCount={totalCount} />
    </>
}

export default PipelineList;