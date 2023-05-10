const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function (app) {
    app.use(
        '/event/*',
        createProxyMiddleware({
            target: 'http://localhost:8081',
            changeOrigin: true,
        })
    );
    app.use(
        ['/api/*', '/login', '/logout', '/mgmt/*', '/oauth2/*'],
        createProxyMiddleware({
            target: 'http://localhost:8080',
            changeOrigin: false,
        })
    );

};