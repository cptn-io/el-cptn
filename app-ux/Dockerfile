FROM caddy:2.7.6-alpine

LABEL org.opencontainers.image.description="Caddy server with UX"

COPY Caddyfile /etc/caddy/Caddyfile

COPY build /usr/share/caddy/ux

CMD ["caddy", "run", "--config", "/etc/caddy/Caddyfile"]
