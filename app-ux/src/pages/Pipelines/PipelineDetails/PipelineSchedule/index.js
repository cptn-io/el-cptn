import './styles.scss';
import Scheduler from "./Scheduler";

const PipelineSchedule = (props) => {
    const { pipeline } = props;



    return <div className="grid grid-flow-row-dense grid-cols-1 xl:grid-cols-8 gap-4 ">
        <div className="xl:col-span-6">
            <div className="card bg-base-100 mb-4">
                <div className="card-body p-4">
                    <div className="text-lg font-bold bg-base-200 p-2 rounded-md">Schedule</div>
                    {!pipeline.batchProcess && <div className="m-2">This Pipeline does not require a schedule as it doesn't process events in Scheduled Batches</div>}
                    {pipeline.batchProcess && <Scheduler pipelineId={pipeline.id} />}
                </div>

            </div>
        </div>
        <div className="hidden xl:col-span-2 xl:block m-2">
            <div className="card bg-base-100 shadow">
                <div className="card-body">
                    <h2 className="card-title text-error">Important!</h2>
                    <div>
                        <p className="mt-4">Quaued events related to a Pipeline are stored in the database. Events are automatically deleted on schedule based on the table rotation settings configured for Outbound queues.</p>
                        <p className="mt-4">Ensure your Pipeline Schedule is configured to align with the table rotation settings. Otherwise, you may have events deleted before they are processed.</p>
                        <p className="mt-4">We recommend a run schedule interval of at least 5 minutes. If you do not have an associated schedule or if the schedule is inactive, events can only be processed on-demand.</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
}

export default PipelineSchedule;