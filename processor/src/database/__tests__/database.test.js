const { Pool } = require('pg');

describe('pgPool', () => {
    test('should create a new pool', () => {
        const pgPool = require('..');
        expect(pgPool).toBeInstanceOf(Pool);
    });
});