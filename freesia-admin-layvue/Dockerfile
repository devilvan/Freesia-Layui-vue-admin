# 第一阶段：构建阶段
FROM node:18 as build-stage
## 设置编码
ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8
# 设置内容限制
ENV NODE_OPTIONS="--max_old_space_size=512"
# 设置工作目录
WORKDIR /
# 复制 package.json 和 yarn.lock 文件
COPY package.json pnpm-lock.yaml ./
# 安装pnpm
RUN npm install -g pnpm
# 安装依赖
RUN pnpm install
# 复制项目源代码
COPY . .
# 打包项目
RUN pnpm build:prod
# 第二阶段：运行阶段
FROM nginx:1.22 as product-stage
# 将构建结果复制到 Nginx 的默认静态文件目录
COPY dist/ /usr/share/nginx/html
# 暴露端口
EXPOSE 8385
# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]