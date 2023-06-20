import { useRef, useState } from "react";

import axios from "axios";
import get from 'lodash/get';
import filter from 'lodash/filter';
import Editor from '@monaco-editor/react'
import ConfigBuilder from "../../../../../components/ConfigBuilder";
import { Link } from "react-router-dom";
import { renderErrors } from "../../../../../common/formHelpers";
import ContextHelp from "../../../../../components/ContextHelp";
import { IconArrowsMaximize } from "@tabler/icons-react";

const scriptTemplate = `module.exports = function(config) { /* required */
    //add your script here to pull data from your data source.
}`;

const Extractor = () => {

    const editorRef = useRef(null);
    const [name, setName] = useState('');
    const [config, setConfig] = useState([{ key: "", value: "", secret: false }]);
    const [script, setScript] = useState(scriptTemplate);
    const [active, setActive] = useState(true);
    const [executing, setExecuting] = useState(false);
    const [error, setError] = useState({ message: null, details: [] });
    const [expandEditor, setExpandEditor] = useState(false);

    const resetAll = () => {
        setName('');
        setConfig([{ key: "", value: "", secret: false }]);
        setScript(scriptTemplate);
        setActive(true);
        clearErrors();
    }

    const clearErrors = () => {
        setError({ message: null, details: [] });
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

        console.log(payload);
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

    return <>
        <div className="md:grid md:grid-cols-3 md:gap-6">
            <div className="mt-5 md:col-span-2 md:mt-0">
                <form onSubmit={submit}>
                    <div className="shadow-inner sm:overflow-hidden sm:rounded-md">
                        <div className="space-y-3 px-4 py-5 sm:p-6">

                            <div className="form-control w-full">
                                <label className="label">
                                    <span className="label-text">Extractor Name</span>
                                </label>
                                <input type="text" placeholder="Provide a name for the Extractor" value={name} className="input input-bordered w-full" onChange={e => setName(e.target.value)} />
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
                            <Link to="/pipelines" className="btn btn-ghost mr-2">Cancel</Link>
                            <button disabled={executing} type="submit" className="btn btn-primary">Submit</button>
                        </div>
                    </div>
                </form>
            </div>
            <div className="md:col-span-1">
                <ContextHelp page="create-pipeline" />
            </div>
        </div>
    </>
}

export default Extractor;