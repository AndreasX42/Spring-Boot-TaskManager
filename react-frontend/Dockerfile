FROM node:20-alpine

EXPOSE 5173

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY ./ ./

# Install nodemon for hot reloading
RUN npm install --save-dev nodemon

CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]