import { IconCheck, IconX } from '@tabler/icons-react';
import { Fragment, useState, useRef } from "react";
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

const DestinationDetailsCard = (props) => {
    const { data: { id, name, script, active }, onUpdate } = props;

    const editorRef = useRef(null);
    const { addNotification } = useNotifications();
    const [editMode, setEditMode] = useState(false);
    const [error, setError] = useState({ message: null, details: [] });
    const [changes, setChanges] = useState({});
    const [executing, setExecuting] = useState(false);


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
        axios.put(`/api/destination/${id}`, payload).then(response => {
            onUpdate(response.data);
            setEditMode(false);
            addNotification({
                message: 'Destination has been updated',
                type: 'success'
            });
        }).catch(err => {
            addNotification({
                message: get(err, 'response.data.message', 'An error occurred while updating Destination'),
                type: 'error'
            });
            setError(err.response.data);
        }).finally(() => {
            setExecuting(false);
        })
    }

    const cancelChanges = (e) => {
        e.preventDefault();
        setChanges({});
        setEditMode(false);
    }

    return <div className="card bg-base-100 mb-4">
        <div className="card-body">
            <h2 className="card-title">Destination Details</h2>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Destination Name</span>
                </label>
                {editMode ? <Fragment><input type="text" placeholder="Provide a name for the Destination" defaultValue={name}
                    className="input input-bordered w-full"
                    onChange={e => setChanges(current => ({
                        ...current,
                        name: e.target.value
                    }))} />
                    {renderErrors(error, 'name')}</Fragment> : <div className="p-1 text-lg">{name}</div>}
            </div>
            <div className="form-control w-full">
                <label className="label">
                    <span className="label-text">Script</span>
                </label>
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
                    {editMode ? <Fragment><input type="checkbox" className="toggle toggle-lg" defaultChecked={active}
                        onChange={e => setChanges(current => ({
                            ...current,
                            active: e.target.checked
                        }))} />
                        {renderErrors(error, 'active')}</Fragment> :
                        <div className="p-1">{active ? <IconCheck size={24} /> : <IconX size={24} />}</div>}
                </div>
            </div>

            <div className="card-actions justify-end">
                {!editMode && <button className="btn" onClick={() => setEditMode(true)}>Edit Destination</button>}
                {editMode && <button className="btn" onClick={cancelChanges}>Cancel</button>}
                {editMode && <button className="btn btn-primary" disabled={executing} onClick={saveChanges}>Save
                    changes</button>}
            </div>
        </div>
    </div>
}

export default DestinationDetailsCard;