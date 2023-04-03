import IntervalSelector, { parseInterval } from "..";
import { fireEvent, render } from "@testing-library/react";

describe("IntervalSelector", () => {


    it("should render with interval", () => {
        const setInterval = jest.fn();
        const wrapper = render(<IntervalSelector interval={5} setInterval={setInterval} />);
        expect(wrapper).toMatchSnapshot();
    });

    it("show call setInterval on click", () => {
        const setInterval = jest.fn();
        const wrapper = render(<IntervalSelector interval={5} setInterval={setInterval} />);
        fireEvent.click(wrapper.getByText("1d"));
        expect(setInterval).toHaveBeenCalledWith(1440);
    });

    it("parseInterval should return correct string", () => {
        expect(parseInterval(5)).toEqual("Last 5 mins");
        expect(parseInterval(60)).toEqual("Last 1 hour");
        expect(parseInterval(1440)).toEqual("Last 24 hours");
    });

});