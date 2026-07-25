# Smart-Rent 智能租房系统

一个基于 Spring Cloud 微服务架构的智能租房平台，包含**管理后台（Web）**、**用户/房东小程序（微信）**以及**AI 租房智能体**。系统覆盖房源发布与审核、预约看房、合同管理、消息通知、数据看板等核心租房业务，并通过 LangChain4j 集成大模型提供智能找房与问答能力。

## 功能特性

- **房源管理**：房东发布房源、管理员审核（待审核/已上架/已下架/已租出）、多条件检索
- **预约看房**：租客发起预约、房东/管理员跟进，状态全流程跟踪
- **合同管理**：租房合同创建、状态流转与查询
- **消息中心**：系统消息、消息类型/已读状态管理
- **数据看板**：用户/房源/预约 KPI、近 N 天增长趋势曲线、房源类型分布
- **AI 智能体**：基于 LangChain4j 的智能找房与对话（`agent-service`）
- **对象存储**：图片通过阿里云 OSS 上传（`house-service`）
- **统一鉴权**：Sa-Token 实现登录态与权限校验，网关统一路由

## 技术栈

| 层 | 技术 |
| --- | --- |
| 后端 | Java 17 · Spring Boot 4.0 · Spring Cloud 2025.1 · MyBatis-Plus 3.5.16 |
| AI | LangChain4j 1.0.0-beta1 |
| 安全 | Sa-Token 1.42.0 · Hutool |
| 管理前端 | Vue 3 · Vite · TypeScript · Element Plus 2.9 · Pinia · ECharts |
| 小程序 | uni-app（Vue 3），编译目标 `mp-weixin` |
| 存储/中间件 | MySQL · Redis（Sa-Token）· RabbitMQ |
| 网关 | Spring Cloud Gateway（当前 Nacos 已禁用，使用静态路由至 `localhost`） |

## 系统架构

```
                 ┌─────────────────────────────────────────┐
   小程序/管理端 ─┤            Gateway ( :8080 )             │
                 └───────────────┬───────────┬─────────────┘
                                 │           │
                  ┌──────────────▼──┐   ┌─────▼──────────┐
                  │ system-service  │   │ house-service   │
                  │   ( :8081 )     │   │   ( :8082 )     │
                  │ 用户/鉴权/看板  │   │ 房源/预约/OSS   │
                  └────────┬────────┘   └────────┬────────┘
                           │                     │
                  ┌────────▼────────┐   ┌────────▼────────┐
                  │ agent-service    │   │ MySQL / Redis   │
                  │   ( :8085 )      │   │ / RabbitMQ      │
                  │ AI 智能体        │   │                 │
                  └─────────────────┘   └─────────────────┘
```

### 模块说明

| 模块 | 端口 | 职责 |
| --- | --- | --- |
| `common-core` | — | 公共依赖：统一响应 `R`、基础实体 `BaseEntity`、共享 DTO（如 `HouseStatsVO`/`TrendVO`） |
| `gateway-service` | 8080 | 统一入口，`/api/**` 路由到各微服务（StripPrefix=1） |
| `system-service` | 8081 | 用户/角色、登录鉴权、看板聚合（Feign 调 house-service） |
| `house-service` | 8082 | 房源 CRUD/审核、预约管理、文件上传（OSS） |
| `agent-service` | 8085 | LangChain4j AI 智能体（智能找房/问答） |
| `admin-panel` | 5173 | 管理后台前端（Vue3 + Element Plus） |
| `rent-app` | — | 微信小程序前端（uni-app，仅编译 `mp-weixin`） |

> 网关路由前缀：`/api/user`、`/api/message`、`/api/admin` → system-service；`/api/house`、`/api/appointment`、`/api/file` → house-service；`/api/agent` → agent-service。

## 目录结构

```
smart-rent-agent/
├── common-core/          # 公共核心模块
├── gateway-service/      # 网关
├── system-service/       # 用户/鉴权/看板
├── house-service/        # 房源/预约/文件
├── agent-service/        # AI 智能体
├── admin-panel/          # 管理后台前端
├── rent-app/             # 微信小程序前端
├── sql/                  # 数据库脚本
│   ├── system_db.sql
│   ├── house_db.sql
│   ├── agent_db.sql
│   └── seed_mock_data.sql
└── docs/                 # 设计原型等文档
```

