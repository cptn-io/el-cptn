import { Fragment, useState, useRef } from "react";
import PageTitle from "../../../components/Nav/PageTitle";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";
import useNotifications from "../../../hooks/useNotifications";
import get from 'lodash/get';
import { renderErrors } from "../../../common/formHelpers";
import Editor from '@monaco-editor/react'
import { breadcrumbs } from "..";
import ContextHelp from "../../../components/ContextHelp";
import Modal from "../../../components/Modal";
import { IconArrowsMaximize } from "@tabler/icons-react";
import { filter } from "lodash";
import ConfigBuilder from "../../../components/ConfigBuilder";

const scriptTemplate = `module.exports = function (event, ctx, config) {
    //add your script here to transform or enrich the event

    //remember to return the transformed event object for the pipeline to continue processing the event
    return event;
}`;

const NewTransformation = () => {
    const navigate = useNavigate();
    const editorRef = useRef(null);
    const { addNotification } = useNotifications();
    const [name, setName] = useState('');
    const [script, setScript] = useState(scriptTemplate);
    const [active, setActive] = useState(true);
    const [config, setConfig] = useState([{ key: "", value: "", secret: false }]);
    const [executing, setExecuting] = useState(false);
    const [error, setError] = useState({ message: null, details: [] });
    const [expandEditor, setExpandEditor] = useState(false);


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
            allowJs: true
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
            active,
            config: filter(config, item => item.key)
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
                                    <span className="label-text">Configuration</span>
                                </label>
                                <ConfigBuilder config={config} setConfig={setConfig} />
                                {renderErrors(error, 'config')}
                            </div>

                            <div className="form-control w-full">
                                <div className="flex justify-between items-center">
                                    <label className="label">
                                        <span className="label-text">Script</span>
                                    </label>
                                    <div>
                                        <button onClick={(e) => { e.preventDefault(); setExpandEditor(current => !current) }} className="btn btn-sm btn-ghost"><IconArrowsMaximize size={16} /></button>
                                    </div>
                                </div>
                                <Editor
                                    theme="vs-dark"
                                    height="300px"
                                    options={{ 'fontSize': 15, quickSuggestions: false, scrollBeyondLastLine: false, minimap: { enabled: false } }}
                                    defaultLanguage="javascript"
                                    value={script}
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
                                <input type="checkbox" className={`toggle toggle-lg ${active ? 'toggle-success' : ''}`} checked={active} onChange={(e) => setActive(e.target.checked)} />
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
                <ContextHelp page="create-transformation" />
            </div>
        </div>
        {expandEditor && <Modal large={true} title="Script" onCancel={() => setExpandEditor(false)}>
            <>
                <div className="px-6 pb-4">
                    <Editor
                        theme="vs-dark"
                        height="75vh"
                        options={{ 'fontSize': 15, quickSuggestions: false, scrollBeyondLastLine: false, minimap: { enabled: false } }}
                        defaultLanguage="javascript"
                        value={script}
                        onChange={handleEditorChange}
                        beforeMount={handleEditorWillMount}
                        onMount={handleEditorDidMount}
                    />
                </div>
                <div className="bg-base-200 px-4 py-3 justify-end sm:px-6 flex">
                    <button className="btn" onClick={() => setExpandEditor(false)}>Collapse</button>
                </div>
            </>
        </Modal>}
    </Fragment>
}

export default NewTransformation;