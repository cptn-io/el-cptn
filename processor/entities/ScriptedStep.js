class ScriptedStep {

    constructor(row) {
        this.id = row.id;
        this.script = row.script;
        this.version = row.version;
        this.active = row.active;
    }
}

module.exports = ScriptedStep;