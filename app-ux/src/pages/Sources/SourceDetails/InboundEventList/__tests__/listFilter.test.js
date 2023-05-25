import InboundEventFilter from "../InboundEventFilter";
import { render } from "@testing-library/react";

describe("ListFilter", () => {
    it("should render", () => {
        const wrapper = render(<InboundEventFilter status={null} setStatus={() => { }} />);
        expect(wrapper).toMatchSnapshot();
    });

    it("None should be shown active", () => {
        const wrapper = render(<InboundEventFilter status={null} setStatus={() => { }} />);
        expect(wrapper.getByText("None")).toHaveClass("text-primary");
    });

    it("Passed filter should be shown active", () => {
        const wrapper = render(<InboundEventFilter status={'COMPLETED'} setStatus={() => { }} />);
        expect(wrapper.getByText("Sent to Pipelines")).toHaveClass("text-primary");
    });

    it("should call setStatus on click", () => {
        const setStatus = jest.fn();
        const wrapper = render(<InboundEventFilter status={null} setStatus={setStatus} />);
        wrapper.getByText("Failed").click();
        expect(setStatus).toHaveBeenCalledWith('FAILED');
    });
}); 