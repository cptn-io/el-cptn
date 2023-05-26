import { IconCircleCheck, IconTimelineEventExclamation, IconTimelineEventPlus } from "@tabler/icons-react";
import pluralize from "pluralize";
import { parseInterval } from "../IntervalSelector";
import { Refreshing } from "../Loading";
import { Link } from "react-router-dom";

export const SourceMetricsRenderer = (props) => {
    const { metrics = {}, interval, refreshing } = props;
    return <div className="grid grid-cols-4 gap-2">
        <div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-primary hidden xl:block">
                <IconTimelineEventPlus size={64} />
            </div>
            <div className="stat-title ">Events Received</div>
            <div className="stat-value text-base-content">{refreshing ? <Refreshing /> : metrics.inboundTotal || 0}</div>
            <div className="stat-desc">{parseInterval(interval)}</div>
        </div>
        <div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-success hidden xl:block">
                <IconCircleCheck size={64} />
            </div>
            <div className="stat-title">Events Processed</div>
            <div className="stat-value text-base-content">{refreshing ? <Refreshing /> : metrics.inboundProcessed || 0}</div>
            <div className="stat-desc">{parseInterval(interval)}</div>
        </div>
        <div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-primary hidden xl:block">
                {!refreshing && <div className="radial-progress text-sky-600" style={{ "--value": (metrics.inboundPercentComplete || 0), "--size": "4.2rem" }}>{metrics.inboundPercentComplete || 0}%</div>}
            </div>
            <div className="stat-title ">% Processed</div>
            <div className="stat-value text-base-content">{refreshing ? <Refreshing /> : `${metrics.inboundPercentComplete || 0}%`}</div>
            <div className="stat-desc">{!refreshing ? `${pluralize('event', ((metrics.inboundTotal || 0) - (metrics.inboundProcessed || 0)), true)} remaining` : ''}</div>
        </div>
        <Link to="/sources?page=0&status=FAILED"><div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-error hidden xl:block">
                <IconTimelineEventExclamation size={64} />
            </div>
            <div className="stat-title">Events Failed</div>
            <div className="stat-value text-base-content">{refreshing ? <Refreshing /> : metrics.inboundFailed || 0}</div>
            <div className="stat-desc">{parseInterval(interval)}</div>
        </div></Link>
    </div>
}


export const PipelineMetricsRenderer = (props) => {
    const { metrics = {}, interval, refreshing } = props;
    return <div className="grid grid-cols-4 gap-2">
        <div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-primary hidden xl:block">
                <IconTimelineEventPlus size={64} />
            </div>
            <div className="stat-title ">Events Queued</div>
            <div className="stat-value text-base-content">{refreshing ? <Refreshing /> : metrics.outboundTotal || 0}</div>
            <div className="stat-desc">{parseInterval(interval)}</div>
        </div>
        <div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-success hidden xl:block">
                <IconCircleCheck size={64} />
            </div>
            <div className="stat-title">Events Processed</div>
            <div className="stat-value text-base-content">{refreshing ? <Refreshing /> : metrics.outboundProcessed || 0}</div>
            <div className="stat-desc">{parseInterval(interval)}</div>
        </div>
        <div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-primary hidden xl:block">
                {!refreshing && <div className="radial-progress text-sky-600" style={{ "--value": (metrics.outboundPercentComplete || 0), "--size": "4.2rem" }}>{metrics.outboundPercentComplete || 0}%</div>}
            </div>
            <div className="stat-title ">% Processed</div>
            <div className="stat-value text-base-content">{refreshing ? <Refreshing /> : `${metrics.outboundPercentComplete || 0}%`}</div>
            <div className="stat-desc">{!refreshing ? `${pluralize('event', ((metrics.outboundTotal || 0) - (metrics.outboundProcessed || 0)), true)} remaining` : ''}</div>
        </div>
        <Link to="/pipelines?page=0&status=FAILED"><div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-error hidden xl:block">
                <IconTimelineEventExclamation size={64} />
            </div>
            <div className="stat-title">Events Failed</div>
            <div className="stat-value text-base-content">{refreshing ? <Refreshing /> : metrics.outboundFailed || 0}</div>
            <div className="stat-desc">{parseInterval(interval)}</div>
        </div></Link>
    </div >
}