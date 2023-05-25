import PipelineFilter from "../PipelineFilter";
import { render } from "@testing-library/react";

describe("ListFilter", () => {
    it("should render", () => {
        const wrapper = render(<PipelineFilter status={null} setStatus={() => { }} />);
        expect(wrapper).toMatchSnapshot();
    });

    it("None should be shown active", () => {
        const wrapper = render(<PipelineFilter status={null} setStatus={() => { }} />);
        expect(wrapper.getByText("None")).toHaveClass("text-secondary");
    });

    it("Passed filter should be shown active", () => {
        const wrapper = render(<PipelineFilter status={'COMPLETED'} setStatus={() => { }} />);
        expect(wrapper.getByText("Completed events")).toHaveClass("text-secondary");
    });

    it("should call setStatus on click", () => {
        const setStatus = jest.fn();
        const wrapper = render(<PipelineFilter status={null} setStatus={setStatus} />);
        wrapper.getByText("Failed events").click();
        expect(setStatus).toHaveBeenCalledWith('FAILED');
    });
}); 