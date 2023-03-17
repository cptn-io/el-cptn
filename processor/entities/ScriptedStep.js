class ScriptedStep {

    constructor(row) {
        this.id = row.id;
        this.script = row.script;
        this.version = row.version;
    }
}

module.exports = ScriptedStep;