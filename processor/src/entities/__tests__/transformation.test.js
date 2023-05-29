process.env.CPTN_CRYPTO_SECRET = 'secret';

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

    test("should create a new transformation for row", () => {
        const row = {
            id: 1,
            script: "some script",
            active: false,
            version: 1,
            config: [
                {
                    key: "test",
                    value: "test",
                    secret: false
                }
            ]
        };
        const transformation = new Transformation(row);
        expect(transformation).toBeInstanceOf(Transformation);
        expect(transformation.id).toEqual(row.id);
        expect(transformation.script).toEqual(row.script);
        expect(transformation.version).toEqual(row.version);
        expect(transformation.active).toEqual(row.active);
        expect(transformation.type).toEqual(row.type);
        expect(transformation.config).toEqual({ test: "test" });
    });

    test("should decrypt values in config for a new transformation", () => {
        const row = {
            id: 10,
            script: "console.log('hello world')",
            active: true,
            version: 10,
            config: [
                {
                    key: "test",
                    value: "test",
                    secret: false
                },
                {
                    key: "foo",
                    value: "jJhIyGoZG+mEv/DMC4AWuERHRs5oTYpqAsnn0Dr6RInRnIBN/ijJZkMlHQmzaFAe40Yo",//bar
                    secret: true
                }
            ]
        };
        const transformation = new Transformation(row);
        expect(transformation).toBeInstanceOf(Transformation);
        expect(transformation.id).toEqual(row.id);
        expect(transformation.script).toEqual(row.script);
        expect(transformation.version).toEqual(row.version);
        expect(transformation.active).toEqual(row.active);
        expect(transformation.type).toEqual(row.type);
        expect(transformation.config).toEqual({ test: "test", foo: "bar" });
    });

});