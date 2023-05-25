import ListFilter from "../OutboundEventFilter";
import { render } from "@testing-library/react";

describe("ListFilter", () => {
    it("should render", () => {
        const wrapper = render(<ListFilter status={null} setStatus={() => { }} />);
        expect(wrapper).toMatchSnapshot();
    });

    it("None should be shown active", () => {
        const wrapper = render(<ListFilter status={null} setStatus={() => { }} />);
        expect(wrapper.getByText("None")).toHaveClass("text-primary");
    });

    it("Passed filter should be shown active", () => {
        const wrapper = render(<ListFilter status={'COMPLETED'} setStatus={() => { }} />);
        expect(wrapper.getByText("Completed")).toHaveClass("text-primary");
    });

    it("should call setStatus on click", () => {
        const setStatus = jest.fn();
        const wrapper = render(<ListFilter status={null} setStatus={setStatus} />);
        wrapper.getByText("Queued").click();
        expect(setStatus).toHaveBeenCalledWith('QUEUED');
    });
}); 