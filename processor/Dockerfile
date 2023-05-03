FROM node:18-alpine3.17

RUN mkdir -p /app
COPY src /app/src
COPY package*.json /app
WORKDIR /app

RUN npm install

CMD [ "npm", "run", "start" ]
