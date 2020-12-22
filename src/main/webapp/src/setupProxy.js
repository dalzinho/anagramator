const proxy = require("http-proxy-middleware");
const morgan = require("morgan");

module.exports = app => {
    app.use(
        "/api/v1",
        proxy.createProxyMiddleware({
            target: "http://localhost:8080",
            changeOrigin: true
        })
    );

    app.use(morgan("combined"));
};