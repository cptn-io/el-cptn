import { IconCheck, IconX, IconEye, IconEyeOff } from '@tabler/icons-react';
import { useState } from "react";

const BasicInformation = (props) => {
    const { data } = props;
    const [showPrimary, setShowPrimary] = useState(false);
    const [showSecondary, setShowSecondary] = useState(false);
    return <div className="card bg-base-100 mb-4">
        <div className="card-body">
            <h2 className="card-title">Source Details</h2>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Source Name</span>
                </label>
                <div className="p-1 text-lg">{data.name}</div>
            </div>
            <div className="flex">
                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Secured</span>
                    </label>
                    <div className="p-1">{data.secured ? <IconCheck size={24} /> : <IconX size={24} />}</div>
                </div>
                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Active</span>
                    </label>
                    <div className="p-1">{data.active ? <IconCheck size={24} /> : <IconX size={24} />}</div>
                </div>
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Event URL</span>
                </label>
                <div className="p-1 break-all">{`${window.location.origin}/api/source/${data.id}/event`}</div>
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Primary key</span>
                </label>
                <div className="p-1">
                    {data.secured ? <div>{showPrimary ? data.primaryKey : '********************'}<button disabled={!data.secured} className="ml-2 btn btn-ghost btn-xs" onClick={() => setShowPrimary(current => !current)}>{showPrimary ? <IconEyeOff size={16} /> : <IconEye size={16} />}</button></div>
                        : <div>Disabled</div>}
                </div>
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Secondary key</span>
                </label>
                <div className="p-1">
                    {data.secured ? <div>{showSecondary ? data.secondaryKey : '********************'}<button disabled={!data.secured} className="ml-2 btn btn-ghost btn-xs" onClick={() => setShowSecondary(current => !current)}>{showSecondary ? <IconEyeOff size={16} /> : <IconEye size={16} />}</button></div>
                        : <div>Disabled</div>}
                </div>
            </div>
            <div className="card-actions justify-end">
                <button disabled={!data.secured} className="btn btn-secondary">Rotate keys</button>
                <button className="btn">Edit Source</button>
            </div>
        </div>
    </div>
}

export default BasicInformation;