# 中瀚安全云平台（Zh_AqY）

中瀚安全云平台是面向项目现场、设备接入、实时数据、阈值告警、短信通知和运维管理的 Web 管理系统。项目采用前后端分离架构：后端提供接口、权限、业务逻辑、MQTT 数据接入和告警处理，前端提供浏览器管理页面和数据展示。

> 当前项目是 B/S Web 管理平台，不是手机 App，也不是桌面客户端。用户通过浏览器访问系统。

> 当前推荐标记：`v2026.8.7-empty-data-stable-docker`。这是“空数据稳定版 / 通用 Docker 安装包”，重点修复空项目、空设备、空图表场景下首页和大屏稳定性，并默认走 Docker-first 跨平台安装。

## 核心能力

- 项目、标段、设备、网关、摄像头等基础资料管理。
- MQTT 设备数据接入、解析、入库和状态更新。
- 实时数据、历史数据、原始数据和数据看板展示。
- 按项目、设备和告警等级生成告警记录。
- 按“项目 + 告警等级”配置短信接收人。
- 告警触发后通过阿里云短信发送通知，并支持短信冷却时间，避免连续刷屏。
- 用户、角色、菜单、部门、岗位、字典、参数、日志等后台管理能力。
- Docker-first 跨 Windows、Ubuntu、macOS 部署。

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 后端 | Java 17、Spring Boot 2.7.18、Spring Framework 5.3.x、Spring Security 5.8.x、MyBatis、Druid |
| API 文档 | springdoc-openapi 1.7.x |
| Token | JJWT 0.11.x |
| 前端 | Vue 2.7.x、Vue CLI 5.x、Webpack 5、Element UI 2.15.x、Vue Router 3.6.x、Vuex 3.6.x、Axios |
| 数据库 | MySQL 5.7+ / 8.x |
| 缓存 | Redis |
| 设备接入 | MQTT |
| 短信 | 阿里云短信服务 |
| 推荐部署 | Docker Compose |

说明：

- 本次没有直接升级到 Spring Boot 3 / Vue 3，因为那会涉及 `javax` 到 `jakarta`、Spring Security 6、Element Plus、路由和页面组件的大范围重构。
- 当前版本选择 Java 17 + Spring Boot 2.7 + Vue 2.7 + Vue CLI 5，是兼顾兼容性和不过度落后的稳定路线。

## 目录说明

```text
.
├── aqy-admin       # 后端启动入口，打包后生成 aqy-admin.jar
├── aqy-common      # 通用工具、基础实体、公共返回结构
├── aqy-framework   # 安全认证、Web 配置、短信服务等框架能力
├── aqy-main        # 安全云核心业务：项目、设备、数据、告警等
├── aqy-mqtt        # MQTT 接入、主题订阅、设备消息处理
├── aqy-quartz      # 定时任务
├── aqy-system      # 用户、角色、菜单、部门、岗位等系统管理
├── aqy-generator   # 代码生成相关能力（默认不在客户菜单展示）
├── aqy-ui          # Vue 前端项目
├── deploy/docker   # Docker 镜像、Nginx 和初始化脚本
├── sql             # 初始化 SQL 和迁移 SQL
├── bin             # Windows / Ubuntu / macOS / Docker 辅助脚本
└── CODE_ANALYSIS.md
```

## 推荐部署方式：Docker

所有新客户、测试、演示环境都建议用 Docker。Docker 会统一运行：

- MySQL
- Redis
- 后端 Java 服务
- 前端 Nginx

服务重启策略已经配置为自动重启；服务器重启后，只要 Docker Desktop / Docker Engine 正常启动，容器会自动拉起。

注意：`RUN-DOCKER-FRESH-TEST.*` 是全新测试/演示环境入口。它会先删除本项目 Docker Compose 的旧容器和旧数据卷，再重新初始化 MySQL。不要在已有生产数据的环境上运行 fresh-test；老库请使用迁移入口。

Docker 演示环境默认关闭 MQTT 消费端订阅，避免未接入真实 MQTT broker 时持续重连刷日志。生产环境接入设备上报时，请配置 `MQTT_HOST` 并设置 `MQTT_CONSUMER_ENABLED=true`。

