import SourceFilter from "../SourceFilter";
import { render } from "@testing-library/react";

describe("ListFilter", () => {
    it("should render", () => {
        const wrapper = render(<SourceFilter status={null} setStatus={() => { }} />);
        expect(wrapper).toMatchSnapshot();
    });

    it("None should be shown active", () => {
        const wrapper = render(<SourceFilter status={null} setStatus={() => { }} />);
        expect(wrapper.getByText("None")).toHaveClass("text-secondary");
    });

    it("Passed filter should be shown active", () => {
        const wrapper = render(<SourceFilter status={'COMPLETED'} setStatus={() => { }} />);
        expect(wrapper.getByText("Sent to Pipelines")).toHaveClass("text-secondary");
    });

    it("should call setStatus on click", () => {
        const setStatus = jest.fn();
        const wrapper = render(<SourceFilter status={null} setStatus={setStatus} />);
        wrapper.getByText("Failed").click();
        expect(setStatus).toHaveBeenCalledWith('FAILED');
    });
}); 