FROM openjdk:21

ENV DEBIAN_FRONTEND=noninteractive \
    CODEX_HOME=/opt/codex \
    FREESIA_FUSEBEAN_SKILL_AUTO_INSTALL=false \
    FREESIA_FUSEBEAN_SKILL_ROOT=/opt/codex/skills/image-to-pindou

WORKDIR /usr/local/src

# Install Node.js and build prerequisites for the image-to-pindou skill.
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        ca-certificates \
        curl \
        git \
        gnupg \
        python3 \
        make \
        g++ \
    && rm -rf /var/lib/apt/lists/*

RUN curl -fsSL https://deb.nodesource.com/setup_22.x | bash - \
    && apt-get install -y --no-install-recommends nodejs \
    && rm -rf /var/lib/apt/lists/*

# Bake the skill into the image so the backend container can execute it locally.
RUN mkdir -p /opt/codex/skills \
    && git clone --depth 1 https://github.com/kuizuo/pin-dou.git /tmp/pin-dou \
    && rm -rf /opt/codex/skills/image-to-pindou \
    && cp -R /tmp/pin-dou/skills/image-to-pindou /opt/codex/skills/ \
    && cd /opt/codex/skills/image-to-pindou \
    && npm install \
    && node scripts/self-test.mjs \
    && rm -rf /tmp/pin-dou

# Copy the backend jar built by Maven.
COPY target/freesia-web-app.jar /usr/local/src/freesia-web-app.jar
EXPOSE 8570
# 设置容器启动命令
CMD ["java", "-jar", "/usr/local/src/freesia-web-app.jar"]