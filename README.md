# Freesia 项目说明

Freesia 是一个基于 Spring Boot 3.5 + Vue 3 的全栈后台管理系统，包含管理后台和移动端两个前端项目，采用模块化架构设计。

项目覆盖权限管理、记账系统、系统管理、多租户、国际化、系统监控等常见后台能力，并补充了邮箱注册、邮箱找回密码、邮箱登录、二维码登录和 OAuth 单点登录等认证场景。

## 项目特点

- 前后端分离，接口统一通过 RESTful API 提供
- 权限体系基于 Sa-Token，支持菜单权限、按钮权限和数据权限
- 支持多租户隔离
- 支持邮箱注册、邮箱找回密码、邮箱登录
- 登录页支持账号登录、二维码登录和第三方 OAuth 登录
- 注册时自动分配默认昵称和随机头像
- JSON 序列化统一使用 Jackson，已移除 Fastjson
- 适配桌面端和移动端

## 技术栈

### 前端管理后台

| 技术 | 版本 |
|------|------|
| Vue | 3.3.4 |
| TypeScript | 4.5.4 |
| Layui-vue | 2.23.3 |
| Pinia | 2.0.32 |
| Vite | 6.2.2 |
| Axios | 1.3.4 |
| ECharts | 5.4.1 |

### 前端移动端

| 技术 | 版本 |
|------|------|
| Vue | 3.5.34 |
| UniApp | 3.0.0-5000720260410001 |
| Vite | 5.2.8 |
| Sass | 1.99.0 |

### 后端

| 技术 | 版本 |
|------|------|
| JDK | 21 |
| Spring Boot | 3.5.0 |
| MyBatis-Plus | 3.5.7 |
| Sa-Token | 1.40.0 |
| SpringDoc | 2.8.9 |
| Hutool | 5.8.27 |
| MySQL | 8.3.0 |
| Redis | 7.0.0 |
| MinIO | 对象存储 |

## 项目结构

```text
Freesia/
├── freesia-admin-layvue/   # 前端管理后台
├── freesia-admin-uniapp/   # 前端移动端
├── freesia-common/         # 后端公共模块
├── freesia-extends/        # 后端扩展模块
├── freesia-web/            # 后端业务模块
├── sql/                    # 数据库脚本
├── logs/                   # 日志目录
├── pom.xml                 # Maven 父工程
└── README.md               # 项目说明
```

### 后端模块

- `freesia-common-api`：统一响应、基础 VO 和公共常量
- `freesia-common-framework`：Web 框架基础能力
- `freesia-common-crypt`：RSA / AES 加密解密
- `freesia-common-desensitization`：敏感数据脱敏
- `freesia-common-oss`：对象存储封装
- `freesia-common-redis`：Redis 操作和分布式能力
- `freesia-common-satoken`：Sa-Token 权限封装
- `freesia-common-tenant`：多租户支持
- `freesia-common-validation`：参数校验
- `freesia-web-admin`：系统管理、登录注册、用户管理
- `freesia-web-account`：记账业务
- `freesia-web-chat`：聊天消息
- `freesia-web-icon`：图标管理
- `freesia-web-worldclock`：世界时钟
- `freesia-web-framework`：Web 配置和公共基础设施
- `freesia-spring-admin`：Spring Boot Admin
- `freesia-xxl-job`：XXL-Job

## 认证与账户

当前登录与账户相关能力包括：

- 账号登录
- 邮箱登录
- 二维码登录
- OAuth 第三方登录
- 邮箱注册
- 邮箱找回密码

注册与找回密码流程使用邮箱验证码，并通过前端加密后发送到后端，后端 Controller 解密后进入业务处理。

## 核心功能

### 系统管理

- 用户管理
- 角色管理
- 部门管理
- 菜单管理
- 字典管理
- 参数配置
- 日志管理
- 租户管理
- 文件管理

### 记账系统

- 预算管理
- 支出记录
- 支出统计
- 用户分配
- 数据分析

### 公共能力

- 图标管理
- 国际化
- URL 配置
- 待办事项

### 系统监控

- Redis 监控
- Spring Boot Admin
- XXL-Job
- Gitee 相关数据展示

### 移动端

- 登录与个人中心
- 账户与记账相关页面
- 消息中心

## 构建与运行

### 前端管理后台

```bash
cd freesia-admin-layvue
pnpm install
pnpm run dev
pnpm run build:prod
```

### 前端移动端

```bash
cd freesia-admin-uniapp
pnpm install
pnpm run dev:h5
pnpm run dev:mp-weixin
pnpm run build:h5
pnpm run build:mp-weixin
```

### 后端

```bash
# 启动依赖
redis-server
# 启动 MinIO

# 初始化数据库
mysql -u root -p < sql/freesia.sql

# 编译打包
mvn clean package -DskipTests

# 启动后端
java -jar freesia-web/freesia-web-admin/target/freesia-web-admin-1.5.0.jar
```

## 访问地址

| 服务 | 地址 |
|------|------|
| 前端管理后台 | `http://localhost:5173` |
| 前端移动端 H5 | `http://localhost:5174` |
| 后端 API | `http://localhost:8570` |
| Swagger 文档 | `http://localhost:8570/swagger-ui.html` |

## 配置说明

### 前端环境变量

- `freesia-admin-layvue/.env.development`
- `freesia-admin-layvue/.env.production`
- `freesia-admin-uniapp/.env.development`
- `freesia-admin-uniapp/.env.production`

### 后端配置

后端通过 Maven profiles 管理环境，支持：

- `dev`
- `test`
- `prod`

默认激活的是 `dev`。

## 备注

- 当前后端 JSON 处理统一采用 Jackson
- 项目采用 MIT License

