# dockerfile for java project
FROM openjdk:17
# 设置工作目录
WORKDIR /
# 将构建好的 JAR 文件复制到容器内
COPY target/freesia-web-app.jar /usr/local/src/freesia-web-app.jar
EXPOSE 8570
# 设置容器启动命令
CMD ["java", "-jar", "/usr/local/src/freesia-web-app.jar"]