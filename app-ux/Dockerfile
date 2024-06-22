FROM caddy:2.8.4-alpine

LABEL org.opencontainers.image.description="Caddy server with UX"

COPY Caddyfile /etc/caddy/Caddyfile

COPY build /usr/share/caddy/ux

CMD ["caddy", "run", "--config", "/etc/caddy/Caddyfile"]
