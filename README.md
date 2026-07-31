# 中瀚安全云平台（Zh_AqY）

中瀚安全云平台是一个面向项目现场、设备接入、实时数据、阈值告警和运维管理的 Web 管理系统。项目基于 Spring Boot + Vue 前后端分离架构开发，后端负责接口、权限、业务逻辑、MQTT 数据接入和告警处理，前端负责浏览器页面展示和管理操作。

> 这不是手机 App，也不是桌面客户端。当前代码形态是“前后端分离的网页系统 / B/S 管理平台”。如果部署在一台服务器上，用户通过浏览器访问；如果进一步做多租户、计费、隔离和统一运营，才更接近严格意义上的 SaaS。

## 核心能力

- 项目、设备、监测点、传感器等基础资料管理。
- 通过 MQTT 接收设备上报的原始数据。
- 对不同设备类型的数据进行解析、入库和状态更新。
- 根据业务阈值生成告警记录并维护设备告警状态。
- 支持按“项目 + 告警级别”配置短信接收人。
- 告警产生后可通过阿里云短信发送通知。
- 提供用户、角色、菜单、权限、字典、参数、日志、任务调度等后台管理能力。
- 支持文件上传目录配置和前端大屏/业务页面展示。

## 系统形态

```text
浏览器
  |
  | 访问静态页面、调用 /prod-api/*
  v
Nginx / IIS / 其他静态 Web 服务
  |
  | 反向代理 API
  v
Spring Boot 后端服务（aqy-admin.jar，内置 Tomcat）
  |
  +--> MySQL：业务数据、用户、权限、告警记录
  +--> Redis：登录、缓存、验证码等运行状态
  +--> MQTT Broker：设备数据接入
  +--> 阿里云短信：告警短信发送
```

部署时不需要把后端放进外部 Tomcat。`aqy-admin.jar` 是 Spring Boot 可执行 Jar，已经内置 Web 容器，使用 `java -jar aqy-admin.jar` 启动即可。前端 `aqy-ui/dist` 是静态文件，建议放到 Nginx 或 IIS 中。

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 后端 | Java 8、Spring Boot 2.5.x、Spring Security、MyBatis、Druid |
| 前端 | Vue 2.6.12、Vue CLI 4.4.x、Element UI 2.15.x、Axios、Vue Router、Vuex、ECharts |
| 数据库 | MySQL |
| 缓存 | Redis |
| 设备接入 | MQTT |
| 短信 | 阿里云短信服务 |
| 部署 | Windows / Linux 均可，推荐后端 Jar + 前端 Nginx 静态站点 |

