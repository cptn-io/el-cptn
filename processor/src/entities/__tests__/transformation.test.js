const ScriptedStep = require("../ScriptedStep");
const Transformation = require("../Transformation");

describe("Transformation", () => {
    test("should create a new transformation for row", () => {
        const row = {
            id: 1,
            script: "some script",
            active: true,
            version: 1
        };
        const transformation = new Transformation(row);
        expect(transformation).toBeInstanceOf(Transformation);
        expect(transformation.id).toEqual(row.id);
        expect(transformation.script).toEqual(row.script);
        expect(transformation.version).toEqual(row.version);
        expect(transformation.active).toEqual(row.active);

        expect(transformation).toBeInstanceOf(ScriptedStep);
    });
});