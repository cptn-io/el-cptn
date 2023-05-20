const { createClient } = require('redis');

const client = createClient({
    socket: {
        host: 'redis',
        port: '6379'
    }
});

async function getClient() {
    if (!client.isOpen) {
        await client.connect();
    }
    return client
}

async function set(key, value) {
    const client = await getClient();
    await client.set(key, JSON.stringify(value));
}

async function get(key) {
    const client = await getClient();
    const cached = await client.get(key, '.');
    if (cached) {
        return JSON.parse(cached);
    }
    return null;
}

module.exports = {
    getClient,
    set,
    get
};