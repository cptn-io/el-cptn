const { spawnSync } = require('child_process');
const logger = require('./logger');

function isModuleInstalled(moduleName) {
    const isBuiltinModule = require('module').builtinModules.includes(moduleName);
    return isBuiltinModule || moduleExistsInNodeModules(moduleName);
}

function moduleExistsInNodeModules(moduleName) {
    try {
        require.resolve(`${moduleName}/package.json`);
        return true;
    } catch (error) {
        logger.info(`Module ${moduleName} not found in node_modules. Will attempt to install it.`);
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
    logger.info(`Installing missing node module ${module}`);
    const result = spawnSync('npm', ['install', module], { stdio: 'inherit' });
    if (result.status !== 0) {
        logger.error(`Failed to install module ${module}.`);
    }
}


module.exports = {
    isModuleInstalled,
    findMissingRequiredModules,
    installModule
};