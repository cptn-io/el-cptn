async function runStep(stepScript, evt, ctx) {
    //TODO use sandbox environment for running scripts
    const func = new Function("evt", "ctx", stepScript);
    return await func(evt, ctx);
}

module.exports = {
    runStep
}