import { useState, useRef } from "react";

import axios from "axios";
import useNotifications from "../../../hooks/useNotifications";
import get from 'lodash/get';
import { renderErrors } from "../../../common/formHelpers";
import Editor from '@monaco-editor/react'
import ConfigBuilder from "../../../components/ConfigBuilder";
import filter from 'lodash/filter';


const scriptTemplate = `module.exports = {
    setup: function(ctx, config) { /* optional */
        //setup connection
    },
    execute: function(event, ctx, config) { /* required */
        //send to destination.
    },
    teardown: function(ctx, config) { /* optional */
        //teardown connection
    }
}`;

const CreateDestination = (props) => {
    const { onSuccess, onCancel, noShadow = false } = props;
    const editorRef = useRef(null);
    const { addNotification } = useNotifications();
    const [name, setName] = useState('');
    const [config, setConfig] = useState([{ key: "", value: "", secret: false }]);
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
        axios.post('/api/destination', payload).then(response => {
            resetAll();
            addNotification({
                message: 'Destination has been created',
                type: 'success'
            });
            onSuccess(response.data);
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while creating Destination'),
                type: 'error'
            });
            setError(err.response.data);
        }).finally(() => {
            setExecuting(false);
        })
    };

    return <form onSubmit={submit}>
        <div className={`sm:overflow-hidden sm:rounded-md ${noShadow ? '' : 'shadow-inner'}`}>
            <div className="space-y-3 px-4 py-5 sm:p-6">

                <div className="form-control w-full">
                    <label className="label">
                        <span className="label-text">Destination Name</span>
                    </label>
                    <input type="text" placeholder="Provide a name for the Destination" value={name} className="input input-bordered w-full" onChange={e => setName(e.target.value)} />
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
                    <input type="checkbox" className={`toggle toggle-lg ${active ? 'toggle-success' : ''}`} checked={active} onChange={(e) => setActive(e.target.checked)} />
                    {renderErrors(error, 'active')}
                </div>

            </div>
            <div className="bg-base-200 px-4 py-3 text-right sm:px-6">
                <button onClick={onCancel} className="btn btn-ghost mr-2">Cancel</button>
                <button disabled={executing} type="submit" className="btn btn-primary">Submit</button>
            </div>
        </div>
    </form>
}

export default CreateDestination;