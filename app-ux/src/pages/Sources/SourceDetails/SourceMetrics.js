import { IconHourglass, IconCircleCheck, IconTimelineEventPlus, IconDatabaseExport } from '@tabler/icons-react';

const SourceMetrics = () => {
    return <div className="grid grid-cols-4 gap-4 mb-4">
        <div className="stat place-items-center shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-primary hidden xl:block">
                <IconTimelineEventPlus size={40} />
            </div>
            <div className="stat-title">Events Ingested</div>
            <div className="stat-value text-primary">31.5K</div>
            <div className="stat-desc">last 24 hours</div>
        </div>
        <div className="stat place-items-center shadow col-span-2 md:col-span-1 rounded-2xl">
            <div className="stat-figure text-success  hidden xl:block">
                <IconCircleCheck size={40} />
            </div>
            <div className="stat-title">Events Processed</div>
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
            <div className="stat-figure  hidden xl:block">
                <IconDatabaseExport size={40} />
            </div>
            <div className="stat-title">Destinations</div>
            <div className="stat-value">5</div>
            <div className="stat-desc">Current</div>
        </div>
    </div>
}

export default SourceMetrics;