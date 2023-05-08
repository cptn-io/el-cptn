FROM node:18-alpine3.17

LABEL org.opencontainers.image.description="Event Processor"

RUN mkdir -p /app
COPY src /app/src
COPY launcher.js /app/launcher.js
COPY package*.json /app/
WORKDIR /app

RUN npm install

CMD [ "npm", "run", "start" ]
