import { Editor } from "@monaco-editor/react";
import ConfigBuilder from "../../../components/ConfigBuilder";

const AppDetails = (props) => {
    const { app, onCancel, onUseApp } = props;

    const handleEditorWillMount = (monaco) => {
        monaco.languages.typescript.javascriptDefaults.setCompilerOptions({
            target: monaco.languages.typescript.ScriptTarget.ES6,
            allowNonTsExtensions: true,
            allowJs: true
        });

        monaco.languages.typescript.javascriptDefaults.setEagerModelSync(true);
    }

    return <div>
        <div className="space-y-3 px-4 py-5 sm:p-6">
            <div className="w-full">
                <label className="label"><span className="label-text">App Type</span></label>
                <div className="mx-1 text-md">{app.type === 'DESTINATION' ? 'Destination' : 'Transformation'}</div>
            </div>
            {app.type === 'DESTINATION' && <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Configuration</span>
                </label>
                <ConfigBuilder config={app.config} setConfig={() => { }} readOnly={true} />
            </div>}
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Script</span>
                </label>
                <Editor
                    theme="vs-dark"
                    height="300px"
                    options={{ 'fontSize': 15, quickSuggestions: false, scrollBeyondLastLine: false, readOnly: true, minimap: { enabled: false } }}
                    defaultLanguage="javascript"
                    value={app.script}
                    beforeMount={handleEditorWillMount}
                />
            </div>
        </div>
        <div className="bg-base-200 px-4 py-3 justify-between sm:px-6 flex">
            <button className="btn" onClick={onCancel}>Close</button>
            <button className="btn btn-primary" onClick={onUseApp}>{app.type === 'DESTINATION' ? `New Destination` : `New Transformation`}</button>
        </div>
    </div>
}

export default AppDetails;