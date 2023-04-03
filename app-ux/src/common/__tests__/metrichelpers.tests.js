import { processInboundMetrics, processOutboundMetrics } from "../metricHelpers";

describe("processInboundMetrics", () => {
    it("should return inbound metrics", () => {
        const data = {
            inbound: [
                {
                    count: 1,
                    state: "COMPLETED",
                },
                {
                    count: 2,
                    state: "FAILED",
                },
                {
                    count: 3,
                    state: "QUEUED",
                },
            ],
        };
        const inboundMetrics = processInboundMetrics(data);
        expect(inboundMetrics).toEqual({
            inboundTotal: 6,
            inboundCompleted: 1,
            inboundProcessed: 3,
            inboundPercentComplete: 50,
            inboundFailed: 2,
        });
    });

    it("should return inbound metrics with no inbound data", () => {
        const data = {};
        const inboundMetrics = processInboundMetrics(data);
        expect(inboundMetrics).toEqual({
            inboundTotal: 0,
            inboundCompleted: 0,
            inboundProcessed: 0,
            inboundPercentComplete: 100,
            inboundFailed: 0,
        });
    });

    it("should return inbound metrics with no inbound data", () => {
        const data = {
            inbound: [],
        };
        const inboundMetrics = processInboundMetrics(data);
        expect(inboundMetrics).toEqual({
            inboundTotal: 0,
            inboundCompleted: 0,
            inboundProcessed: 0,
            inboundPercentComplete: 100,
            inboundFailed: 0,
        });
    });
});

describe("processOutboundMetrics", () => {
    it("should return outbound metrics", () => {
        const data = {
            outbound: [
                {
                    count: 1,
                    state: "COMPLETED",
                },
                {
                    count: 2,
                    state: "FAILED",
                },
                {
                    count: 3,
                    state: "QUEUED",
                },
            ],
        };
        const outboundMetrics = processOutboundMetrics(data);
        expect(outboundMetrics).toEqual({
            outboundTotal: 6,
            outboundCompleted: 1,
            outboundProcessed: 3,
            outboundPercentComplete: 50,
            outboundFailed: 2,
        });
    });

    it("should return outbound metrics with no outbound data", () => {
        const data = {};
        const outboundMetrics = processOutboundMetrics(data);
        expect(outboundMetrics).toEqual({
            outboundTotal: 0,
            outboundCompleted: 0,
            outboundProcessed: 0,
            outboundPercentComplete: 100,
            outboundFailed: 0,
        });
    });

    it("should return outbound metrics with no outbound data", () => {
        const data = {
            outbound: [],
        };
        const outboundMetrics = processOutboundMetrics(data);
        expect(outboundMetrics).toEqual({
            outboundTotal: 0,
            outboundCompleted: 0,
            outboundProcessed: 0,
            outboundPercentComplete: 100,
            outboundFailed: 0,
        });
    });
});