import { IconCircleCheck, IconTimelineEventExclamation, IconTimelineEventPlus } from "@tabler/icons-react";
import pluralize from "pluralize";
import { parseInterval } from "../IntervalSelector";
import { Refreshing } from "../Loading";

export const SourceMetricsRenderer = (props) => {
    const { metrics, interval, refreshing } = props;
    return <div className="grid grid-cols-4 gap-2">
        <div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-primary hidden xl:block">
                <IconTimelineEventPlus size={64} />
            </div>
            <div className="stat-title ">Events Received</div>
            <div className="stat-value text-base-content">{refreshing ? <Refreshing /> : metrics.inboundTotal}</div>
            <div className="stat-desc">{parseInterval(interval)}</div>
        </div>
        <div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-success hidden xl:block">
                <IconCircleCheck size={64} />
            </div>
            <div className="stat-title">Events Processed</div>
            <div className="stat-value text-base-content">{refreshing ? <Refreshing /> : metrics.inboundProcessed}</div>
            <div className="stat-desc">{parseInterval(interval)}</div>
        </div>
        <div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-primary hidden xl:block">
                {!refreshing && <div className="radial-progress text-primary" style={{ "--value": metrics.inboundPercentComplete, "--size": "4.2rem" }}>{metrics.inboundPercentComplete}%</div>}
            </div>
            <div className="stat-title ">% Processed</div>
            <div className="stat-value text-base-content">{refreshing ? <Refreshing /> : `${metrics.inboundPercentComplete}%`}</div>
            <div className="stat-desc">{!refreshing ? `${pluralize('event', metrics.inboundTotal - metrics.inboundProcessed, true)} remaining` : ''}</div>
        </div>
        <div className="stat bg-base-100 shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-error hidden xl:block">
                <IconTimelineEventExclamation size={64} />
            </div>
            <div className="stat-title">Events Failed</div>
            <div className="stat-value text-base-content">{refreshing ? <Refreshing /> : metrics.inboundFailed}</div>
            <div className="stat-desc">{parseInterval(interval)}</div>
        </div>
    </div>
}