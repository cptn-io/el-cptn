const ScriptedStep = require("./ScriptedStep");
const { decrypt } = require("../crypto");

class Destination extends ScriptedStep {
    constructor(row) {
        super(row)
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

module.exports = Destination;