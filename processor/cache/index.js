const { createClient } = require('redis');

const client = createClient();

async function getClient() {
    if (!client.isOpen) {
        await client.connect();
    }
    return client
}

async function set(key, value) {
    const client = await getClient();
    client.json.set(key, '.', value)
}

async function get(key) {
    const client = await getClient();
    return client.json.get(key, '.')
}

module.exports = {
    getClient,
    set,
    get
};