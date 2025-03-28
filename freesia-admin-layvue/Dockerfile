## 第一阶段：构建阶段
#FROM node:18 AS build-stage
## 设置编码
#ENV LANG C.UTF-8
#ENV LC_ALL C.UTF-8
## 设置工作目录
#WORKDIR /
## 复制 package.json 和 yarn.lock 文件
#COPY package.json yarn.lock ./
## 安装依赖
#RUN yarn install
## 复制项目源代码
#COPY . .
## 打包项目
#RUN yarn build
# 第二阶段：运行阶段
FROM nginx:1.22 as production-stage
# 将构建结果复制到 Nginx 的默认静态文件目录
COPY --from=build-stage /dist /usr/share/nginx/html
# 暴露端口
EXPOSE 8385
# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]