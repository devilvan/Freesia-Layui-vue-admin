FROM nginx:1.22 as product-stage
COPY dist/build/h5/ /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 8386
CMD ["nginx", "-g", "daemon off;"]
