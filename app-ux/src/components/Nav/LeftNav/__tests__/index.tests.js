import LeftNav from "..";
import { fireEvent, render } from "@testing-library/react";
import { MemoryRouter } from 'react-router-dom'

jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useLocation: () => ({
        pathname: "/home"
    })
}));

describe("LeftNav", () => {
    it("should render", () => {
        const wrapper = render(<MemoryRouter><LeftNav /></MemoryRouter>);
        expect(wrapper).toMatchSnapshot();
    });

    it("should render active link", () => {
        const wrapper = render(<MemoryRouter><LeftNav /></MemoryRouter>);
        expect(wrapper.getByText("Home")).toHaveClass("active");
    });
}); 