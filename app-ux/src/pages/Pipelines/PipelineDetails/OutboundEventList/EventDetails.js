import Editor from "@monaco-editor/react";
import { resolveState } from ".";

const EventDetails = ({ event, onCancel, onSendEvent }) => {

    return <div>
        <div className="space-y-3 px-4 py-5 sm:p-6">
            <div className="w-full">
                <label className="label"><span className="label-text">Event ID</span></label>
                <div className="mx-1 text-md">{event.id}</div>
            </div>
            <div className="w-full">
                <label className="label"><span className="label-text">Status</span></label>
                <div className="flex mx-1 text-md">{resolveState(event.state)}</div>
            </div>
            <div className="w-full">
                <label className="label">
                    <span className="label-text">Payload</span>
                </label>
                <div className="mx-1"><Editor
                    theme="vs-dark"
                    height="220px"
                    options={{ 'fontSize': 15, quickSuggestions: false, scrollBeyondLastLine: false, readOnly: true, minimap: { enabled: false } }}
                    defaultLanguage="json"
                    value={JSON.stringify(event.payload, null, 2)}
                /></div>
            </div>
            <div className="w-full">
                <label className="label">
                    <span className="label-text">Logs</span>
                </label>
                <div className="mx-1"><Editor
                    theme="vs-dark"
                    height="150px"
                    options={{ 'fontSize': 15, quickSuggestions: false, scrollBeyondLastLine: false, readOnly: true, minimap: { enabled: false } }}
                    defaultLanguage="text"
                    value={event.consoleLog}
                /></div>
            </div>
            {event.state === 'FAILED' && event.exception && <div className="w-full">
                <label className="label">
                    <span className="label-text">Error message</span>
                </label>
                <div className="mx-1 text-error">
                    {event.exception}
                </div>
            </div>}
        </div>
        <div className="bg-base-200 px-4 py-3 justify-between sm:px-6 flex">
            <button className="btn" onClick={() => onSendEvent(event.id)}>Resend Event</button>
            <button className="btn btn-primary" onClick={onCancel}>Close</button>
        </div>
    </div>
}

export default EventDetails;