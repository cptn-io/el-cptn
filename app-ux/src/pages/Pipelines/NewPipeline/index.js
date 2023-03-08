import { Fragment, useState } from "react";
import PageTitle from "../../../components/Nav/PageTitle";
import { breadcrumbs } from "..";
import { Link, useNavigate } from "react-router-dom";
import useNotifications from "../../../hooks/useNotifications";
import { renderErrors } from "../../../common/formHelpers";
import { IconSearch } from "@tabler/icons-react";
import ItemSelectorModal from "../../../components/ItemSelectorModal";
import axios from "axios";
import get from "lodash/get";

const NewPipeline = (props) => {
    const navigate = useNavigate();
    const { addNotification } = useNotifications();
    const [name, setName] = useState('');
    const [active, setActive] = useState(true);
    const [source, setSource] = useState(null);
    const [destination, setDestination] = useState(null);

    const [executing, setExecuting] = useState(false);
    const [error, setError] = useState({ message: null, details: [] });
    const [showSourceSelector, setShowSourceSelector] = useState(false);
    const [showDestinationSelector, setShowDestinationSelector] = useState(false);

    const resetAll = () => {
        setName('');
        setSource(null);
        setDestination(null);
        setActive(true);
        clearErrors();
    }

    const clearErrors = () => {
        setError({ message: null, details: [] });
    }

    const onSourceSelection = (item) => {
        setSource(item);
        setShowSourceSelector(false);
    }

    const onDestinationSelection = (item) => {
        setDestination(item);
        setShowDestinationSelector(false);
    }
    const submit = (e) => {
        e.preventDefault();
        clearErrors();
        setExecuting(true);

        const payload = {
            name,
            source,
            destination,
            active
        };
        axios.post('/api/pipeline', payload).then(response => {
            resetAll();
            addNotification({
                message: 'Pipeline has been created',
                type: 'success'
            });
            navigate('/pipelines');
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while creating Pipeline'),
                type: 'error'
            });
            setError(err.response.data);
        }).finally(() => {
            setExecuting(false);
        })
    };

    return <Fragment>
        <PageTitle itemKey="pipelines" label="New Pipeline" breadcrumbs={breadcrumbs} />
        <div className="md:grid md:grid-cols-3 md:gap-6">
            <div className="mt-5 md:col-span-2 md:mt-0">
                <form onSubmit={submit}>
                    <div className="shadow-inner sm:overflow-hidden sm:rounded-md">
                        <div className="space-y-3 px-4 py-5 sm:p-6">

                            <div className="form-control w-full">
                                <label className="label">
                                    <span className="label-text">Pipeline Name</span>
                                </label>
                                <input type="text" placeholder="Provide a name for the Pipeline" value={name} className="input input-bordered w-full" onChange={e => setName(e.target.value)} />
                                {renderErrors(error, 'name')}
                            </div>

                            <div className="form-control w-full">
                                <label className="label">
                                    <span className="label-text">Select a Source</span>
                                </label>
                                <div className="input-group">
                                    <input type="text" readOnly disabled placeholder="Select" value={source?.name || ''} className="input input-bordered" />
                                    <button className="btn btn-square" onClick={e => { e.preventDefault(); setShowSourceSelector(true) }}>
                                        <IconSearch width={24} />
                                    </button>
                                </div>
                                {renderErrors(error, 'source')}
                            </div>

                            <div className="form-control w-full">
                                <label className="label">
                                    <span className="label-text">Select a Destination</span>
                                </label>
                                <div className="input-group">
                                    <input type="text" readOnly disabled placeholder="Select" value={destination?.name || ''} className="input input-bordered" />
                                    <button className="btn btn-square" onClick={e => { e.preventDefault(); setShowDestinationSelector(true) }}>
                                        <IconSearch width={24} />
                                    </button>
                                </div>
                                {renderErrors(error, 'destination')}
                            </div>

                            <div className="form-control w-full">
                                <label className="label">
                                    <span className="label-text">Active</span>
                                </label>
                                <input type="checkbox" className="toggle toggle-lg" checked={active} onChange={(e) => setActive(e.target.checked)} />
                                {renderErrors(error, 'active')}
                            </div>

                        </div>
                        <div className="bg-base-200 px-4 py-3 text-right sm:px-6">
                            <Link to="/sources" className="btn btn-ghost mr-2">Cancel</Link>
                            <button disabled={executing} type="submit" className="btn btn-primary">Submit</button>
                        </div>
                    </div>
                </form>
            </div>
            <div className="md:col-span-1">
                <div className="px-4 sm:px-0">
                    <h3 className="text-base font-semibold leading-6">Create a new Source</h3>
                    <p className="text-base-content mt-1 text-sm">
                        This information will be displayed publicly so be careful what you share.
                    </p>
                </div>
            </div>
        </div>
        {showSourceSelector && <ItemSelectorModal url="/api/source" title="Select a Source" cols={[{ "label": "Name", "datacss": "text-left", "css": "w-1/2", "name": "name" }, { "label": "Active", "datacss": "", "css": "text-center", "name": "active" }, { "label": "Secured", "datacss": "", "css": "text-center", "name": "secured" }]}
            onCancel={() => setShowSourceSelector(false)} onSelect={onSourceSelection} />}
        {showDestinationSelector && <ItemSelectorModal url="/api/destination" title="Select a Destination" cols={[{ "label": "Name", "datacss": "text-left", "css": "w-1/2", "name": "name" }, { "label": "Active", "datacss": "", "css": "text-center", "name": "active" }]}
            onCancel={() => setShowDestinationSelector(false)} onSelect={onDestinationSelection} />}
    </Fragment>
}

export default NewPipeline;