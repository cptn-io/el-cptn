import { useEffect, useRef, useState } from "react";

import axios from "axios";
import get from 'lodash/get';
import filter from 'lodash/filter';
import Editor from '@monaco-editor/react'
import ConfigBuilder from "../../../../../components/ConfigBuilder";
import { renderErrors } from "../../../../../common/formHelpers";
import { IconArrowsMaximize, IconCheck, IconX } from "@tabler/icons-react";
import Loading from "../../../../../components/Loading";
import useNotifications from "../../../../../hooks/useNotifications";
import Modal from "../../../../../components/Modal";
import cloneDeep from 'lodash/cloneDeep';
import ConfirmModal from "../../../../../components/ConfirmModal";
import ExtractorSchedule from "./ExtractorSchedule";


const scriptTemplate = `module.exports = function(config) { /* required */
    //add your script here to pull data from your data source.
}`;

const Extractor = ({ sourceId }) => {

    const { addNotification } = useNotifications();
    const editorRef = useRef(null);
    const [original, setOriginal] = useState({});
    const [id, setId] = useState(null);
    const [name, setName] = useState('');
    const [editMode, setEditMode] = useState(false);
    const [config, setConfig] = useState([{ key: "", value: "", secret: false }]);
    const [script, setScript] = useState(scriptTemplate);
    const [active, setActive] = useState(true);
    const [executing, setExecuting] = useState(false);
    const [error, setError] = useState({ message: null, details: [] });
    const [expandEditor, setExpandEditor] = useState(false);
    const [loading, setLoading] = useState(true);
    const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);

    const clearErrors = () => {
        setError({ message: null, details: [] });
    }

    const parseResponsePayload = (data) => {
        setId(data.id);
        setName(data.name);
        setActive(data.active);
        setScript(data.script);
        setConfig(cloneDeep(data.config));
    };

    const resetFormOnDelete = () => {
        setName("");
        setConfig([{ key: "", value: "", secret: false }]);
        setScript(scriptTemplate);
        setActive(true);
        setId(null);
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

        if (id) {
            axios.put(`/api/extractor/${id}`, payload).then(response => {
                setOriginal(response.data);
                addNotification({
                    message: 'Your changes have been saved',
                    type: 'success'
                });
            }).catch(err => {
                addNotification({
                    message: get(err, 'response.data.message', 'An error occurred while saving the Extractor'),
                    type: 'error'
                });
                setError(err.response.data);
            }).finally(() => {
                setExecuting(false);
                setEditMode(false);
            });
        } else {
            payload.source = {
                id: sourceId
            };
            //create a new extractor
            axios.post(`/api/extractor`, payload).then(response => {
                parseResponsePayload(response.data);
                addNotification({
                    message: 'Your changes have been saved',
                    type: 'success'
                });
            }).catch(err => {
                addNotification({
                    message: get(err, 'response.data.message', 'An error occurred while saving the Extractor'),
                    type: 'error'
                });
                setError(err.response.data);
            }).finally(() => {
                setExecuting(false);
                setEditMode(false);
            });
        }
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

    const cancelChanges = (e) => {
        e.preventDefault();
        parseResponsePayload(original);
        setEditMode(false);
    }

    const deleteExtractor = (e) => {
        e.preventDefault();
        setExecuting(true);
        axios.delete(`/api/extractor/${id}`).then(response => {
            resetFormOnDelete();
            addNotification({
                message: 'Extractor has been deleted',
                type: 'success'
            });
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while deleting Extractor'),
                type: 'error'
            });
            setError(err.response.data);
        }).finally(() => {
            setExecuting(false);
            setShowDeleteConfirmation(false);
        })
    }


    useEffect(() => {
        setExecuting(true);
        axios.get(`/api/extractor/source/${sourceId}`).then(response => {
            setOriginal(response.data);
            parseResponsePayload(response.data);
        }).catch(err => {
            if (err.response.status !== 404) {
                addNotification({
                    title: 'Error',
                    message: 'An error occurred when fetching the Data Extractor for this Source',
                    type: 'error'
                });
            } else {
                setEditMode(true);
            }
        }).finally(() => {
            setExecuting(false);
            setLoading(false);
        });

    }, [addNotification, sourceId])
    if (loading) {
        return <Loading />
    }

    return <><div className="card bg-base-100 mb-4">
        <div className="card-body p-4">
            <div className="text-lg font-bold bg-base-200 p-2 rounded-md">Data Extractor Details</div>
            <form onSubmit={submit}>
                <div>
                    <div className="space-y-3 pb-5">

                        <div className="form-control w-full">
                            <label className="label">
                                <span className="label-text">Extractor Name</span>
                            </label>
                            {editMode ? <>
                                <input type="text" placeholder="Provide a name for the Extractor" value={name} className="input input-bordered w-full" onChange={e => setName(e.target.value)} />
                                {renderErrors(error, 'name')}
                            </> : <div className="text-lg font-bold">{name}</div>}

                        </div>
                        <div className="form-control w-full">
                            <label className="label">
                                <span className="label-text">Configuration</span>
                            </label>
                            <ConfigBuilder config={config} setConfig={setConfig} readOnly={!editMode} />
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
                                options={{ 'fontSize': 15, quickSuggestions: false, scrollBeyondLastLine: false, readOnly: !editMode, minimap: { enabled: false } }}
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
                            {editMode ? <><input type="checkbox" className={`toggle toggle-lg ${active ? 'toggle-success' : ''}`} checked={active} onChange={(e) => setActive(e.target.checked)} />
                                {renderErrors(error, 'active')}
                            </> : <div className="p-1">{active ? <IconCheck className="text-success" size={24} /> : <IconX className="text-error" size={24} />}</div>}
                        </div>

                    </div>
                    <div className="mt-2 flex justify-between">
                        <div>{editMode && id && <button className="btn btn-error" type="button" disabled={executing} onClick={() => setShowDeleteConfirmation(true)}>Delete</button>}</div>
                        <div className="flex justify-end">
                            {!editMode && <button className="btn" type="button" onClick={() => setEditMode(true)}>Edit Extractor</button>}
                            {editMode && id && <button className="btn mr-2" type="button" onClick={cancelChanges}>Cancel</button>}
                            {editMode && <button disabled={executing} type="submit" className="btn btn-primary">Save Changes</button>}
                        </div>
                    </div>
                </div>
            </form>
            {showDeleteConfirmation && <ConfirmModal title="Delete Extractor" message="Are you sure you want to delete this Extractor?" onConfirm={deleteExtractor} onCancel={() => setShowDeleteConfirmation(false)} />}
            {expandEditor && <Modal large={true} title="Script" onCancel={() => setExpandEditor(false)}>
                <>
                    <div className="px-6 pb-4">
                        <Editor
                            theme="vs-dark"
                            height="75vh"
                            options={{ 'fontSize': 15, quickSuggestions: false, scrollBeyondLastLine: false, readOnly: !editMode, minimap: { enabled: false } }}
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
        </div></div>
        {id && <ExtractorSchedule extractorId={id} />}
    </>

}

export default Extractor;