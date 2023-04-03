import { renderErrors } from "../formHelpers";

describe("renderErrors", () => {

    it("should return valid error message", () => {
        const error = {
            fieldErrors: { testField: ["testError"] },
        };

        const field = "testField";
        const result = renderErrors(error, field);

        expect(result).toMatchSnapshot();
    });

    it("should return null if no error", () => {
        const error = null;
        const field = "testField";
        const result = renderErrors(error, field);
        expect(result).toBeNull();
    });

    it("should return null if no fieldErrors", () => {
        const error = {};
        const field = "testField";
        const result = renderErrors(error, field);
        expect(result).toBeNull();
    });

    it("should return null if no fieldErrors for field", () => {
        const error = {
            fieldErrors: {},
        };
        const field = "testField";
        const result = renderErrors(error, field);
        expect(result).toBeNull();
    });

    it("should return null if no fieldErrors for field", () => {
        const error = {
            fieldErrors: {
                testField: [],
            },
        };
        const field = "testField";
        const result = renderErrors(error, field);
        expect(result).toBeNull();
    });

    it("should return null if no fieldErrors for field", () => {
        const error = {
            fieldErrors: {
                testField: ["testError"],
            },
        };
        const field = "testField";
        const result = renderErrors(error, field);
        expect(result).not.toBeNull();
    });
});