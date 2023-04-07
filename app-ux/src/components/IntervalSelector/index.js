import moment from "moment-timezone";
import pluralize from "pluralize";

const intervals = [
    {
        label: '1d',
        value: 1440
    },
    {
        label: '12h',
        value: 720
    },
    {
        label: '6h',
        value: 360
    },
    {
        label: '1h',
        value: 60
    },
    {
        label: '5m',
        value: 5
    }
]

export const parseInterval = (val) => {
    const duration = moment.duration(val, 'minutes');
    if (val < 60) {
        return `Last ${duration.asMinutes()} ${pluralize('min', val)}`;
    }
    return `Last ${duration.asHours()} ${pluralize('hour', Math.floor(val / 60))}`;
}

const IntervalSelector = (props) => {
    const { interval, setInterval } = props;

    return <div className="btn-group">
        {intervals.map(item => <button key={item.label} onClick={() => setInterval(item.value)} className={`btn btn-ghost btn-xs ${interval === item.value ? 'text-primary' : ''}`}>{item.label}</button>)}
    </div>
}

export default IntervalSelector;