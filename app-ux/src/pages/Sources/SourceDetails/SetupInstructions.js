import { useState } from "react";
import { IconEye, IconEyeOff } from '@tabler/icons-react';

const SetupInstructions = (props) => {
    const { data } = props;
    const [showPrimary, setShowPrimary] = useState(false);
    const [showSecondary, setShowSecondary] = useState(false);

    return <div className="card bg-base-100 shadow mt-4">
        <div className="card-body">
            <h2 className="card-title">Setup instructions</h2>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Event URL</span>
                </label>
                <div className="label break-all">{`${window.location.origin}/api/source/${data.id}/event`}</div>
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Primary key</span>
                </label>
                <div className="label">
                    {data.secured ? <div>{showPrimary ? data.primaryKey : '********************'}<button disabled={!data.secured} className="ml-2 btn btn-ghost btn-xs" onClick={() => setShowPrimary(current => !current)}>{showPrimary ? <IconEyeOff size={16} /> : <IconEye size={16} />}</button></div>
                        : <div>Disabled</div>}
                </div>
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Secondary key</span>
                </label>
                <div className="label">
                    {data.secured ? <div>{showSecondary ? data.secondaryKey : '********************'}<button disabled={!data.secured} className="ml-2 btn btn-ghost btn-xs" onClick={() => setShowSecondary(current => !current)}>{showSecondary ? <IconEyeOff size={16} /> : <IconEye size={16} />}</button></div>
                        : <div>Disabled</div>}
                </div>
            </div>
            <div className="card-actions justify-end">
                <button disabled={!data.secured} className="btn btn-secondary">Rotate keys</button>
            </div>
        </div>
    </div>
}

export default SetupInstructions;