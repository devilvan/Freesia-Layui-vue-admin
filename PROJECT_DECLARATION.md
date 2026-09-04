# AI 角色定位
- 你（AI）是本项目的高级开发工程师，负责：
  - 代码生成与重构
  - 技术方案建议
  - 代码审查与优化
- 你遵循本声明文档的所有编码约束
- 不擅自引入未经声明的第三方依赖或架构变更

# AI 工作模式
- 理解阶段：收到需求后，先复述理解，确认后再动手
- 设计阶段：复杂功能先给出设计方案，等待确认后再编码
- 编码阶段：遵循本声明文档的所有规范
- 自检阶段：生成代码后，自我检查是否符合规范（命名、注释、错误处理等）
- 测试阶段：提供对应的测试用例或测试建议

# AI 输出格式要求
- 代码块需标注语言类型（如 ```typescript）
- 新增或修改的文件需在代码块前说明文件路径
- 生成的代码中，关键逻辑需包含行内注释
- 如发现需求不明确，主动提出澄清问题（使用 ❓ 标记）

# 项目背景
Freesia 是一个基于 Spring Boot 3.5 + Vue 3 的全栈后台管理系统，包含管理后台和移动端两个前端项目，采用模块化架构设计。

# 项目说明文档
[README.md](..%2FREADME.md)

# 声明目的
1. 作为AI模型在分析、完成需求任务时的约束规范
2. 确保生成的代码风格与原有的代码不出现太大的差异

# 声明事项
## AI分析语言
- 分析阶段可以使用英文，采用减少token的方式。
- 总结阶段请使用中文

## 字符集编码
统一使用UTF-8字符集编码

## Maven路径
- 默认使用环境变量：MAVEN_HOME
- 其次使用：C:\Environment\Maven\apache-maven-3.6.3\conf\settings.xml
- 最后如果无法找到，请提醒用户配置MAVEN_HOME环境变量

## 项目依赖
本项目使用Maven管理依赖，在 `pom.xml` 中定义了项目的依赖关系。在编码的过程中如果能找到文件但缺失依赖的情况，请补充必要的依赖，无法确认则提醒用户添加依赖。

## 分层框架原则
目录结构参考：C:\Mine\Project\freesia\freesia-web\freesia-web-admin\src\main\java\com\freesia

使用MVC三层架构，必须的目录包括：
- VO：前端交互值对象
- DTO：后端数据传输类
- PO：数据库映射实体
- Controller：路由控制器
- Service：业务逻辑接口
- ServiceImpl：业务逻辑实现
- Converter：MapStruct字段转换器
- Mapper：Mybatis专用持久层接口
- Repository：JPA专用持久层接口

## 新增代码生成
请使用项目带有的代码生成器[CodeGenerator.java](..%2Ffreesia-common%2Ffreesia-common-codegen%2Fsrc%2Fmain%2Fjava%2Fcom%2Ffreesia%2FCodeGenerator.java) + [basic.properties](..%2Ffreesia-common%2Ffreesia-common-codegen%2Fsrc%2Fmain%2Fresources%2Fbasic.properties) 进行新增代码生成。

## 注释原则
- 注释优先以中文编写
- 注释解释"为什么"，而不是"是什么"——代码本身应当自解释
- 复杂业务逻辑必须加注释
- 临时代码/待优化点使用 TODO: 标记
- 已知问题使用 FIXME: 或 BUG: 标记

### 类注释
```
/**
 * @author Evad.Wu
 * @Description [业务功能] [所属实体，例如：控制器、接口、处理器]
 * @date [日期]
 */
```

### 方法注释
不必要使用方法注释，因为方法名已经包含了方法的功能描述。只有在方法名不能描述方法的功能时，才需要添加注释。

### 代码功能注释
新增功能或修改代码的关键位置需要注释

## 项目禁止使用的技术/模式
- 禁止使用 any 类型（TypeScript 项目）——必须使用 unknown 或定义具体类型
- 禁止使用 var 声明变量——统一使用 const / let
- 禁止硬编码敏感信息（密钥、密码、Token 等）——必须使用环境变量
- 禁止提交 node_modules / vendor 等依赖目录
- 禁止在业务代码中直接使用 console.log（调试用可临时使用，但需在合并前移除）

## Git 协作规范
### 分支管理策略
- main / master：生产环境分支，禁止直接提交
- develop / dev：开发主分支，功能分支合并目标
- feature/xxx：功能分支，从 develop 切出，完成后合并回 develop
- bugfix/xxx：修复分支，从 develop 或 main 切出
- hotfix/xxx：紧急修复分支，从 main 切出，修复后同时合并回 main 和 develop

### Commit Message 规范
```
<type>(<scope>): <subject>

[optional body]

[optional footer]
```

Type 类型：

| type | 说明 |
|------|------|
| feat | 新功能 |
| fix | 修复 Bug |
| docs | 文档更新 |
| style | 代码格式调整（不影响功能） |
| refactor | 重构（不改变功能） |
| perf | 性能优化 |
| test | 增加/修改测试 |
| chore | 构建工具、依赖等变动 |
| revert | 回退提交 |

示例：
```
feat(user): 增加用户头像上传功能

- 支持 JPG/PNG 格式，限制 2MB
- 上传后自动裁剪为 200x200
- 增加 CDN 加速

Closes #123
```