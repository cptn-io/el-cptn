import { IconRefresh, IconRotateClockwise } from '@tabler/icons-react';
import './index.scss';

const Loading = () => {
    return <div className="flex my-4 justify-center"><IconRotateClockwise className="icon-spinner" size={48} /></div>
}
export const Refreshing = () => {
    return <IconRefresh className="icon-spinner text-base-300" size={36} />
}
export default Loading;