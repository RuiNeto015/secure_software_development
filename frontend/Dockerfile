FROM node:18.19.0

WORKDIR /usr/src/app

COPY . /usr/src/app

RUN npm install -g @angular/cli

RUN npm install

ENTRYPOINT ["npm", "start"]