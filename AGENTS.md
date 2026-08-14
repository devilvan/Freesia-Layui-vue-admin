# Freesia 项目概览

## 分析问题语言使用
中文
## 项目简介

Freesia 是基于 SpringBoot + Vue3 的全栈后台管理系统，包含管理后台和移动端两个前端项目，采用模块化架构设计。

**核心功能**：权限管理、记账系统、系统管理、多租户、国际化、系统监控

---

## 项目结构

```
Freesia/
├── freesia-admin-layvue/       # 前端管理后台（Vue3 + Layui-vue）
├── freesia-admin-uniapp/       # 前端移动端（UniApp）
├── freesia-common/             # 后端公共模块（20个子模块）
├── freesia-extends/            # 后端扩展模块（Spring Admin、XXL-Job）
├── freesia-web/                # 后端Web业务模块（8个子模块）
├── sql/                        # 数据库脚本
├── logs/                       # 日志目录
└── pom.xml                     # Maven父工程
```

---

## 模块职责

### 前端管理后台 (freesia-admin-layvue)

| 目录 | 职责 |
|-----|------|
| `api/` | API接口定义（account、system、dashboard等） |
| `views/` | 页面组件（系统管理、记账、工作台、仪表盘） |
| `layouts/` | 布局组件（侧边栏、顶部导航、标签页） |
| `store/` | Pinia状态管理（用户、应用、加密、记账） |
| `types/` | TypeScript类型定义 |
| `directives/` | 自定义指令（权限验证、按钮权限） |

### 前端移动端 (freesia-admin-uniapp)

| 目录 | 职责 |
|-----|------|
| `pages/` | 页面组件（登录、账户、消息、个人中心） |
| `api/` | API接口定义（与后端共用接口） |
| `static/` | 静态资源（TabBar图标） |
| `store/` | 状态管理 |

### 后端公共模块 (freesia-common)

| 模块 | 职责 |
|-----|------|
| `freesia-common-api` | 统一响应封装(R.java)、基础VO |
| `freesia-common-satoken` | Sa-Token权限认证封装 |
| `freesia-common-tenant` | 多租户支持、数据隔离 |
| `freesia-common-redis` | Redis操作、分布式锁(Redisson) |
| `freesia-common-ratelimit` | 接口限流、流量控制 |
| `freesia-common-oss` | 对象存储(MinIO) |
| `freesia-common-log` | 日志记录、日志切面 |
| `freesia-common-excel` | Excel导入导出(EasyExcel) |
| `freesia-common-crypt` | 加密解密工具 |
| `freesia-common-validation` | 参数校验 |

### 后端业务模块 (freesia-web)

| 模块 | 职责 |
|-----|------|
| `freesia-web-admin` | 系统管理（用户、角色、部门、菜单等） |
| `freesia-web-account` | 记账业务（预算、支出、报表） |
| `freesia-web-worldclock` | 世界时钟功能 |
| `freesia-web-icon` | 图标管理 |
| `freesia-web-chat` | 聊天消息模块 |
| `freesia-web-api` | API统一入口 |
| `freesia-web-app` | 应用主模块（配置文件） |
| `freesia-web-framework` | Web框架基础 |

### 后端扩展模块 (freesia-extends)

| 模块 | 职责 |
|-----|------|
| `freesia-spring-admin` | Spring Boot Admin监控 |
| `freesia-xxl-job` | XXL-Job定时任务 |

---

## 技术栈

### 前端（管理后台）

| 技术 | 版本 |
|-----|------|
| Vue | 3.3.4 |
| TypeScript | 4.5.4 |
| Layui-vue | 2.23.3 |
| Pinia | 2.0.32 |
| Vite | 6.2.2 |
| Axios | 1.3.4 |
| ECharts | 5.4.1 |

### 前端（移动端）

| 技术 | 版本 |
|-----|------|
| UniApp | 3.0.0-5000720260410001 |
| Vue | 3.5.34 |
| Vite | 5.2.8 |
| Sass | 1.99.0 |

### 后端

| 技术 | 版本 |
|-----|------|
| JDK | 21 |
| SpringBoot | 3.5.0 |
| MyBatis-Plus | 3.5.3.1 |
| Sa-Token | 1.35.0.RC |
| SpringDoc | 1.8.0 |
| MySQL | 8.3.0 |
| Redis | 7.0.0 |
| MinIO | - |

---

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
pnpm run dev:h5        # H5开发
pnpm run dev:mp-weixin # 微信小程序
pnpm run build:h5
pnpm run build:mp-weixin
```

### 后端

```bash
# 启动依赖服务
redis-server
# MinIO服务

# 初始化数据库
mysql -u root -p < sql/freesia.sql

# 编译打包
mvn clean package -DskipTests

# 启动服务
java -jar freesia-web/freesia-web-admin/target/freesia-web-admin-1.5.0.jar
```

---

## 访问地址

| 服务 | 地址 |
|-----|------|
| 前端管理后台 | `http://localhost:5173` |
| 前端移动端H5 | `http://localhost:5174` |
| 后端API | `http://localhost:8570` |
| Swagger文档 | `http://localhost:8570/swagger-ui.html` |

---

## 核心设计

### 架构模式

- **分层架构**：Controller → Service → Mapper
- **模块化设计**：按业务功能划分独立模块
- **前后端分离**：RESTful API通信

### 权限体系

- **Sa-Token**：会话管理、权限验证
- **三级权限**：菜单权限、按钮权限、数据权限
- **多租户隔离**：基于租户ID的数据隔离

### 数据访问

- **MyBatis-Plus**：CRUD、分页、条件构造器
- **QueryDSL**：类型安全查询
- **P6Spy**：SQL监控

### 缓存策略

- **Redis**：缓存热点数据
- **Redisson**：分布式锁
- **Lock4j**：注解式锁

---

## 编码规范

### Java

- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- MapStruct进行对象转换
- 统一异常处理

### TypeScript

- 严格模式(strict: true)
- 接口优先设计
- 使用组合式API
- 统一响应类型

### 提交规范

```
Add: 新增功能
Update: 更新功能
Fix: 修复Bug
Refactor: 代码重构
Docs: 文档更新
```

---

## 配置管理

### 前端环境变量

- `freesia-admin-layvue/.env.development`
- `freesia-admin-layvue/.env.production`
- `freesia-admin-uniapp/.env.development`
- `freesia-admin-uniapp/.env.production`

### 后端环境变量

- Maven profiles：`dev`、`test`、`prod`
- 默认激活：`dev`

---

## 快速开发指南

### 新增业务模块

1. 在 `freesia-web/` 创建新模块
2. 添加数据库表和Mapper
3. 实现Service和Controller
4. 在前端添加API、类型和页面
5. 配置路由和菜单

### 新增页面

1. 在 `src/views/` 创建组件
2. 在 `src/router/` 配置路由
3. 在 `src/types/` 添加类型定义
4. 在后端添加接口

---

## 关键文件

| 文件 | 说明 |
|-----|------|
| `pom.xml` | Maven父工程配置 |
| `sql/freesia.sql` | 数据库初始化脚本 |
| `freesia-admin-layvue/package.json` | 前端依赖 |
| `freesia-admin-uniapp/package.json` | 移动端依赖 |
| `freesia-web/freesia-web-app/src/main/resources/application-dev.yml` | 后端配置 |

---

## 许可证

MIT License
