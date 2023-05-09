process.env.CPTN_CRYPTO_SECRET = 'secret';

const Destination = require("../Destination");
const ScriptedStep = require("../ScriptedStep");

describe("Destination", () => {

    test("should create a new destination for row", () => {
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
        const destination = new Destination(row);
        expect(destination).toBeInstanceOf(Destination);
        expect(destination.id).toEqual(row.id);
        expect(destination.script).toEqual(row.script);
        expect(destination.version).toEqual(row.version);
        expect(destination.active).toEqual(row.active);
        expect(destination.type).toEqual(row.type);
        expect(destination.config).toEqual({ test: "test" });
    });

    test("should decrypt values in config for a new destination", () => {
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
        const destination = new Destination(row);
        expect(destination).toBeInstanceOf(Destination);
        expect(destination.id).toEqual(row.id);
        expect(destination.script).toEqual(row.script);
        expect(destination.version).toEqual(row.version);
        expect(destination.active).toEqual(row.active);
        expect(destination.type).toEqual(row.type);
        expect(destination.config).toEqual({ test: "test", foo: "bar" });
    });

    test("should handle invalid config object", () => {
        const row = {
            id: 10,
            script: "console.log('hello world')",
            active: true,
            version: 10,
            config: null
        };
        const destination = new Destination(row);
        expect(destination).toBeInstanceOf(Destination);
        expect(destination).toBeInstanceOf(ScriptedStep);
        expect(destination.config).toEqual({});

        const row2 = {
            id: 10,
            script: "console.log('hello world')",
            active: true,
            version: 10,
            config: "test"
        };

        const destination2 = new Destination(row2);
        expect(destination2).toBeInstanceOf(Destination);
        expect(destination2.config).toEqual({});
    });

    test("should handle invalid config object", () => {
        const row = {
            id: 10,
            script: "console.log('hello world')",
            active: true,
            version: 10,
            config: [{
                key: "foo",
                value: "",
                secret: true
            }]
        };
        const destination = new Destination(row);
        expect(destination).toBeInstanceOf(Destination);
        expect(destination.config).toEqual({ "foo": "" });
    });
});