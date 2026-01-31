# Freesia 项目介绍

## 项目概述

Freesia 是一款基于 SpringBoot、Layui-vue-Admin、Layui-vue 的后台权限管理和记账系统。它在 Layui-vue-Admin 的前端模板基础上对接了完整的后端功能，实现了从前端页面到后端逻辑的全流程功能。

### 项目特点

- **完整的权限管理体系**：基于 Sa-Token 实现的细粒度权限控制
- **多功能记账系统**：支持预算管理、支出记录、用户分配等记账功能
- **丰富的系统管理功能**：用户、角色、部门、菜单、字典、配置等完整的系统管理模块
- **多租户架构**：支持个人、企业、组织等不同类型的租户
- **国际化支持**：内置多语言支持
- **响应式设计**：适配不同屏幕尺寸
- **完整的技术栈**：前端 Vue3 + TypeScript + Layui-vue，后端 SpringBoot + MyBatis-Plus

## 技术栈

### 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.3.4 | 前端框架 |
| TypeScript | 4.5.4 | 类型系统 |
| Layui-vue | 2.22.0 | UI组件库 |
| Layui-vue-admin | - | 前端模板 |
| Pinia | 2.0.32 | 状态管理 |
| Vite | 6.2.2 | 构建工具 |
| Axios | 1.3.4 | HTTP客户端 |
| ECharts | 5.4.1 | 数据可视化 |
| NodeJS | 18.12.1 | 运行环境 |
| pnpm | 10.6.5 | 包管理工具 |

### 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| JDK | 17 | 开发语言 |
| SpringBoot | 2.7.0 | 项目管理 |
| Spring Data JPA | - | 持久层 |
| MyBatis-Plus | 3.5.3.1 | 持久层 |
| SpringDoc | 1.8.0 | 接口文档 |
| Sa-Token | 1.35.0.RC | 权限管理 |
| P6spy | 3.9.1 | SQL日志监控 |
| FastJson2 | 2.0.50 | JSON处理 |
| Hutool | 5.8.18 | 工具类 |
| EasyExcel | 3.2.1 | Excel处理 |
| MySQL | 8.3.0 | 关系型数据库 |
| Redis | 7.0.0 | 缓存数据库 |
| MinIO | - | 对象存储 |

## 项目结构

### 整体结构

```
Freesia/
├── freesia-admin-layvue/       # 前端项目
├── freesia-common/             # 后端公共模块
├── freesia-extends/            # 后端扩展模块
├── freesia-web/                # 后端Web模块
│   ├── freesia-web-admin/      # 后端管理模块
│   ├── freesia-web-api/        # 后端API模块
│   ├── freesia-web-app/        # 后端应用模块
│   ├── freesia-web-icon/       # 后端图标模块
│   └── freesia-web-account/    # 后端记账模块
├── sql/                        # 数据库脚本
├── .gitignore                  # Git忽略文件
├── LICENSE                     # 许可证文件
├── README.md                   # 项目说明
└── pom.xml                     # Maven配置文件
```

### 前端项目结构

```
freesia-admin-layvue/
├── public/                     # 静态资源
├── src/
│   ├── api/                    # API接口
│   ├── assets/                 # 资源文件
│   ├── directives/             # 指令
│   ├── lang/                   # 国际化
│   ├── layouts/                # 布局组件
│   ├── library/                # 工具库
│   ├── router/                 # 路由
│   ├── store/                  # 状态管理
│   ├── styles/                 # 样式文件
│   ├── types/                  # 类型定义
│   ├── util/                   # 工具函数
│   ├── views/                  # 页面组件
│   ├── App.vue                 # 根组件
│   └── main.ts                 # 入口文件
├── .env.development            # 开发环境配置
├── .env.production             # 生产环境配置
├── package.json                # 前端依赖
└── vite.config.ts              # Vite配置
```

### 后端项目结构

```
freesia-web/
├── freesia-web-account/        # 记账模块
│   ├── src/main/java/com/freesia/account/
│   │   ├── constant/           # 常量
│   │   ├── dto/                # 数据传输对象
│   │   ├── mapper/             # 数据映射
│   │   ├── po/                 # 持久对象
│   │   ├── service/            # 业务逻辑
│   │   └── vo/                 # 视图对象
│   └── src/main/resources/
│       ├── i18n/               # 国际化资源
│       └── mapper/             # XML映射文件
├── freesia-web-admin/          # 管理模块
│   ├── src/main/java/com/freesia/
│   │   ├── controller/         # 控制器
│   │   ├── dto/                # 数据传输对象
│   │   ├── entity/             # 实体类
│   │   ├── mapper/             # 数据映射
│   │   └── po/                 # 持久对象
└── pom.xml                     # Maven配置
```

## 核心功能模块

### 1. 系统管理

| 模块 | 功能描述 |
|------|----------|
| 用户管理 | 用户信息管理、角色分配、状态管理 |
| 角色管理 | 角色信息管理、菜单权限分配、用户分配、按钮权限管理 |
| 部门管理 | 部门层级管理、负责人设置、状态管理 |
| 菜单管理 | 菜单层级管理、路由配置、权限标识设置 |
| 字典管理 | 字典键值管理、数据字典配置 |
| 参数配置 | 系统参数配置、全局变量管理 |
| 日志管理 | 登录日志、操作记录、敏感日志 |
| 租户管理 | 多租户管理、租户类型配置 |
| 文件管理 | 文件上传、下载、管理（基于 MinIO） |

### 2. 记账系统

| 模块 | 功能描述 |
|------|----------|
| 预算管理 | 预算设置、预算容量查询、预算类型管理 |
| 支出记录 | 支出类型管理、支出记录、支出统计 |
| 用户分配 | 支出用户分配、分配状态管理 |
| 数据分析 | 支出趋势分析、类型占比分析、排名分析 |

