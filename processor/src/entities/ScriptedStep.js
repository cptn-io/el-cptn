const { decrypt } = require("../crypto");

class ScriptedStep {

    constructor(row) {
        this.id = row.id;
        this.script = row.script;
        this.version = row.version;
        this.active = row.active;
        this.config = {};
        if (row.config && Array.isArray(row.config)) {
            row.config.forEach(item => {
                const key = item.key;
                let value = item.value;
                if (item.secret && item.value) {
                    value = decrypt(item.value)
                }
                this.config[key] = value;
            });
        }
    }
}

module.exports = ScriptedStep;