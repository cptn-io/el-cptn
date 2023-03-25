import { IconCheck, IconClipboard, IconEye, IconEyeOff, IconX } from "@tabler/icons-react";
import { useState } from "react";
import useNotifications from "../../../../hooks/useNotifications";

const PipelineDetailsCard = (props) => {
    const { data } = props;
    const { addNotification } = useNotifications();

    const [showPrimary, setShowPrimary] = useState(false);
    const [showSecondary, setShowSecondary] = useState(false);

    const copyToClipboard = (key, message) => {
        navigator.clipboard.writeText(key)
        addNotification({
            message,
            type: 'info'
        })
    }

    return <div className="card bg-base-100 mb-4">
        <div className="card-body p-4">
            <div className="text-lg font-bold bg-base-200 p-2 rounded-md">Pipeline Details</div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Pipeline Name</span>
                </label>
                <div className="p-1 text-lg">{data.name}</div>
            </div>
            <div className="flex">
                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Active</span>
                    </label>
                    <div className="p-1">{data.active ? <IconCheck size={24} /> : <IconX size={24} />}</div>
                </div>
            </div>
            <div className="text-lg font-bold bg-base-200 p-2 rounded-md">Data Source</div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Source</span>
                </label>
                <div className="p-1 text-lg">{data.source.name}</div>
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Event URL</span>
                </label>
                <div className="p-1 break-all">{`${window.location.origin}/api/source/${data.source.id}/event`}
                    <button title="Copy to Clipboard" className="ml-2 btn btn-ghost btn-xs"
                        onClick={() => copyToClipboard(`${window.location.origin}/api/source/${data.source.id}/event`, "URL has been copied to Clipboard")}>
                        <IconClipboard size={16} /></button>
                </div>
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Primary key</span>
                </label>
                <div className="p-1">
                    {data.source.secured ? <div>{showPrimary ? data.source.primaryKey : '********************'}
                        <button title="Show key" disabled={!data.source.secured} className="ml-2 btn btn-ghost btn-xs"
                            onClick={() => setShowPrimary(current => !current)}>{showPrimary ?
                                <IconEyeOff size={16} /> : <IconEye size={16} />}</button>
                        <button title="Copy to Clipboard" disabled={!data.source.secured} className="ml-2 btn btn-ghost btn-xs"
                            onClick={() => copyToClipboard(data.source.primaryKey, "Key has been copied to Clipboard")}>
                            <IconClipboard size={16} /></button>
                    </div>
                        : <div>Disabled</div>}
                </div>
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Secondary key</span>
                </label>
                <div className="p-1">
                    {data.source.secured ? <div>{showSecondary ? data.source.secondaryKey : '********************'}
                        <button title="Show key" disabled={!data.source.secured} className="ml-2 btn btn-ghost btn-xs"
                            onClick={() => setShowSecondary(current => !current)}>{showSecondary ?
                                <IconEyeOff size={16} /> : <IconEye size={16} />}</button>
                        <button title="Copy to Clipboard" disabled={!data.source.secured} className="ml-2 btn btn-ghost btn-xs"
                            onClick={() => copyToClipboard(data.source.secondaryKey, "Key has been copied to Clipboard")}>
                            <IconClipboard size={16} /></button>
                    </div>
                        : <div>Disabled</div>}
                </div>
            </div>
            <div className="flex">
                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Active</span>
                    </label>
                    <div className="p-1">{data.source.active ? <IconCheck size={24} /> : <IconX size={24} />}</div>
                </div>
            </div>

            <div className="text-lg font-bold bg-base-200 p-2 rounded-md">Destination</div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Destination Name</span>
                </label>
                <div className="p-1 text-lg">{data.destination.name}</div>
            </div>
            <div className="flex">
                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Active</span>
                    </label>
                    <div className="p-1">{data.destination.active ? <IconCheck size={24} /> : <IconX size={24} />}</div>
                </div>
            </div>
        </div>
    </div>
}

export default PipelineDetailsCard;