import { IconArrowsMaximize, IconCheck, IconX } from '@tabler/icons-react';
import { Fragment, useState, useRef, useEffect } from "react";
import useNotifications from '../../../hooks/useNotifications';
import { renderErrors } from "../../../common/formHelpers";
import keys from 'lodash/keys';
import fromPairs from 'lodash/fromPairs';
import toPairs from 'lodash/toPairs';
import differenceWith from 'lodash/differenceWith';
import isEqual from 'lodash/isEqual';
import get from 'lodash/get';
import axios from 'axios';
import Editor from '@monaco-editor/react';
import { useNavigate } from 'react-router-dom';
import ConfirmModal from '../../../components/ConfirmModal';
import Modal from '../../../components/Modal';
import ConfigBuilder from '../../../components/ConfigBuilder';
import cloneDeep from 'lodash/cloneDeep';
import filter from 'lodash/filter';

const TransformationDetailsCard = (props) => {
    const { data: { id, name, script, active, config }, onUpdate } = props;
    const navigate = useNavigate();
    const editorRef = useRef(null);
    const { addNotification } = useNotifications();
    const [editMode, setEditMode] = useState(false);
    const [error, setError] = useState({ message: null, details: [] });
    const [changes, setChanges] = useState({});
    const [executing, setExecuting] = useState(false);
    const [configChanges, setConfigChanges] = useState(config ? cloneDeep(config) : []);
    const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);
    const [expandEditor, setExpandEditor] = useState(false);


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
        setChanges(current => ({
            ...current,
            script: value
        }))
    }


    const saveChanges = (e) => {
        e.preventDefault();

        const payload = fromPairs(differenceWith(toPairs(changes), toPairs(props.data), isEqual));
        if (keys(payload).length === 0) {
            setEditMode(false);
            return;
        }
        setExecuting(true);
        axios.put(`/api/transformation/${id}`, payload).then(response => {
            onUpdate(response.data);
            setEditMode(false);
            addNotification({
                message: 'Transformation has been updated',
                type: 'success'
            });
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while updating Transformation'),
                type: 'error'
            });
            setError(err.response.data);
        }).finally(() => {
            setExecuting(false);
        })
    }

    const deleteTransformer = (e) => {
        e.preventDefault();
        setExecuting(true);
        axios.delete(`/api/transformation/${id}`).then(response => {
            addNotification({
                message: 'Transformation has been deleted',
                type: 'success'
            });
            navigate('/transformations');
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while deleting Transformation'),
                type: 'error'
            });
            setError(err.response.data);
        }).finally(() => {
            setExecuting(false);
            setShowDeleteConfirmation(false);
        })
    }

    useEffect(() => {
        setChanges(current => ({
            ...current,
            config: filter(configChanges, item => item.key)
        }));
    }, [configChanges]);

    const cancelChanges = (e) => {
        e.preventDefault();
        setChanges({});
        setEditMode(false);
    }

    return <div className="card bg-base-100 mb-4">
        <div className="card-body p-4">
            <div className="text-lg font-bold">Transformation Details</div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Transformation Name</span>
                </label>
                {editMode ? <Fragment><input type="text" placeholder="Provide a name for the Transformation" defaultValue={name}
                    className="input input-bordered w-full"
                    onChange={e => setChanges(current => ({
                        ...current,
                        name: e.target.value
                    }))} />
                    {renderErrors(error, 'name')}</Fragment> : <div className="p-1 text-lg">{name}</div>}
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Configuration</span>
                </label>
                <ConfigBuilder config={configChanges} setConfig={setConfigChanges} readOnly={!editMode} />
                {renderErrors(error, 'config')}
            </div>
            <div className="form-control w-full">
                <div className="flex justify-between items-center">
                    <label className="label">
                        <span className="label-text">Script</span>
                    </label>
                    <div>
                        <button onClick={() => setExpandEditor(current => !current)} className="btn btn-sm btn-ghost"><IconArrowsMaximize size={16} /></button>
                    </div>
                </div>
                <Editor
                    theme="vs-dark"
                    height="300px"
                    options={{ 'fontSize': 15, quickSuggestions: false, scrollBeyondLastLine: false, readOnly: !editMode, minimap: { enabled: false } }}
                    defaultLanguage="javascript"
                    value={changes.script || script}
                    onChange={handleEditorChange}
                    beforeMount={handleEditorWillMount}
                    onMount={handleEditorDidMount}
                />
                {renderErrors(error, 'script')}
            </div>
            <div className="flex">
                <div className="form-control w-6/12">
                    <label className="label">
                        <span className="label-text">Active</span>
                    </label>
                    {editMode ? <Fragment><input type="checkbox" className={`toggle toggle-lg ${(changes.active || active) ? 'toggle-success' : ''}`} defaultChecked={active}
                        onChange={e => setChanges(current => ({
                            ...current,
                            active: e.target.checked
                        }))} />
                        {renderErrors(error, 'active')}</Fragment> :
                        <div className="p-1">{active ? <IconCheck className="text-success" size={24} /> : <IconX className="text-error" size={24} />}</div>}
                </div>
            </div>

            <div className="card-actions mt-2 justify-between">
                <div>{editMode && <button className="btn btn-error" type="button" disabled={executing} onClick={() => setShowDeleteConfirmation(true)}>Delete</button>}</div>
                <div className="flex justify-end">
                    {!editMode && <a className="btn btn-info mx-2" target="_blank" rel="noreferrer" href={`/api/transformation/${id}/export`}>Export as App</a>}
                    {!editMode && <button className="btn" type="button" onClick={() => setEditMode(true)}>Edit Transformation</button>}
                    {editMode && <button className="btn mr-2" type="button" onClick={cancelChanges}>Cancel</button>}
                    {editMode && <button className="btn btn-primary" type="button" disabled={executing} onClick={saveChanges}>Save
                        Changes</button>}
                </div>

            </div>
            {showDeleteConfirmation && <ConfirmModal title="Delete Transformation" message="Are you sure you want to delete this Transformation?" onConfirm={deleteTransformer} onCancel={() => setShowDeleteConfirmation(false)} />}

            {expandEditor && <Modal large={true} title={editMode ? 'Script' : 'Script (Read-only)'} onCancel={() => setExpandEditor(false)}>
                <>
                    <div className="px-6 pb-4">
                        <Editor
                            theme="vs-dark"
                            height="75vh"
                            options={{ 'fontSize': 15, quickSuggestions: false, scrollBeyondLastLine: false, readOnly: !editMode, minimap: { enabled: false } }}
                            defaultLanguage="javascript"
                            value={changes.script || script}
                            onChange={handleEditorChange}
                            beforeMount={handleEditorWillMount}
                            onMount={handleEditorDidMount}
                        />
                    </div>
                    <div className="bg-base-200 px-4 py-3 justify-between sm:px-6 flex">
                        <button disabled={editMode} className="btn btn-primary mr-2" onClick={() => setEditMode(true)}>Edit</button>
                        <button className="btn" onClick={() => setExpandEditor(false)}>Collapse</button>
                    </div>
                </>
            </Modal>}
        </div>
    </div>
}

export default TransformationDetailsCard;