## 环境要求

- JDK 17+
- Node.js 20+（管理前端）/ 对应 uni-app 构建环境（小程序）
- MySQL 8.x
- Redis 7.x（Sa-Token 登录态必须）
- RabbitMQ（system-service 消息队列）

## 快速开始

### 1. 初始化数据库

```bash
# 在 MySQL 中依次执行（按业务库拆分）
mysql -u root -p < sql/system_db.sql
mysql -u root -p < sql/house_db.sql
mysql -u root -p < sql/agent_db.sql
mysql -u root -p < sql/seed_mock_data.sql   # 可选：示例数据
```

### 2. 启动中间件

```bash
# Redis（登录鉴权依赖）
redis-server

# RabbitMQ（按需）
rabbitmq-server
```

### 3. 启动后端（按需启动各模块）

```bash
# 需先配置各服务的数据库连接信息（见下方「配置说明」）
# 建议启动顺序：gateway → system → house → agent
cd gateway-service && mvn spring-boot:run
cd system-service  && mvn spring-boot:run
cd house-service    && mvn spring-boot:run
cd agent-service    && mvn spring-boot:run
```

### 4. 启动管理后台（admin-panel）

```bash
cd admin-panel
npm install
npm run dev        # 开发模式，默认 http://localhost:5173
# 生产构建
npm run build      # 产物在 dist/
```

### 5. 小程序（rent-app）

```bash
cd rent-app
npm install
# 仅编译微信小程序
npm run build:mp-weixin
# 用微信开发者工具导入 rent-app/dist/build/mp-weixin
```

## 默认账号

| 角色 | 账号 | 密码 |
| --- | --- | --- |
| 管理员（管理后台） | `admin` | `123456` |
| 微信用户 | 微信授权登录 | 默认密码 `wx_default_pwd` |

> 后台登录态基于 Sa-Token，依赖 Redis；未启动 Redis 时登录会失败（无法写入 token）。

## 配置说明

### 数据库 / Redis / RabbitMQ

各服务 `src/main/resources/application.yml` 中已配置本地默认地址（`127.0.0.1`），按实际环境修改用户名、密码、库名即可。

### OSS 图片上传（house-service）

`house-service` 已集成阿里云 OSS（`OssService` + `FileController`），配置通过环境变量注入，**密钥不要硬编码进仓库**：

```bash
# Windows（开发用，参考 house-service/run-local.bat）
set OSS_ACCESS_KEY_ID=你的AccessKeyId
set OSS_ACCESS_KEY_SECRET=你的AccessKeySecret
# 然后启动：mvn spring-boot:run
```

Bucket `smart-rent`、Endpoint `oss-cn-beijing.aliyuncs.com`，详见 `house-service/OSS-README.md`。

> ⚠️ OSS Bucket 需开启**公共读**，否则上传后返回的访问 URL 会 403。

## 接口示例

### 文件上传

```http
POST http://localhost:8080/api/file/upload
Content-Type: multipart/form-data

file: <图片文件>
dir:  house/images   # 可选，默认 house/images
```

返回：`{ "code": 200, "data": "https://smart-rent.oss-cn-beijing.aliyuncs.com/house/images/xxx.png" }`

### 数据看板

```http
GET http://localhost:8080/api/admin/dashboard          # KPI 汇总
GET http://localhost:8080/api/admin/trend/user?days=7  # 用户增长趋势
GET http://localhost:8080/api/admin/trend/house?days=7 # 房源增长趋势
```

## 注意事项

- **Nacos 当前已禁用**：网关使用写死的静态路由（`http://localhost:8082` 等），服务发现未启用。
- **密钥管理**：OSS 密钥、数据库密码等敏感信息通过环境变量注入；`house-service/run-local.bat`、`application-local.yml` 等已被 `.gitignore` 排除，请勿提交含真实密钥的文件。
- **小程序请求**：`rent-app` 正式环境需配置合法 HTTPS 域名并在微信公众平台登记 request 合法域名。

## 许可证

本项目为毕业设计用途，具体授权请联系作者。
