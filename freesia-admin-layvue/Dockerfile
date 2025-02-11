# 第一阶段：构建阶段
FROM node:18 AS builder
# 设置工作目录
WORKDIR /app
# 复制 package.json 和 yarn.lock 文件
COPY package.json yarn.lock ./
# 安装依赖
RUN yarn install --frozen-lockfile
# 复制项目源代码
COPY . .
# 打包项目
RUN yarn build
# 第二阶段：运行阶段
FROM nginx:1.22
# 将构建结果复制到 Nginx 的默认静态文件目录
COPY --from=builder /app/dist /usr/share/nginx/html
# 暴露端口
EXPOSE 80
# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]