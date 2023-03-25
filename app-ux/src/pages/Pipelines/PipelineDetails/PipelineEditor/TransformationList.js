import { IconPlus, IconX } from "@tabler/icons-react";
import pluralize from "pluralize";
import { useState } from "react";
import ConfirmModal from "../../../../components/ConfirmModal";

const TransformationList = (props) => {
    const { selectTransformation, transformations = [], unlinkTransformation } = props;
    const [deleteTransformationId, setDeleteTransformationId] = useState(null);

    return <div className="overflow-x-scroll overflow-y-scroll">
        <table className="table table-zebra w-full">
            <thead>
                <tr>
                    <th>Transformations</th>
                    <th className="text-center">
                        <button className="btn btn-sm btn-ghost btn-square" onClick={selectTransformation}>
                            <IconPlus width={32} />
                        </button>
                    </th>
                </tr>
            </thead>
            <tbody>
                {transformations.map(transformation => {
                    return <tr key={transformation.id}>
                        <td>{transformation.name}</td>
                        <td className="text-center">
                            <button className="btn btn-sm btn-ghost btn-square" onClick={() => setDeleteTransformationId(transformation.id)}>
                                <IconX width={32} />
                            </button>
                        </td>
                    </tr>
                })}
                {transformations.length === 0 && <tr><td colSpan={2}>No associated transformations</td></tr>}
            </tbody>
            {transformations.length > 0 && <tfoot>
                <tr>
                    <th colSpan={2}>{pluralize('transformation', transformations.length, true)}</th>
                </tr>
            </tfoot>}
        </table>
        {deleteTransformationId && <ConfirmModal title="Are you sure?"
            message="Are you sure you want to remove the transformation? Pipeline mappings must be updated to ensure data is correctly processed."
            onConfirm={() => { unlinkTransformation(deleteTransformationId); setDeleteTransformationId(null); }} onCancel={() => setDeleteTransformationId(null)} />}
    </div>
}

export default TransformationList;