### 3. 公共功能

| 模块 | 功能描述 |
|------|----------|
| 图标管理 | 图标模板管理、图标库管理 |
| URL配置 | 系统URL管理、链接配置 |
| 待办事项 | 个人待办管理、任务跟踪 |
| 国际化 | 多语言支持、语言切换 |

### 4. 系统监控

| 模块 | 功能描述 |
|------|----------|
| Redis监控 | Redis面板、缓存状态监控 |
| Gitee集成 | Gitee提交更新、代码仓库监控 |

### 5. 世界时钟

提供全球时区时钟功能，支持多地区时间显示和管理。

## 快速开始

### 前端准备

1. **进入前端目录**
   ```bash
   cd freesia-admin-layvue
   ```

2. **安装依赖**
   ```bash
   # 使用 pnpm
   pnpm install
   
   # 或使用 yarn
   yarn install
   ```

3. **启动开发服务器**
   ```bash
   # 使用 pnpm
   pnpm run dev
   
   # 或使用 yarn
   yarn run dev
   ```

### 后端准备

1. **启动依赖服务**
   - Redis-server 7.0.0
   - MinIO 对象存储服务

2. **数据库准备**
   - 执行 `sql/freesia.sql` 脚本初始化数据库

3. **编译打包**
   ```bash
   mvn clean package -DskipTests
   ```

4. **启动后端服务**
   ```bash
   java -jar freesia-web/freesia-web-admin/target/freesia-web-admin-1.5.0.jar
   ```

### 访问系统

- **前端地址**：`http://localhost:5173`
- **后端地址**：`http://localhost:8570`
- **API文档**：`http://localhost:8570/swagger-ui.html`

## 配置说明

### 前端配置

前端配置文件位于 `freesia-admin-layvue/.env.development` 和 `.env.production`：

```env
# 环境
VITE_APP_ENV = 'development'
# 后端地址
VITE_APP_BASE_URL = 'http://localhost:8570'
# 上下文路径
VITE_APP_CONTEXT_PATH = '/'
# 接口文档地址
VITE_APP_SPRING_DOC_PATH = '${VITE_APP_BASE_URL}/swagger-ui.html'
# 文件上传地址
VITE_APP_UPLOAD_PATH = '${VITE_APP_BASE_URL}/common/sysOssController/upload'
```

### 后端配置

后端配置文件通过 Maven  profiles 管理，支持 dev、test、prod 三种环境：

```xml
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <profiles.active>dev</profiles.active>
            <logging.level>debug</logging.level>
        </properties>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
    </profile>
    <!-- 其他环境配置 -->
</profiles>
```

## 部署指南

### 前端部署

1. **构建生产版本**
   ```bash
   cd freesia-admin-layvue
   pnpm run build:prod
   ```

2. **部署到 Nginx**
   将 `dist` 目录内容复制到 Nginx 静态目录，配置 Nginx：

   ```nginx
   server {
       listen 80;
       server_name your-domain.com;
       
       location / {
           root /path/to/freesia/dist;
           index index.html;
           try_files $uri $uri/ /index.html;
       }
       
       location /api/ {
           proxy_pass http://localhost:8570/;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
       }
   }
   ```

### 后端部署

1. **构建可执行 jar 包**
   ```bash
   mvn clean package -DskipTests
   ```

2. **部署到服务器**
   - 将 `freesia-web/freesia-web-admin/target/freesia-web-admin-1.5.0.jar` 上传到服务器
   - 创建启动脚本：

   ```bash
   #!/bin/bash
   java -jar freesia-web-admin-1.5.0.jar --spring.profiles.active=prod
   ```

3. **使用 systemd 管理服务**（可选）
   创建 systemd 服务文件：

   ```
   [Unit]
   Description=Freesia Backend Service
   After=network.target

   [Service]
   Type=simple
   ExecStart=/path/to/java -jar /path/to/freesia-web-admin-1.5.0.jar
   Restart=on-failure

   [Install]
   WantedBy=multi-user.target
   ```

## 技术亮点

1. **前后端分离架构**：Vue3 + TypeScript 前端与 SpringBoot 后端分离，提高开发效率

2. **细粒度权限控制**：基于 Sa-Token 实现的菜单权限、按钮权限、数据权限三级权限体系

3. **多租户设计**：支持个人、企业、组织等不同类型的租户，实现数据隔离

4. **丰富的记账功能**：完整的预算管理、支出记录、用户分配和数据分析功能

5. **国际化支持**：内置多语言支持，可轻松扩展其他语言

6. **系统监控**：集成 Redis 监控和 Gitee 代码仓库监控

7. **对象存储**：集成 MinIO 实现文件的安全存储和管理

8. **API文档**：使用 SpringDoc 自动生成接口文档，方便前后端对接

9. **SQL监控**：集成 P6spy 实现 SQL 执行监控，便于性能优化

10. **工具类集成**：集成 Hutool、FastJson2 等常用工具，提高开发效率

## 贡献指南

1. **Fork 本仓库**

2. **新建分支**
   ```bash
   git checkout -b Feat_xxx
   ```

3. **提交代码**
   ```bash
   git commit -m "Add: xxx feature"
   ```

4. **推送分支**
   ```bash
   git push origin Feat_xxx
   ```

5. **新建 Pull Request**

## 许可证

本项目采用 MIT 许可证，详见 `LICENSE` 文件。

## 联系方式

- **Gitee 地址**：[https://gitee.com/devilvan/freesia](https://gitee.com/devilvan/freesia)
- **前端技术文档**：[http://www.layui-vue.com/](http://www.layui-vue.com/)

---

**Freesia** - 为您提供高效、安全、易用的后台管理和记账系统解决方案！