Docker 演示和生产默认关闭 Swagger/OpenAPI 与 Druid 监控页面，避免接口文档和连接池监控匿名暴露。确需排障时再临时打开，并配置强密码和访问白名单。

### 新环境 / 演示环境

下载 Release 包并解压后，在包目录运行：

> 空数据稳定版推荐用下面的 Docker fresh-test 入口安装。它会创建一套全新的 MySQL / Redis / 后端 / 前端容器，适合新机器、演示环境、客户首次部署验证。

| 平台 | 双击 / 执行入口 |
| --- | --- |
| Windows | `bin/RUN-DOCKER-FRESH-TEST.bat` |
| Ubuntu | `./bin/RUN-DOCKER-FRESH-TEST.sh` |
| macOS | 双击 `bin/RUN-DOCKER-FRESH-TEST.command` |

成功后访问：

- 前端：`http://127.0.0.1:8080`
- 后端健康检查：`http://127.0.0.1:7070/prod-api/captchaImage`

默认初始化账号：

- 用户名：`admin`
- 密码：`ChangeMe@123456`

首次登录后必须修改管理员密码，并检查数据库、Redis、短信、上传目录等生产配置。

### 老数据库迁移到 Docker

如果老环境已经有 MySQL 数据，不要走 fresh-test。使用迁移脚本：

| 平台 | 入口 |
| --- | --- |
| Windows | `bin/RUN-DOCKER-MIGRATE-EXISTING-DB.bat` |
| Ubuntu | `./bin/RUN-DOCKER-MIGRATE-EXISTING-DB.sh` |
| macOS | 双击 `bin/RUN-DOCKER-MIGRATE-EXISTING-DB.command` |

迁移脚本会做这些事：

1. 使用 Docker MySQL client 从老库导出 dump。
2. 重建 Docker MySQL 数据卷。
3. 把 dump 导入 Docker MySQL。
4. 执行 `sql/migrations/*.sql`。
5. 启动 MySQL、Redis、后端、前端。
6. 检查后端是否可访问。

本仓库已经提供菜单清理迁移脚本，会删除客户侧不需要的默认监控/工具/代码生成/系统接口菜单，并补齐安全云业务菜单。

### Docker 日常运维脚本

| 动作 | Windows | Ubuntu | macOS |
| --- | --- | --- | --- |
| 查看状态 | `bin/RUN-DOCKER-STATUS.bat` | `./bin/RUN-DOCKER-STATUS.sh` | `bin/RUN-DOCKER-STATUS.command` |
| 停止服务 | `bin/RUN-DOCKER-STOP.bat` | `./bin/RUN-DOCKER-STOP.sh` | `bin/RUN-DOCKER-STOP.command` |
| 备份数据库 | `bin/RUN-DOCKER-BACKUP-DB.bat` | `./bin/RUN-DOCKER-BACKUP-DB.sh` | `bin/RUN-DOCKER-BACKUP-DB.command` |
| 恢复数据库 | `bin/RUN-DOCKER-RESTORE-DB.bat` | `./bin/RUN-DOCKER-RESTORE-DB.sh` | `bin/RUN-DOCKER-RESTORE-DB.command` |

备份文件默认放在 `backups/`，该目录不会提交到 Git。

## 原生部署（备用）

如果客户暂时不能安装 Docker，仍可使用原生部署：

- 后端：`java -jar aqy-admin.jar`
- 前端：把 `aqy-ui/dist` 放到 Nginx / IIS / Apache。
- 数据库和 Redis：使用客户已有服务。

原生部署需要自行安装：

- Java 17
- MySQL
- Redis
- Nginx 或 IIS

Release 包里仍保留原生脚本：

| 平台 | 环境检查 | 安装依赖 | 新环境测试 | 老环境升级 |
| --- | --- | --- | --- | --- |
| Windows | `bin/RUN-CHECK-WINDOWS-ENV.bat` | `bin/RUN-INSTALL-WINDOWS-PREREQS.bat` | `bin/RUN-FRESH-WINDOWS-TEST.bat` | `bin/RUN-UPGRADE-EXISTING-WINDOWS.bat` |
| Ubuntu | `bin/RUN-UBUNTU-CHECK-ENV.sh` | `bin/RUN-UBUNTU-INSTALL-PREREQS.sh` | `bin/RUN-UBUNTU-FRESH-TEST.sh` | `bin/RUN-UBUNTU-UPGRADE-EXISTING.sh` |
| macOS | `bin/RUN-MACOS-CHECK-ENV.command` | `bin/RUN-MACOS-INSTALL-PREREQS.command` | `bin/RUN-MACOS-FRESH-TEST.command` | `bin/RUN-MACOS-UPGRADE-EXISTING.command` |

