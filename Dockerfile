# Stage 1: Build the frontend app with Node.js
FROM node:20-alpine AS build

# Set the working directory in the container
WORKDIR /app

# Copy the package.json and package-lock.json files
COPY package*.json ./

# Install all dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

# Build the application for production
RUN npm run build

# Stage 2: Serve the built app with NGINX
FROM nginx:alpine

# Copy the build output from the previous stage to NGINX's html directory
COPY --from=build /app/dist /usr/share/nginx/html

# Copy the custom nginx configuration file
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 8080 on the container
EXPOSE 8080

# Start NGINX server
CMD ["nginx", "-g", "daemon off;"]
