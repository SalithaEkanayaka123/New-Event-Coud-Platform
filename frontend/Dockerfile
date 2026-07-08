FROM nginx:1.27-alpine

# Copy the New Event template files (index.html, css/, js/, images/, etc.)
# into Nginx's default web root.
COPY . /usr/share/nginx/html

# Custom config for clean single-page routing behavior.
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
