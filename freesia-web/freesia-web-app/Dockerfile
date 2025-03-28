# dockerfile for java project
FROM openjdk:8

# 设置工作目录
WORKDIR /app

# 将构建好的 JAR 文件复制到容器内
COPY target/freesia-web-app.jar /app/freesia-web-app.jar

EXPOSE 8570

# 设置容器启动命令
CMD ["java", "-jar", "freesia-web-app.jar"]