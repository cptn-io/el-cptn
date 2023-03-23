import find from 'lodash/find';

export const processInboundMetrics = (data) => {
    const inboundTotal = data.inbound.reduce((sum, status) => sum + status.count, 0);
    const inboundProcessed = find(data.inbound, { 'state': 'COMPLETED' })?.count || 0;
    const inboundPercentComplete = inboundTotal > 0 ? Math.floor(inboundProcessed / inboundTotal * 100) : 100;
    const inboundFailed = find(data.inbound, { 'state': 'FAILED' })?.count || 0;

    return {
        inboundTotal,
        inboundProcessed,
        inboundPercentComplete,
        inboundFailed
    }
}