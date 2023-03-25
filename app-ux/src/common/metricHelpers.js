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

export const processOutboundMetrics = (data) => {
    const outboundTotal = data.outbound.reduce((sum, status) => sum + status.count, 0);
    const outboundProcessed = find(data.outbound, { 'state': 'COMPLETED' })?.count || 0;
    const outboundPercentComplete = outboundTotal > 0 ? Math.floor(outboundProcessed / outboundTotal * 100) : 100;
    const outboundFailed = find(data.outbound, { 'state': 'FAILED' })?.count || 0;

    return {
        outboundTotal,
        outboundProcessed,
        outboundPercentComplete,
        outboundFailed
    }
}