const ScriptedStep = require("../ScriptedStep");

describe("ScriptedStep", () => {
    test("should create a new scripted step for row", () => {
        const row = {
            id: 1,
            script: "some script",
            active: true,
            version: 1
        };
        const scriptedStep = new ScriptedStep(row);
        expect(scriptedStep).toBeInstanceOf(ScriptedStep);
        expect(scriptedStep.id).toEqual(row.id);
        expect(scriptedStep.script).toEqual(row.script);
        expect(scriptedStep.version).toEqual(row.version);
        expect(scriptedStep.active).toEqual(row.active);
    });

});