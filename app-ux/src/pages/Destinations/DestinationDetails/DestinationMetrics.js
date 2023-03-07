import { IconHourglass, IconCircleCheck, IconTimelineEventPlus, IconAlertCircle } from '@tabler/icons-react';

const DestinationMetrics = () => {
    return <div className="grid grid-cols-4 gap-4 mb-4">
        <div className="stat place-items-center shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-primary hidden xl:block">
                <IconTimelineEventPlus size={40} />
            </div>
            <div className="stat-title">Events Queued</div>
            <div className="stat-value text-primary">31.5K</div>
            <div className="stat-desc">last 24 hours</div>
        </div>
        <div className="stat place-items-center shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-success  hidden xl:block">
                <IconCircleCheck size={40} />
            </div>
            <div className="stat-title">Events Dispatched</div>
            <div className="stat-value text-success">31.2K</div>
            <div className="stat-desc">last 24 hours</div>
        </div>
        <div className="stat place-items-center shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-warning  hidden xl:block">
                <IconHourglass size={40} />
            </div>
            <div className="stat-title">Events Pending</div>
            <div className="stat-value text-warning">700</div>
            <div className="stat-desc">Current</div>
        </div>
        <div className="stat place-items-center shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-error hidden xl:block">
                <IconAlertCircle size={40} />
            </div>
            <div className="stat-title">Events Failed</div>
            <div className="stat-value text-error">5</div>
            <div className="stat-desc">last 24 hours</div>
        </div>
    </div>
}

export default DestinationMetrics;