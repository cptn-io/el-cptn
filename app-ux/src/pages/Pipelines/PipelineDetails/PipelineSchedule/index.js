import './styles.scss';
import Scheduler from "./Scheduler";

const PipelineSchedule = (props) => {
    const { pipeline } = props;



    return <div className="grid grid-flow-row-dense grid-cols-1 xl:grid-cols-8 gap-4 ">
        <div className="xl:col-span-6">
            <div className="card bg-base-100 mb-4">
                <div className="card-body p-4">
                    <div className="text-lg font-bold bg-base-200 p-2 rounded-md">Schedule</div>
                    {!pipeline.batchProcess && <div className="m-2">This pipeline runs without a schedule since it does not process events in scheduled batches.</div>}
                    {pipeline.batchProcess && <Scheduler pipelineId={pipeline.id} />}
                </div>

            </div>
        </div>
        <div className="hidden xl:col-span-2 xl:block m-2">
            <div className="card bg-base-100 shadow">
                <div className="card-body">
                    <h2 className="card-title text-error">Important!</h2>
                    <div>
                        <p className="mt-4">Events related to a pipeline are queued and stored in the database, with automatic deletion based on the table rotation settings configured for outbound queues.</p>
                        <p className="mt-4">Make sure to align your pipeline schedule with the table rotation settings to prevent events from being deleted before processing.</p>
                        <p className="mt-4">To ensure optimal performance, we suggest setting the run schedule interval to a minimum of 5 minutes. Without an active associated schedule, events will only be processed on-demand.</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
}

export default PipelineSchedule;