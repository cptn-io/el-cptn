import { Fragment, useState, useRef } from "react";
import PageTitle from "../../../components/Nav/PageTitle";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";
import useNotifications from "../../../hooks/useNotifications";
import get from 'lodash/get';
import { renderErrors } from "../../../common/formHelpers";
import Editor from '@monaco-editor/react'
import { breadcrumbs } from "..";

const scriptTemplate = `return (function (evt, ctx){
    //add your script here to send the event to your destination service
    return evt;
})(evt, ctx);`;

const NewTransformation = () => {
    const navigate = useNavigate();
    const editorRef = useRef(null);
    const { addNotification } = useNotifications();
    const [name, setName] = useState('');
    const [script, setScript] = useState(scriptTemplate);
    const [active, setActive] = useState(true);
    const [executing, setExecuting] = useState(false);
    const [error, setError] = useState({ message: null, details: [] });


    const resetAll = () => {
        setName('');
        setScript(scriptTemplate);
        setActive(true);
        clearErrors();
    }

    const clearErrors = () => {
        setError({ message: null, details: [] });
    }

    const handleEditorWillMount = (monaco) => {

        monaco.languages.typescript.javascriptDefaults.setCompilerOptions({
            target: monaco.languages.typescript.ScriptTarget.ES6,
            allowNonTsExtensions: true,
        });

        monaco.languages.typescript.javascriptDefaults.setEagerModelSync(true);
    }


    const handleEditorDidMount = (editor, monaco) => {
        editorRef.current = editor;
    }

    const handleEditorChange = (value) => {
        setScript(() => value);
    }

    const submit = (e) => {
        e.preventDefault();
        clearErrors();
        setExecuting(true);

        const payload = {
            name,
            script,
            active
        };
        axios.post('/api/transformation', payload).then(response => {
            resetAll();
            addNotification({
                message: 'Destination has been created',
                type: 'success'
            });
            navigate('/transformations');
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while creating Transformation'),
                type: 'error'
            });
            setError(err.response.data);
        }).finally(() => {
            setExecuting(false);
        })
    };

    return <Fragment>
        <PageTitle itemKey="transformations" label="New Transformation" breadcrumbs={breadcrumbs} />
        <div className="md:grid md:grid-cols-3 md:gap-6">
            <div className="mt-5 md:col-span-2 md:mt-0">
                <form onSubmit={submit}>
                    <div className="shadow-inner sm:overflow-hidden sm:rounded-md">
                        <div className="space-y-3 px-4 py-5 sm:p-6">

                            <div className="form-control w-full">
                                <label className="label">
                                    <span className="label-text">Transformation Name</span>
                                </label>
                                <input type="text" placeholder="Provide a name for the Transformation" value={name} className="input input-bordered w-full" onChange={e => setName(e.target.value)} />
                                {renderErrors(error, 'name')}
                            </div>

                            <div className="form-control w-full">
                                <label className="label">
                                    <span className="label-text">Script</span>
                                </label>
                                <Editor
                                    theme="vs-dark"
                                    height="300px"
                                    options={{ 'fontSize': 15, quickSuggestions: false, scrollBeyondLastLine: false, minimap: { enabled: false } }}
                                    defaultLanguage="javascript"
                                    defaultValue={scriptTemplate}
                                    onChange={handleEditorChange}
                                    beforeMount={handleEditorWillMount}
                                    onMount={handleEditorDidMount}
                                />
                                {renderErrors(error, 'script')}
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
                            <Link to="/transformations" className="btn btn-ghost mr-2">Cancel</Link>
                            <button disabled={executing} type="submit" className="btn btn-primary">Submit</button>
                        </div>
                    </div>
                </form>
            </div>
            <div className="md:col-span-1">
                <div className="px-4 sm:px-0">
                    <h3 className="text-base font-semibold leading-6">Helpful Information</h3>
                    <p className="text-base-content mt-1">
                        <p>Transformations help with enriching and removing sensitive data from your event payload. Add any Javascript code with required npm JS modules to transform your payloads.</p>
                        <p className="mt-3">Remember that Transformations must return updated event payload for the event data to move across all the Pipeline steps. Pipeline will stop processing an event if a Transformation does not return evt object.</p>
                    </p>
                </div>
            </div>
        </div>
    </Fragment>
}

export default NewTransformation;