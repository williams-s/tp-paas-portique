FROM node:18 AS build
WORKDIR /app

ARG VITE_WS_URL
ENV VITE_WS_URL=${VITE_WS_URL}

COPY package*.json ./
RUN npm install
COPY . .

RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]