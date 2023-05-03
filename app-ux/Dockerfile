FROM caddy:latest

COPY Caddyfile /etc/caddy/Caddyfile

COPY build /usr/share/caddy/ux

CMD ["caddy", "run", "--config", "/etc/caddy/Caddyfile"]