## 关键环境变量

生产环境不要把真实密码、AccessKey 写死到代码或提交到 Git。主要配置可以通过环境变量覆盖：

| 环境变量 | 用途 |
| --- | --- |
| `DB_URL` | MySQL JDBC 地址 |
| `DB_USERNAME` | MySQL 用户名 |
| `DB_PASSWORD` | MySQL 密码 |
| `SWAGGER_ENABLED` | 是否开放 OpenAPI/Swagger 页面，默认 `false` |
| `DRUID_STAT_ENABLED` | 是否开放 Druid 监控页面，默认 `false` |
| `DRUID_WEB_STAT_ENABLED` | 是否启用 Druid Web 统计过滤器，默认 `false` |
| `DRUID_ALLOW` | Druid 监控页面 IP 白名单；开启 Druid 时建议配置 |
| `DRUID_LOGIN_USERNAME` | Druid 控制台用户名 |
| `DRUID_LOGIN_PASSWORD` | Druid 控制台密码；开启 Druid 时必须配置 |
| `TOKEN_SECRET` | JWT Token 密钥 |
| `SERVER_PORT` | 后端端口，默认 `7070` |
| `SERVER_CONTEXT_PATH` | 后端上下文路径，默认 `/prod-api` |
| `RUOYI_PROFILE` | 上传文件根目录 |
| `REDIS_HOST` | Redis 地址 |
| `REDIS_PORT` | Redis 端口 |
| `REDIS_DATABASE` | Redis 数据库索引 |
| `REDIS_PASSWORD` | Redis 密码，没有密码时留空 |
| `MQTT_HOST` | MQTT 地址 |
| `MQTT_CONSUMER_ENABLED` | 是否启动 MQTT 消费端订阅；Docker 演示默认 `false`，生产接入设备时设为 `true` |
| `MQTT_USERNAME` | MQTT 用户名 |
| `MQTT_PASSWORD` | MQTT 密码 |
| `ALIYUN_SMS_ACCESS_KEY_ID` | 阿里云短信 AccessKey ID |
| `ALIYUN_SMS_ACCESS_KEY_SECRET` | 阿里云短信 AccessKey Secret |
| `ALIYUN_SMS_SIGN_NAME` | 阿里云短信签名 |
| `ALIYUN_SMS_TEMPLATE_CODE` | 阿里云短信模板编码 |
| `ALIYUN_SMS_COOLDOWN_SECONDS` | 短信冷却秒数，默认 `600` |
| `SDRK_API_URL` | SDRK 外部平台 API 地址，默认 `http://www.0531yun.com/` |
| `SDRK_LOGIN_NAME` | SDRK 外部平台登录名；未配置时调用 SDRK 接口会明确报错 |
| `SDRK_PASSWORD` | SDRK 外部平台密码；未配置时调用 SDRK 接口会明确报错 |
| `TGY_API_URL` | 通感云外部平台 API 地址，默认 `http://api.cloud.tongganyun.com/` |
| `TGY_USERNAME` | 通感云外部平台用户名；未配置时调用通感云接口会明确报错 |
| `TGY_PASSWORD` | 通感云外部平台密码；未配置时调用通感云接口会明确报错 |
| `TGY_GRANT_TYPE` | 通感云 OAuth grant type，默认 `password` |
| `TGY_SCOPE` | 通感云 OAuth scope；未配置时调用通感云接口会明确报错 |
| `FILE_DOMAIN` | 文件访问域名 |
| `FILE_UPLOAD_PATH` | 文件上传目录 |
| `FILE_PREFIX` | 文件 URL 前缀 |

### 前端构建期环境变量

