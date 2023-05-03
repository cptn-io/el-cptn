FROM caddy:latest

COPY Caddyfile /etc/caddy/Caddyfile

COPY build /usr/share/caddy/ux

EXPOSE 8080

CMD ["caddy", "run", "--config", "/etc/caddy/Caddyfile"]
