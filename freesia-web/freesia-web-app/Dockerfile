# 多阶段构建：JDK21 + Node24 + image-to-pindou skill
# 注意：构建上下文必须是 freesia-web/ 目录（要同时取到 fusebean 的 skill 和 app 的 jar）
#   docker build -f freesia-web/freesia-web-app/Dockerfile -t freesia-admin:1.5.0 freesia-web/

# ---- Stage 1: 构建 skill 依赖（Node 24，Debian bookworm，glibc）----
FROM node:24-bookworm-slim AS skill-builder
WORKDIR /opt/skills/image-to-pindou
# 先拷贝依赖清单，利用构建缓存；lockfile 保证 npm ci 可复现
COPY freesia-web-fusebean/skills/image-to-pindou/package.json \
     freesia-web-fusebean/skills/image-to-pindou/package-lock.json ./
RUN npm ci --omit=dev
# 拷贝完整 skill（SKILL.md、scripts、palettes、references）
COPY freesia-web-fusebean/skills/image-to-pindou/ ./

# ---- Stage 2: 运行时镜像 ----
FROM eclipse-temurin:21-jre-noble
# 将 Node 24 运行时（node/npm/npx/corepack）从官方镜像拷入 /usr/local
COPY --from=node:24-bookworm-slim /usr/local /usr/local
# skill 及预编译的 sharp 原生依赖
COPY --from=skill-builder /opt/skills/image-to-pindou /opt/skills/image-to-pindou
# 应用 jar
COPY freesia-web-app/target/freesia-web-app.jar /app/freesia-web-app.jar

# 拼豆 skill：指向容器内路径，关闭运行期 npm install（依赖已在镜像中预装）
# 如应用需要 JDK 专属能力，可将基础镜像换成 eclipse-temurin:21-jdk-noble
ENV FREESIA_FUSEBEAN_SKILL_ENABLED=true \
    FREESIA_FUSEBEAN_SKILL_ROOT=/opt/skills/image-to-pindou \
    FREESIA_FUSEBEAN_SKILL_NODE_COMMAND=node \
    FREESIA_FUSEBEAN_SKILL_NPM_COMMAND=npm \
    FREESIA_FUSEBEAN_SKILL_AUTO_INSTALL=false \
    SPRING_PROFILES_ACTIVE=dev
EXPOSE 8570
CMD ["java", "-jar", "/app/freesia-web-app.jar"]