以下变量由 Vue 构建时写入前端产物。Release 包默认留空，因此不会加载高德地图/天气，也不会显示“下载 APP”按钮；如客户需要这些能力，请在 `aqy-ui/.env.production` 或构建环境中配置后重新构建前端。

| 变量 | 用途 |
| --- | --- |
| `VUE_APP_AMAP_JS_KEY` | 高德地图 JS API Key，留空时首页不加载地图 |
| `VUE_APP_AMAP_WEATHER_KEY` | 高德天气 Web 服务 Key，留空时不请求天气 |
| `VUE_APP_WEBSOCKET_URL` | 大屏 WebSocket 地址，留空时自动使用当前域名的 `/prod-api/websocket/message` |
| `VUE_APP_APP_DOWNLOAD_URL` | App 下载地址，留空时隐藏顶部“下载 APP”按钮 |

## 阿里云短信配置

启用短信告警前，需要在阿里云完成：

1. 完成账号实名认证。
2. 开通短信服务。
3. 申请短信签名。
4. 申请短信模板。
5. 创建 RAM 用户并授予短信发送所需权限。
6. 配置 `ALIYUN_SMS_*` 环境变量。
7. 在系统里按“项目 + 告警等级”配置短信接收人。

建议短信模板包含项目、设备、告警等级、告警内容和发生时间。生产环境请配置 `ALIYUN_SMS_COOLDOWN_SECONDS`，避免持续超阈值时重复给同一手机号发送大量短信。

## 本地开发构建

### 后端

需要 Java 17 和 Maven 3.6+：

```bash
mvn clean package -DskipTests
```

构建产物：

```text
aqy-admin/target/aqy-admin.jar
```

### 前端

需要 Node.js 22.x 和 npm 10.x：

```bash
cd aqy-ui
npm ci --legacy-peer-deps --no-audit --no-fund
CI=false NODE_OPTIONS=--max-old-space-size=4096 npm run build:prod
```

构建产物：

```text
aqy-ui/dist
```

## GitHub Actions 与 Release

推送到 GitHub 后，工作流会：

1. 使用 Java 17 编译后端。
2. 使用 Node.js 22 和 `npm ci` 构建前端。
3. 校验 Docker Compose fresh/migrate 配置。
4. 组装 Docker-first 跨平台 Release 包。
5. 校验包体中后端 jar、前端 `index.html`、Docker/Nginx/SQL/脚本等关键文件存在。
6. 包含 Windows、Ubuntu、macOS 的 Docker 和原生辅助脚本。

发布新版本后，建议按下面顺序回归：

1. 下载 Release 包到干净目录。
2. 用 `RUN-DOCKER-FRESH-TEST.*` 做全新 Docker 部署。
3. 登录首页，确认菜单只包含安全云业务菜单和必要系统管理菜单。
4. 检查 `/prod-api/captchaImage`、登录、项目、设备、告警、短信接收人页面。
5. 如有老库，使用 `RUN-DOCKER-MIGRATE-EXISTING-DB.*` 做迁移演练。

## 数据初始化说明

- `sql/ry_20240629.sql`：系统基础表、用户角色权限、必要菜单、字典、参数、公告等。
- `sql/quartz.sql`：定时任务表。
- `sql/zh_aqy_schema.sql`：安全云业务表结构，只使用 `CREATE TABLE IF NOT EXISTS`，不会清空已有业务数据。
- `sql/migrations/*.sql`：老库迁移后的增量修正脚本。

新环境 fresh-test 会执行初始化 SQL。老库升级不要重复执行初始化 SQL，应使用迁移脚本。

### 为什么第二阶段建议引入 Flyway

Flyway 是数据库版本迁移工具。它把每次数据库结构或基础数据变更写成有版本号的 SQL，例如 `V1__init.sql`、`V2__add_alarm_index.sql`。部署时 Flyway 会检查哪些版本已经在当前数据库执行过，只执行缺失的迁移，并把执行记录写入数据库历史表。

当前第一阶段仍保留 `sql/migrations/*.sql` 脚本模式，适合先把 Docker-first 部署跑稳。第二阶段引入 Flyway 的价值是让老客户数据库升级变得可追踪、可重复、可验证，避免手工 SQL 漏执行、重复执行或不同环境执行顺序不一致。
