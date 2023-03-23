const { spawnSync } = require('child_process');

function isModuleInstalled(module) {
    try {
        require(module);
        return true;
    } catch (err) {
        return false;
    }
}

function findMissingRequiredModules(script) {

    const requiredModules = [];
    const regex = /require\(['"](.+)['"]\)/g;
    let match;
    while ((match = regex.exec(script)) !== null) {
        requiredModules.push(match[1]);
    }

    return requiredModules.filter((module) => !isModuleInstalled(module));
}

function installModule(module) {
    console.info(`Installing missing node module ${module}`)
    const result = spawnSync('npm', ['install', '--no-save', module], { stdio: 'inherit' });
    if (result.status !== 0) {
        console.error(`Failed to install module ${module}.`);
    }
}


module.exports = {
    isModuleInstalled,
    findMissingRequiredModules,
    installModule
}