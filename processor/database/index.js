const { Pool } = require('pg');

const pgPool = new Pool();

module.exports = pgPool;