前端不是 React，也不是移动端 App；它是 Vue 2 单页管理后台。开发目录是 `aqy-ui`，生产构建命令是 `npm run build:prod`，构建结果在 `aqy-ui/dist`。

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
├── aqy-generator   # 代码生成相关能力
├── aqy-ui          # Vue 前端项目
├── sql             # 初始化 SQL，新环境使用；老环境升级不要重复执行
├── bin             # 辅助脚本
└── CODE_ANALYSIS.md # 代码结构和告警链路分析文档
```

## 运行依赖

生产或测试环境至少需要：

1. JDK 8。
2. MySQL 5.7+ 或 8.x。
3. Redis。
4. 可访问的 MQTT Broker。
5. Nginx / IIS / Apache 等静态 Web 服务，用于部署前端页面并反向代理接口。
6. 如果需要短信告警，准备阿里云短信 AccessKey、签名和模板。

本地构建还需要：

1. Maven 3.6+。
2. Node.js，建议使用 16.x LTS；老 Vue 2 项目不建议直接使用过新的 Node 版本。
3. npm。

## 关键配置

后端配置在：

- `aqy-admin/src/main/resources/application.yml`
- `aqy-admin/src/main/resources/application-druid.yml`

生产环境不要把真实密码、AccessKey 写死进代码。当前配置支持通过环境变量覆盖：

| 环境变量 | 用途 |
| --- | --- |
| `DB_URL` | MySQL JDBC 地址 |
| `DB_USERNAME` | MySQL 用户名 |
| `DB_PASSWORD` | MySQL 密码 |
| `DRUID_LOGIN_USERNAME` | Druid 控制台用户名 |
| `DRUID_LOGIN_PASSWORD` | Druid 控制台密码 |
| `TOKEN_SECRET` | JWT Token 密钥 |
| `SERVER_PORT` | 后端 HTTP 端口，默认 `7070` |
| `SERVER_CONTEXT_PATH` | 后端上下文路径，默认 `/prod-api` |
| `RUOYI_PROFILE` | 平台上传文件根目录，Windows 推荐 `D:/aqy/uploadPath` |
| `REDIS_HOST` | Redis 地址 |
| `REDIS_PORT` | Redis 端口 |
| `REDIS_DATABASE` | Redis 数据库索引 |
| `REDIS_PASSWORD` | Redis 密码，没有密码时留空 |
| `MQTT_HOST` | MQTT 地址 |
| `MQTT_USERNAME` | MQTT 用户名 |
| `MQTT_PASSWORD` | MQTT 密码 |
| `ALIYUN_SMS_ACCESS_KEY_ID` | 阿里云短信 AccessKey ID |
| `ALIYUN_SMS_ACCESS_KEY_SECRET` | 阿里云短信 AccessKey Secret |
| `ALIYUN_SMS_SIGN_NAME` | 阿里云短信签名 |
| `ALIYUN_SMS_TEMPLATE_CODE` | 阿里云短信模板编码 |
| `ALIYUN_SMS_COOLDOWN_SECONDS` | 同项目、同设备、同等级、同手机号短信冷却秒数，默认 `600` |
| `FILE_DOMAIN` | 文件访问域名 |
| `FILE_UPLOAD_PATH` | 文件上传目录 |
| `FILE_PREFIX` | 文件 URL 前缀 |

Windows 上可以通过“系统属性 -> 高级 -> 环境变量”设置，也可以在服务管理工具或启动脚本里设置。不要把这些值提交到 Git。

## Windows 服务器部署总览

### 先回答几个关键问题

1. **能不能直接 `git clone` 当前代码？**
   - 可以，但只建议把代码 clone 到“源码目录”，例如 `D:\source\zh_aqy`。
   - 不要把源码目录当成最终运行目录。
   - 不要 clone 到 Nginx/IIS 的网站根目录。
   - 不要 clone 到 MySQL 数据目录。

2. **真正部署上线的是哪些文件？**
   - 后端部署的是 `aqy-admin/target/aqy-admin.jar`。
   - 前端部署的是 `aqy-ui/dist` 里面的静态文件。
   - Git 仓库源码不是直接给浏览器访问的产物，必须先构建。

3. **建议放到什么文件夹？**

   ```text
   D:\
   ├── source
   │   └── zh_aqy          # Git clone 下来的源码目录，只用于拉代码和构建
   └── aqy
       ├── server          # 运行目录：只放 aqy-admin.jar
       ├── web             # 运行目录：只放前端 dist 构建结果
       ├── uploadPath      # 上传文件目录，升级时不能删
       └── backups         # 每次升级自动备份旧 Jar 和旧前端文件
   ```

4. **部署的时候是直接放置中间件吗？**
   - 后端不需要外置 Tomcat，`aqy-admin.jar` 内置 Tomcat，直接用 Java 启动。
   - 前端需要一个静态 Web 服务，推荐 Nginx，也可以用 IIS。
   - Nginx/IIS 负责访问前端页面，并把 `/prod-api/*` 反向代理到后端 `7070` 端口。

5. **老服务器已有数据库怎么办？**
   - 保留老 MySQL 数据库。
   - 不执行 `sql/ry_20240629.sql`。
   - 不执行 `sql/quartz.sql`。
   - `DB_URL` 必须指向老数据库。
   - 升级前用 `mysqldump` 备份一次。

### 第一次在 Windows 服务器上拉代码

先安装这些软件，并确认命令可用：

```powershell
git --version
java -version
mvn -version
node -v
npm -v
```

推荐版本：

- JDK 8。
- Maven 3.6+。
- Node.js 16.x LTS。
- Git for Windows。
- MySQL、Redis、Nginx 或 IIS。

创建目录：

```powershell
New-Item -ItemType Directory -Force D:\source
New-Item -ItemType Directory -Force D:\aqy\server
New-Item -ItemType Directory -Force D:\aqy\web
New-Item -ItemType Directory -Force D:\aqy\uploadPath
New-Item -ItemType Directory -Force D:\aqy\backups
```

使用 SSH 拉代码：

```powershell
git clone git@github.com:zongyangbigpolo/zh_aqy.git D:\source\zh_aqy
```

如果服务器没有配置 GitHub SSH Key，可以临时改用 HTTPS：

```powershell
git clone https://github.com/zongyangbigpolo/zh_aqy.git D:\source\zh_aqy
```

### 配置老数据库连接

如果这是老服务器升级，先确认老数据库名称、地址、用户名和密码，然后设置环境变量。下面命令里的值必须替换成老服务器真实配置：

```powershell
setx DB_URL "jdbc:mysql://127.0.0.1:3306/zh_aqy?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8"
setx DB_USERNAME "老数据库用户名"
setx DB_PASSWORD "老数据库密码"
setx TOKEN_SECRET "生产环境自己的token密钥"
setx REDIS_HOST "127.0.0.1"
setx REDIS_PORT "6379"
setx REDIS_DATABASE "0"
setx REDIS_PASSWORD ""
setx RUOYI_PROFILE "D:/aqy/uploadPath"
setx MQTT_HOST "tcp://实际MQTT地址:端口"
setx MQTT_USERNAME "MQTT用户名"
setx MQTT_PASSWORD "MQTT密码"
setx ALIYUN_SMS_ACCESS_KEY_ID "阿里云短信AccessKeyId"
setx ALIYUN_SMS_ACCESS_KEY_SECRET "阿里云短信AccessKeySecret"
setx ALIYUN_SMS_SIGN_NAME "短信签名"
setx ALIYUN_SMS_TEMPLATE_CODE "短信模板CODE"
setx ALIYUN_SMS_COOLDOWN_SECONDS "600"
```

`setx` 写入的是持久环境变量，新开的 PowerShell、CMD 或 Windows 服务才会读取到。生产环境也可以在 Windows 图形界面或服务管理工具里配置环境变量，避免密码出现在命令历史里。

### 升级前备份老数据库

升级代码前先备份数据库。示例：

```powershell
mysqldump -u root -p --databases zh_aqy > D:\aqy\backups\zh_aqy_backup.sql
```

这里会提示输入 MySQL 密码。数据库名不是 `zh_aqy` 时，改成老服务器实际库名。

### 手动构建并部署

进入源码目录：

```powershell
Set-Location D:\source\zh_aqy
```

构建后端：

```powershell
mvn clean package -DskipTests
```

构建完成后应该存在：

```text
D:\source\zh_aqy\aqy-admin\target\aqy-admin.jar
```

构建前端：

```powershell
Set-Location D:\source\zh_aqy\aqy-ui
npm install --legacy-peer-deps
npm run build:prod
```

构建完成后应该存在：

```text
D:\source\zh_aqy\aqy-ui\dist\index.html
```

执行升级脚本，把构建产物部署到运行目录：

```powershell
Set-Location D:\source\zh_aqy
powershell -ExecutionPolicy Bypass -File .\bin\windows-upgrade.ps1 `
  -BackendJar D:\source\zh_aqy\aqy-admin\target\aqy-admin.jar `
  -FrontendDist D:\source\zh_aqy\aqy-ui\dist `
  -DeployRoot D:\aqy `
  -StartBackend `
  -RestartNginx
```

如果后端已经注册成 Windows 服务，例如服务名叫 `ZhAqyBackend`，使用：

```powershell
powershell -ExecutionPolicy Bypass -File .\bin\windows-upgrade.ps1 `
  -BackendJar D:\source\zh_aqy\aqy-admin\target\aqy-admin.jar `
  -FrontendDist D:\source\zh_aqy\aqy-ui\dist `
  -DeployRoot D:\aqy `
  -ServiceName ZhAqyBackend `
  -StartBackend `
  -RestartNginx
```

### 一键拉代码、构建、部署

如果服务器可以访问 GitHub，并且 Git、Maven、Node、npm 都已经安装好，也可以使用脚本完成“拉最新代码 -> 构建 -> 替换部署”：

> 推荐首次部署时先按上面的命令 clone 到 `D:\source\zh_aqy`，后续升级再直接运行这个一键脚本。如果还没有 clone 源码，但想让脚本自己 clone，需要先把 `bin/windows-build-and-deploy.ps1` 单独复制到服务器某个临时目录运行，并通过 `-RepoUrl`、`-SourceDir` 指定仓库和源码目录。

```powershell
powershell -ExecutionPolicy Bypass -File D:\source\zh_aqy\bin\windows-build-and-deploy.ps1 `
  -SourceDir D:\source\zh_aqy `
  -DeployRoot D:\aqy `
  -StartBackend `
  -RestartNginx
```

如果使用 Windows 服务托管后端：

```powershell
powershell -ExecutionPolicy Bypass -File D:\source\zh_aqy\bin\windows-build-and-deploy.ps1 `
  -SourceDir D:\source\zh_aqy `
  -DeployRoot D:\aqy `
  -ServiceName ZhAqyBackend `
  -StartBackend `
  -RestartNginx
```

如果没有配置 SSH Key，可以让脚本使用 HTTPS 仓库地址：

```powershell
powershell -ExecutionPolicy Bypass -File D:\source\zh_aqy\bin\windows-build-and-deploy.ps1 `
  -RepoUrl https://github.com/zongyangbigpolo/zh_aqy.git `
  -SourceDir D:\source\zh_aqy `
  -DeployRoot D:\aqy `
  -StartBackend `
  -RestartNginx
```

这个脚本会：

1. 如果 `D:\source\zh_aqy` 不存在，就 clone 仓库。
2. 如果目录已经存在，就执行 `git pull --ff-only` 拉最新代码。
3. 执行 Maven 后端打包。
4. 执行 npm 前端打包。
5. 调用 `bin/windows-upgrade.ps1` 备份旧文件并替换新文件。
6. 不连接、不删除、不初始化 MySQL 数据库。

### 后续每次更新代码怎么做

以后有新代码时，推荐流程：

```powershell
Set-Location D:\source\zh_aqy
git pull --ff-only
mvn clean package -DskipTests

Set-Location D:\source\zh_aqy\aqy-ui
npm install --legacy-peer-deps
npm run build:prod

Set-Location D:\source\zh_aqy
powershell -ExecutionPolicy Bypass -File .\bin\windows-upgrade.ps1 `
  -BackendJar D:\source\zh_aqy\aqy-admin\target\aqy-admin.jar `
  -FrontendDist D:\source\zh_aqy\aqy-ui\dist `
  -DeployRoot D:\aqy `
  -StartBackend `
  -RestartNginx
```

如果使用一键脚本，则后续只需要：

```powershell
powershell -ExecutionPolicy Bypass -File D:\source\zh_aqy\bin\windows-build-and-deploy.ps1 `
  -SourceDir D:\source\zh_aqy `
  -DeployRoot D:\aqy `
  -StartBackend `
  -RestartNginx
```

## 本地开发

### 后端

```bash
mvn clean package -DskipTests
java -jar aqy-admin/target/aqy-admin.jar
```

默认后端端口为 `7070`，统一上下文路径为 `/prod-api`。

### 前端

```bash
cd aqy-ui
npm install --legacy-peer-deps
npm run dev
```

开发模式下前端会把接口代理到：

```text
http://localhost:7070/prod-api
```

## 生产构建

### 后端构建

在仓库根目录执行：

```bash
mvn clean package -DskipTests
```

构建产物：

```text
aqy-admin/target/aqy-admin.jar
```

### 前端构建

```bash
cd aqy-ui
npm install --legacy-peer-deps
npm run build:prod
```

构建产物：

```text
aqy-ui/dist
```

## GitHub Actions 自动打包

仓库已经配置 GitHub Actions：

```text
.github/workflows/build-release.yml
```

它会在以下场景自动构建：

- 推送到 `main` 分支。
- 推送到跨平台部署分支 `feature/cross-platform-deployment`。
- 手动在 GitHub Actions 页面点击 `Run workflow`。
- 推送 `v*` 标签，例如 `v1.0.0`，会额外创建 GitHub Release。

正式交付客户时，推荐从 GitHub **Releases** 下载最新 `zh-aqy-cross-platform-*.zip` 或 `zh-aqy-cross-platform-*.tar.gz`，不要让客户去 Actions 页面找临时 artifact。

构建内容：

1. 使用 JDK 8 编译后端。
2. 使用 Node.js 16 编译前端。
3. 组装 Windows / Ubuntu / macOS 跨平台可部署压缩包。
4. 上传 artifact：`zh-aqy-cross-platform-package`。

压缩包结构：

```text
zh-aqy-cross-platform-版本号
├── server
│   └── aqy-admin.jar
├── web
│   └── 前端静态文件
├── bin
│   ├── RUN-CHECK-WINDOWS-ENV.bat
│   ├── RUN-INSTALL-WINDOWS-PREREQS.bat
│   ├── RUN-FRESH-WINDOWS-TEST.bat
│   ├── RUN-UPGRADE-EXISTING-WINDOWS.bat
│   ├── RUN-UBUNTU-CHECK-ENV.sh
│   ├── RUN-UBUNTU-INSTALL-PREREQS.sh
│   ├── RUN-UBUNTU-FRESH-TEST.sh
│   ├── RUN-UBUNTU-UPGRADE-EXISTING.sh
│   ├── RUN-MACOS-CHECK-ENV.command
│   ├── RUN-MACOS-INSTALL-PREREQS.command
│   ├── RUN-MACOS-FRESH-TEST.command
│   ├── RUN-MACOS-UPGRADE-EXISTING.command
│   ├── deploy-release.ps1
│   ├── windows-fresh-test-deploy.ps1
│   ├── windows-preflight-check.ps1
│   ├── windows-install-prerequisites.ps1
│   ├── windows-upgrade.ps1
│   ├── unix-common.sh
│   ├── unix-preflight-check.sh
│   ├── unix-install-prerequisites.sh
│   ├── unix-fresh-test-deploy.sh
│   └── unix-upgrade-existing.sh
├── sql
│   ├── ry_20240629.sql
│   ├── quartz.sql
│   └── zh_aqy_schema.sql
├── README.md
└── START-HERE.txt
```

下载到服务器后，先按系统和场景选脚本：

| 系统 | 场景 | 使用脚本 | 是否初始化数据库 |
| --- | --- | --- | --- |
| Windows | 检查机器和配置是否准备好 | `bin\RUN-CHECK-WINDOWS-ENV.bat` | 不会修改数据库 |
| Windows | 帮助安装 Java 8、MySQL、Redis、可选 Nginx | `bin\RUN-INSTALL-WINDOWS-PREREQS.bat` | 不会初始化数据库 |
| Windows | 全新测试机、空数据库、可丢弃测试数据 | `bin\RUN-FRESH-WINDOWS-TEST.bat` | 会初始化空测试库 |
| Windows | 已经部署过老代码、有老 MySQL 数据的服务器 | `bin\RUN-UPGRADE-EXISTING-WINDOWS.bat` 或 `bin\deploy-release.ps1` | 不会碰数据库 |
| Ubuntu | 检查机器和配置是否准备好 | `bin/RUN-UBUNTU-CHECK-ENV.sh` | 不会修改数据库 |
| Ubuntu | 帮助安装 Java 8、MySQL、Redis、可选 Nginx | `bin/RUN-UBUNTU-INSTALL-PREREQS.sh` | 不会初始化数据库 |
| Ubuntu | 全新测试机、空数据库、可丢弃测试数据 | `bin/RUN-UBUNTU-FRESH-TEST.sh` | 会初始化空测试库 |
| Ubuntu | 已经部署过老代码、有老 MySQL 数据的服务器 | `bin/RUN-UBUNTU-UPGRADE-EXISTING.sh` | 不会碰数据库 |
| macOS | 检查机器和配置是否准备好 | `bin/RUN-MACOS-CHECK-ENV.command` | 不会修改数据库 |
| macOS | 帮助安装 Java 8、MySQL、Redis、可选 Nginx | `bin/RUN-MACOS-INSTALL-PREREQS.command` | 不会初始化数据库 |
| macOS | 全新测试机、空数据库、可丢弃测试数据 | `bin/RUN-MACOS-FRESH-TEST.command` | 会初始化空测试库 |
| macOS | 已经部署过老代码、有老 MySQL 数据的测试环境 | `bin/RUN-MACOS-UPGRADE-EXISTING.command` | 不会碰数据库 |

## Windows 依赖安装辅助脚本

全新 Windows 测试机如果还没有安装 Java 8、MySQL、Redis，可以先双击：

```text
bin\RUN-INSTALL-WINDOWS-PREREQS.bat
```

这个脚本会尝试用 Windows 自带的 `winget` 安装依赖；如果没有 `winget`，会尝试使用 Chocolatey。默认安装：

1. Java 8 JDK。
2. MySQL Server/Client。
3. Redis for Windows。
4. 可选 Nginx，脚本会询问是否安装。

注意：

- MySQL 安装器可能需要人工设置 root 密码、端口和 Windows 服务。
- Redis 在 Windows 上使用社区移植版本，生产环境也可以改用单独的 Linux/云 Redis。
- 安装完成后请关闭并重新打开命令行窗口，让 `PATH` 环境变量生效。
- 安装脚本只是辅助工具；如果公司服务器不能联网，可以手动安装这些软件，然后继续运行预检脚本。

## Windows 启动前环境预检脚本

部署或升级前，可以先双击：

```text
bin\RUN-CHECK-WINDOWS-ENV.bat
```

它不会修改系统，也不会连接或初始化数据库，只会检查：

1. Release 包是否完整：`server\aqy-admin.jar`、`web\index.html`、SQL 文件和脚本是否存在。
2. Java 是否可用，是否看起来是 Java 8。
3. `mysql.exe`、Redis、可选 Nginx 是否可用。
4. 老服务器升级需要的环境变量是否配置完整，例如：
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `TOKEN_SECRET`
   - `REDIS_HOST`
   - `REDIS_PORT`
   - `FILE_UPLOAD_PATH`
5. 可选业务能力配置是否完整，例如 MQTT 和阿里云短信。

老服务器升级入口 `RUN-UPGRADE-EXISTING-WINDOWS.bat` 会自动执行严格预检。严格预检不通过时，脚本会停止，不会替换后端 jar 或前端文件。

老服务器升级可以双击：

```text
bin\RUN-UPGRADE-EXISTING-WINDOWS.bat
```

也可以手动执行：

```powershell
powershell -ExecutionPolicy Bypass -File .\bin\deploy-release.ps1 `
  -DeployRoot D:\aqy `
  -StartBackend `
  -RestartNginx
```

如果后端是 Windows 服务：

```powershell
powershell -ExecutionPolicy Bypass -File .\bin\deploy-release.ps1 `
  -DeployRoot D:\aqy `
  -ServiceName ZhAqyBackend `
  -StartBackend `
  -RestartNginx
```

老服务器升级脚本不会自动连接、删除、重建或初始化 MySQL 数据库。老服务器升级时仍然使用原来的 `DB_URL` 指向老数据库即可。

> 不要在老服务器上双击 `RUN-FRESH-WINDOWS-TEST.bat`。它是全新测试机脚本，会导入初始化 SQL；虽然脚本会拒绝向非空数据库导入初始化 SQL，但老服务器应始终使用升级脚本。

## 全新 Windows 测试机一键脚本

如果是一台全新的 Windows 测试机，可以从 GitHub Releases 下载最新 `zh-aqy-cross-platform-*.zip`，解压后运行：

```text
双击 bin\RUN-FRESH-WINDOWS-TEST.bat
```

或者手动执行 PowerShell：

```powershell
powershell -ExecutionPolicy Bypass -File .\bin\windows-fresh-test-deploy.ps1 `
  -MysqlAdminUser root
```

脚本会提示输入 MySQL 管理员密码，然后自动执行：

1. 检查 `java`、`mysql.exe`、Redis 是否可用。
2. 创建测试库，默认库名 `zh_aqy`。
3. 创建应用数据库用户，默认用户 `zh_aqy_app`。
4. 按顺序导入：
   - `sql/ry_20240629.sql`
   - `sql/quartz.sql`
   - `sql/zh_aqy_schema.sql`
5. 生成本次测试所需的运行环境变量。
6. 部署后端到 `D:\aqy\server`，部署前端到 `D:\aqy\web`。
7. 生成后端复用启动脚本：`D:\aqy\server\run-backend.ps1`。
8. 启动后端。
9. 如果能找到 `nginx.exe`，自动生成测试用 Nginx 配置并启动前端站点。

全新测试库初始化后默认管理员为：

```text
账号：admin
临时密码：ChangeMe@123456
```

首次登录后必须立即修改管理员密码。

常用参数：

```powershell
# 指定 MySQL 客户端和部署目录
powershell -ExecutionPolicy Bypass -File .\bin\windows-fresh-test-deploy.ps1 `
  -MysqlAdminUser root `
  -MysqlCli "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" `
  -DeployRoot D:\aqy

# 指定 Nginx 路径
powershell -ExecutionPolicy Bypass -File .\bin\windows-fresh-test-deploy.ps1 `
  -MysqlAdminUser root `
  -NginxExe C:\nginx\nginx.exe

# 如果只是先测后端，暂时没有 Nginx
powershell -ExecutionPolicy Bypass -File .\bin\windows-fresh-test-deploy.ps1 `
  -MysqlAdminUser root `
  -SkipNginx
```

> 这个脚本是“全新测试环境一键部署”脚本，不是老服务器升级脚本。它会执行带 `DROP TABLE IF EXISTS` 的初始化 SQL，所以默认只允许导入到空数据库。如果目标数据库已经有表，脚本会拒绝继续，避免误伤老数据。

脚本运行完成后：

- 后端健康检查：`http://127.0.0.1:7070/prod-api/captchaImage`
- 前端地址：`http://127.0.0.1/`
- 后续手动启动后端：右键 PowerShell 执行 `D:\aqy\server\run-backend.ps1`

## Ubuntu 一键脚本

Ubuntu 推荐下载 `zh-aqy-cross-platform-*.tar.gz`，也可以使用 zip 包。解压后先给脚本确认执行权限：

```bash
chmod +x bin/*.sh
```

如果机器还没有 Java 8、MySQL、Redis，可以运行：

```bash
./bin/RUN-UBUNTU-INSTALL-PREREQS.sh
```

这个脚本会使用 `apt-get` 安装依赖；如果系统默认源没有 Java 8，会尝试添加 Eclipse Temurin 8 的 apt 源。

部署前预检：

```bash
./bin/RUN-UBUNTU-CHECK-ENV.sh
```

全新测试机、空数据库部署：

```bash
./bin/RUN-UBUNTU-FRESH-TEST.sh
```

老 Ubuntu 服务器升级：

```bash
./bin/RUN-UBUNTU-UPGRADE-EXISTING.sh
```

Ubuntu 脚本默认部署目录是：

```text
~/zh-aqy
```

如果要部署到生产常用目录 `/opt/zh-aqy`，可以在执行前设置：

```bash
export DEPLOY_ROOT=/opt/zh-aqy
```

全新测试部署完成后：

- 后端健康检查：`http://127.0.0.1:7070/prod-api/captchaImage`
- 前端测试地址：`http://127.0.0.1:8080/`
- 后续手动启动后端：`~/zh-aqy/server/run-backend.sh`
- 默认管理员：`admin / ChangeMe@123456`

> Ubuntu 老服务器升级脚本会先运行严格预检。预检失败时不会替换后端 jar 或前端文件。

## macOS 一键脚本

macOS 主要建议用于开发、演示和测试，不建议作为客户生产服务器。下载 `zh-aqy-cross-platform-*.zip` 或 `.tar.gz` 后解压。

如果缺少 Java 8、MySQL、Redis，可以双击：

```text
bin/RUN-MACOS-INSTALL-PREREQS.command
```

脚本会使用 Homebrew 安装依赖；如果没有 Homebrew，需要先安装 Homebrew。

部署前预检可以双击：

```text
bin/RUN-MACOS-CHECK-ENV.command
```

全新测试机、空数据库部署可以双击：

```text
bin/RUN-MACOS-FRESH-TEST.command
```

已有 macOS 测试环境升级可以双击：

```text
bin/RUN-MACOS-UPGRADE-EXISTING.command
```

macOS 默认部署目录也是：

```text
~/zh-aqy
```

如果 macOS 提示脚本没有执行权限，可以在终端执行：

```bash
chmod +x bin/*.sh bin/*.command
```

macOS 全新测试部署完成后：

- 后端健康检查：`http://127.0.0.1:7070/prod-api/captchaImage`
- 前端测试地址：`http://127.0.0.1:8080/`
- 默认管理员：`admin / ChangeMe@123456`

## 全新部署

全新环境才需要初始化数据库：

1. 创建 MySQL 数据库，例如 `zh_aqy`。
2. 执行 `sql/ry_20240629.sql`。
3. 执行 `sql/quartz.sql`。
4. 执行 `sql/zh_aqy_schema.sql` 创建业务表。
5. 配置 Redis、MQTT、短信等运行参数。
6. 启动后端 Jar。
7. 将前端 `dist` 放到 Nginx / IIS。

> 注意：`sql` 目录中的脚本主要用于“新环境初始化”。老服务器已经有数据库时，不要为了升级代码重复执行初始化 SQL，否则可能覆盖或污染已有数据。
> `sql/zh_aqy_schema.sql` 使用 `CREATE TABLE IF NOT EXISTS`，不会删除已有数据；但老库升级前仍应先备份。

## 老 Windows 服务器升级：保留原数据库

如果老 Windows 电脑已经部署过当前项目，并且 MySQL 里已经有真实业务数据，升级时遵守下面原则：

1. 不删除数据库。
2. 不重新创建数据库。
3. 不重复执行初始化 SQL。
4. 先备份数据库，再替换后端 Jar 和前端静态文件。
5. 如果某次代码升级明确提供了“增量 SQL”，只执行增量 SQL，不执行初始化 SQL。

本次“告警短信接收人配置”和短信冷却能力复用了已有报警联系人表和系统用户表，没有新增运行时必需表或字段，因此老库升级到当前代码不需要执行破坏性数据库脚本。

### 推荐升级流程

假设 Windows 服务器目录规划如下：

```text
D:\aqy
├── server       # 放 aqy-admin.jar
├── web          # 放前端 dist 内容
├── uploadPath   # 文件上传目录，不能删除
└── backups      # 升级备份目录
```

升级步骤：

1. 在开发机或服务器上拉取最新代码。
2. 构建后端：`mvn clean package -DskipTests`。
3. 构建前端：进入 `aqy-ui` 后执行 `npm install --legacy-peer-deps`、`npm run build:prod`。
4. 在老服务器上备份 MySQL。
5. 停止旧后端进程。
6. 备份旧的 `aqy-admin.jar` 和旧前端静态文件。
7. 复制新的 `aqy-admin.jar` 到后端部署目录。
8. 复制新的 `aqy-ui/dist` 内容到前端部署目录。
9. 启动后端。
10. 重载 Nginx / IIS。
11. 登录系统，检查项目、设备、告警记录、报警联系人、短信配置。

### 数据库备份示例

在 Windows 上可以使用：

```bat
mysqldump -u root -p --databases zh_aqy > D:\aqy\backups\zh_aqy_%date:~0,4%%date:~5,2%%date:~8,2%.sql
```

如果数据库名不是 `zh_aqy`，请改成实际库名。备份命令会提示输入密码，不建议把密码直接写进命令或脚本。

### 使用升级脚本

仓库提供了一个非破坏性的 Windows 升级脚本：

```text
bin/windows-upgrade.ps1
```

脚本做的事情：

- 检查新的后端 Jar 是否存在。
- 检查新的前端 `dist/index.html` 是否存在。
- 备份旧后端 Jar。
- 备份旧前端静态文件。
- 停止匹配到的旧后端 Java 进程。
- 复制新的后端 Jar。
- 替换前端静态文件。
- 可选启动后端。
- 可选重载 Nginx。

脚本不会做的事情：

- 不连接 MySQL。
- 不删除 MySQL 数据库。
- 不执行 `sql/ry_20240629.sql`。
- 不删除上传文件目录。
- 不写入任何密码或 AccessKey。

在仓库根目录执行示例：

```powershell
powershell -ExecutionPolicy Bypass -File .\bin\windows-upgrade.ps1 `
  -BackendJar .\aqy-admin\target\aqy-admin.jar `
  -FrontendDist .\aqy-ui\dist `
  -DeployRoot D:\aqy `
  -StartBackend `
  -RestartNginx
```

如果老服务器已有自己的目录，可以显式指定：

```powershell
powershell -ExecutionPolicy Bypass -File .\bin\windows-upgrade.ps1 `
  -BackendJar C:\build\zh_aqy\aqy-admin\target\aqy-admin.jar `
  -FrontendDist C:\build\zh_aqy\aqy-ui\dist `
  -BackendDir D:\old-aqy\backend `
  -FrontendDir D:\nginx\html `
  -StartBackend
```

如果后端已经通过 NSSM、WinSW 或 Windows 服务方式托管，建议传入服务名，让脚本先停服务、替换文件、再启动服务：

```powershell
powershell -ExecutionPolicy Bypass -File .\bin\windows-upgrade.ps1 `
  -BackendJar C:\build\zh_aqy\aqy-admin\target\aqy-admin.jar `
  -FrontendDist C:\build\zh_aqy\aqy-ui\dist `
  -BackendDir D:\aqy\server `
  -FrontendDir D:\nginx\html `
  -ServiceName ZhAqyBackend `
  -StartBackend
```

### 回滚方式

脚本每次升级会在 `D:\aqy\backups\时间戳` 下保存旧 Jar 和旧前端文件。如果升级后出现问题：

1. 停止新的后端进程。
2. 把备份目录里的旧 Jar 复制回后端目录。
3. 把备份目录里的 `web` 内容复制回前端目录。
4. 启动后端，重载 Nginx / IIS。
5. 数据库不需要回滚，除非你手动执行过增量 SQL。

## Nginx 示例

下面是常见的前后端分离部署方式。按实际目录和域名调整：

```nginx
server {
    listen 80;
    server_name _;

    root D:/aqy/web;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /prod-api/ {
        proxy_pass http://127.0.0.1:7070/prod-api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

如果使用 IIS，也需要实现同样效果：

- `/` 指向前端静态文件目录。
- `/prod-api/*` 反向代理到 `http://127.0.0.1:7070/prod-api/*`。

## 启动后检查

后端检查：

```powershell
Invoke-WebRequest http://127.0.0.1:7070/prod-api/captchaImage -UseBasicParsing
```

页面检查：

```text
http://服务器IP/
```

业务检查：

1. 能否登录。
2. 项目、设备、告警记录是否正常。
3. Redis 是否正常连接。
4. MQTT 是否正常连接并接收数据。
5. 告警短信接收人是否能按项目和告警级别配置。
6. 阿里云短信配置是否完整。

## 告警短信配置说明

当前告警短信发送逻辑是“精确匹配项目和告警级别”：

1. 到“报警联系人”页面。
2. 选择项目。
3. 选择告警级别：一级、二级或三级。
4. 选择一个或多个系统用户作为接收人。
5. 当该项目产生相同级别的告警记录时，系统向这些接收人的手机号发送短信。

例如：

- 只配置“项目 A + 一级”，则只有项目 A 的一级告警会发短信。
- 同时配置“项目 A + 一级”和“项目 A + 二级”，则项目 A 的一级、二级告警都会发短信。
- 当前不是“二级及以上自动匹配”的阈值模式；如果要支持这种模式，需要继续扩展后端查询逻辑或配置模型。
- 系统默认有短信冷却：同一个项目、同一个设备、同一个告警等级、同一个手机号，默认 600 秒内只发送一次，避免持续超限时短信刷屏。可通过 `ALIYUN_SMS_COOLDOWN_SECONDS` 调整，设置为 `0` 表示关闭冷却。

短信发送依赖这些环境变量：

```text
ALIYUN_SMS_ACCESS_KEY_ID
ALIYUN_SMS_ACCESS_KEY_SECRET
ALIYUN_SMS_SIGN_NAME
ALIYUN_SMS_TEMPLATE_CODE
```

阿里云短信模板里的变量名需要和后端传入的模板参数匹配。

### 阿里云短信开通与资料准备

阿里云短信能力不是代码里“申请”的，需要在阿里云控制台开通并审核。代码里最终只需要拿到短信服务的 AccessKey、签名名称和模板 Code。

基本流程：

1. 登录阿里云控制台，完成账号实名认证。
2. 搜索并开通“短信服务”。
3. 进入“短信服务 -> 国内消息”。
4. 申请短信签名，例如公司简称、品牌名或产品名。
5. 申请短信模板，例如告警通知模板。
6. 审核通过后，记录签名名称和模板 Code。
7. 创建 RAM 用户或使用已有 RAM 用户，授权短信发送权限。
8. 生成 AccessKey ID 和 AccessKey Secret。
9. 把这些值配置到服务器环境变量中，重启后端服务。

通常需要准备的资料：

| 类型 | 说明 |
| --- | --- |
| 企业实名认证资料 | 营业执照、统一社会信用代码、企业名称、法人/管理员验证信息等，按阿里云账号认证要求提交。 |
| 短信签名证明 | 如果签名使用公司简称，一般需要营业执照；如果使用品牌/商标，可能需要商标注册证或授权书；如果使用网站/App/公众号/小程序名称，可能需要对应归属证明、ICP备案或后台截图。 |
| 短信模板内容 | 需要提前写好短信内容，明确变量名。模板内容必须符合阿里云审核规范，不能含营销、诱导或不合规内容。 |
| 接收手机号 | 系统用户需要维护手机号；报警联系人配置时选择这些用户后，告警短信才有接收号码。 |
| 费用准备 | 短信按量计费，发送前需要确保阿里云账户余额或套餐包可用。 |

建议模板内容示例：

```text
安全云平台告警：项目${projectId}设备${eqmtName}发生${alarmLevel}级告警，内容：${alarmContent}，时间：${recordTime}。
```

实际模板变量以阿里云审核通过的模板为准。当前后端会传入多组可用变量，包括：

```text
projectId
equipment
eqmtName
alarmLevel
level
alarmContent
content
recordTime
time
value1
value2
value3
```

模板里用到哪些变量，阿里云就会校验哪些变量。模板审核通过后，把配置写到服务器环境变量：

```powershell
setx ALIYUN_SMS_ACCESS_KEY_ID "你的AccessKeyId"
setx ALIYUN_SMS_ACCESS_KEY_SECRET "你的AccessKeySecret"
setx ALIYUN_SMS_SIGN_NAME "审核通过的短信签名"
setx ALIYUN_SMS_TEMPLATE_CODE "审核通过的模板Code"
```

注意事项：

- 不要把 AccessKey 写进代码，也不要提交到 Git。
- 推荐使用 RAM 子账号，只授予短信发送需要的权限，不建议使用主账号 AccessKey。
- 修改环境变量后，需要重启后端服务。
- 如果短信没有发出，优先检查：签名是否审核通过、模板 Code 是否正确、模板变量是否匹配、账户是否欠费、用户手机号是否为空。

## 常见问题

### 升级会不会删数据库？

正常升级不会删除数据库。替换代码只需要替换后端 Jar 和前端静态文件。不要重复执行初始化 SQL。

### 是否要安装 Tomcat？

不需要。后端 Jar 内置 Tomcat，直接 `java -jar aqy-admin.jar` 启动。

### 是否必须用 Nginx？

不强制，但推荐。前端是静态文件，Nginx 部署和反向代理最简单。Windows 上也可以使用 IIS。

### 上传文件目录会不会被覆盖？

升级脚本只处理后端 Jar 和前端静态文件，不处理 `uploadPath`。只要不要手动删除上传目录，历史上传文件就会保留。

### 老服务器直接 git pull 可以吗？

可以，但不推荐在生产服务器上直接编译和调试。更稳妥的方式是在开发机或构建机打包出 `aqy-admin.jar` 和 `aqy-ui/dist`，再拷贝到老服务器替换。

### 当前新增短信告警功能是否需要数据库迁移？

当前不需要。该能力复用已有报警联系人和系统用户数据结构。
