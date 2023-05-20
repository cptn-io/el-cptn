jest.mock('redis', () => ({
    createClient: jest.fn().mockImplementation(() => ({
        isOpen: true,
        connect: jest.fn(),
        set: jest.fn(),
        get: jest.fn(),
        quit: jest.fn(),
    })),
}));

let getClient, set, get;

describe('cache', () => {
    beforeEach(() => {
        jest.resetModules();
        const cache = require('..');
        getClient = cache.getClient;
        set = cache.set;
        get = cache.get;
    });

    test('getClient should call connect if connection is not open', async () => {
        const client = await getClient();
        client.isOpen = false;
        await getClient();
        expect(client.connect).toHaveBeenCalled();
    });

    test('getClient should not call connect if connection is open', async () => {
        const client = await getClient();
        client.isOpen = true;
        await getClient();
        expect(client.connect).not.toHaveBeenCalled();
    });

    test('should set object to redis', async () => {
        const client = await getClient();
        const key = 'test-key';
        const value = { name: 'test-name', age: 30 };
        await set(key, value);
        expect(client.set).toHaveBeenCalledWith(key, JSON.stringify(value));
    });

    test('should get object to redis', async () => {
        const client = await getClient();
        const key = 'test-key';
        const value = { name: 'test-name', age: 30 };

        client.get = jest.fn().mockImplementationOnce(() => JSON.stringify(value));
        const cached = await get(key);
        expect(client.get).toHaveBeenCalledWith(key, '.');

        expect(cached).toEqual(value);
    });

    test('should get null for a key if not cached', async () => {
        await getClient();
        const key = 'non-existent-key';
        const cached = await get(key);

        expect(cached).toBeNull();
    });
});
