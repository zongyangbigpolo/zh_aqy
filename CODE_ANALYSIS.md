# 中瀚安全云平台代码仓库分析与短信告警扩展方案

> 分析时间：2026-07-29  
> 仓库位置：`/Users/polo2/srccode/try-down-demo/zh_aqy`  
> 项目基线：安全云平台二次开发，面向安全监测/边坡监测场景
> 说明：本文档基于代码静态分析生成；当前仓库已补充 `sql/zh_aqy_schema.sql` 作为安全云业务表结构脚本。

---

## 1. 项目总体结论

这是一个基于 **Java 17 + Spring Boot 2.7 + Spring Security + MyBatis + Redis + JWT + Vue 2.7 + Element UI** 的前后端分离安全云平台，核心业务模块包括：

- 工程项目管理
- 断面管理
- 网关与采集设备管理
- 摄像头/海康设备接入
- MQTT 设备通信
- 原始监测数据入库
- 实时/历史报表
- 报警等级、报警联系人、报警记录
- WebSocket 推送前端刷新
- 已接入阿里云短信 SDK，但业务告警短信发送尚未真正闭环

从告警短信能力看，当前代码已经具备三个关键基础：

1. **告警判定入口已存在**：`AqyAlarmServiceImpl` 中的 `check*RawDataWillAlarm(...)`。
2. **告警联系人配置已存在**：`aqy_alarm_person` 对应 `AqyAlarmPerson`，含项目、报警等级、联系人、联系方式。
3. **短信发送服务已存在**：`aqy-framework` 中 `SmsConfig`、`SmsService`、`SmsServiceImpl`，且已引入阿里云短信 SDK。

但是告警短信目前没有真正发出，核心原因是：

- `AqyAlarmServiceImpl#sendAlarmMessageToPerson(...)` 只有空循环，未调用 `SmsService`。
- 查询报警联系人时只按 `alarmLevel` 过滤，没有按 `projectId` 过滤。
- `alarm_count`、`cron_express` 等字段在告警等级中存在，但告警发送节流/次数控制尚未实现。
- 短信发送结果没有持久化，失败不可追踪，不利于补偿重试。

---

## 2. 仓库规模与模块结构

### 2.1 代码规模

当前仓库主要源码统计：

| 类型 | 数量 |
|---|---:|
| Java 源文件 | 434 |
| Vue 文件 | 134 |
| JS 文件 | 97 |
| Maven 模块 | 8 个业务/框架模块 + 根 POM |

### 2.2 后端 Maven 模块

根 `pom.xml` 定义了多模块工程：

| 模块 | 作用 |
|---|---|
| `aqy-admin` | Spring Boot 启动入口，Web 服务聚合模块 |
| `aqy-framework` | 框架层：安全、Redis、配置、短信服务、Web 基础能力 |
| `aqy-system` | 系统管理：用户、角色、菜单、部门、字典、参数等 |
| `aqy-quartz` | 定时任务模块 |
| `aqy-generator` | 代码生成模块 |
| `aqy-common` | 通用工具、基础实体、注解、AQY 业务 Domain |
| `aqy-main` | 安全云业务主模块：工程、设备、原始数据、告警、报表等 |
| `aqy-mqtt` | MQTT 接入、订阅、发布、主题策略分发 |

后端启动入口是：

- `aqy-admin/src/main/java/com/ruoyi/RuoYiApplication.java`

关键注解：

- `@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })`
- `@ConfigurationPropertiesScan`

MyBatis Mapper 扫描由框架配置完成：

- `aqy-framework/src/main/java/com/ruoyi/framework/config/ApplicationConfig.java`
- `@MapperScan("com.ruoyi.**.mapper")`

### 2.3 前端模块

前端在 `aqy-ui`，基于 Vue2：

| 目录 | 作用 |
|---|---|
| `src/api/aqy` | AQY 业务接口封装 |
| `src/views/aqy` | AQY 业务页面 |
| `src/views/screen` | 大屏页面 |
| `src/router` | 路由 |
| `src/store` | Vuex 状态 |
| `src/utils/request.js` | Axios 请求封装 |
| `src/permission.js` | 前端路由权限控制 |

`aqy-ui/package.json` 中项目描述为“安全云平台”，核心依赖：

- `vue@2.6.12`
- `element-ui@2.15.14`
- `axios@0.28.1`
- `echarts`
- `ezuikit-js`
- `speak-tts`

---

## 3. 技术栈与运行配置

### 3.1 后端技术栈

| 技术 | 当前用途 |
|---|---|
| Spring Boot 2.7.18 | 后端应用框架 |
| Spring Security 5.7.12 | 登录认证、接口权限 |
| JWT | 无状态 Token |
| MyBatis | DAO/Mapper 持久化 |
| PageHelper | 分页 |
| Druid | 数据库连接池与 SQL 监控 |
| Redis | 登录态、缓存等若依基础能力 |
| Quartz | 定时任务 |
| Spring Integration MQTT + Eclipse Paho | MQTT 设备通信 |
| WebSocket | 后端主动通知前端刷新 |
| Aliyun Dysmsapi SDK | 短信发送 |
| Fastjson2 / Jackson / Hutool | JSON 与工具类 |
| Lombok | 部分 Domain/配置类简化代码 |

### 3.2 后端核心配置

主配置：

- `aqy-admin/src/main/resources/application.yml`

数据库配置：

- `aqy-admin/src/main/resources/application-druid.yml`

关键配置项：

| 配置项 | 说明 |
|---|---|
| `server.port=7070` | 后端服务端口 |
| `server.servlet.context-path=/prod-api` | 后端统一上下文 |
| `spring.datasource.druid.master.url` | MySQL 数据库连接 |
| `spring.redis.*` | Redis 连接 |
| `token.header=Authorization` | JWT Header |
| `mybatis.typeAliasesPackage=com.ruoyi.**.domain` | MyBatis 类型别名 |
| `mybatis.mapperLocations=classpath*:mapper/**/*Mapper.xml` | Mapper XML 扫描 |
| `mqtt.*` | MQTT Broker、客户端、订阅主题等 |
| `aliyun.sms.*` | 阿里云短信配置 |

### 3.3 重要安全注意点

当前配置文件中存在明文的外部服务访问凭据配置。建议立即处理：

1. 将短信 AccessKey、MQTT 密码、数据库密码从配置文件迁移到环境变量或密钥管理系统。
2. 对已经暴露过的 AccessKey 做轮换。
3. 不要把生产凭据提交到源码仓库。
4. 对不同环境使用 `application-dev.yml`、`application-prod.yml` 或部署平台 Secret 注入。

---

## 4. 领域模型分析

AQY 业务 Domain 位于：

- `aqy-common/src/main/java/com/ruoyi/common/core/domain/aqy`

### 4.1 工程与断面

#### `AqyProject`

对应工程项目，核心字段：

| 字段 | 含义 |
|---|---|
| `id` | 项目 ID |
| `name` | 项目名称 |
| `city` | 城市 |
| `projectType` | 项目类型 |
| `companyName` | 所属企业 |
| `projectDesc` | 项目简介 |
| `longitude` / `latitude` / `elevation` | 经纬高 |
| `courseAngle` / `depressionAngle` | 航向角、俯视角 |
| `yzCompany` / `jsCompany` | 业主/建设单位 |
| `projectStartDate` | 项目开始日期 |

控制器：

- `AqyProjectController`

接口路径：

- `GET /aqy/project/list`
- `GET /aqy/project/{id}`
- `POST /aqy/project`
- `PUT /aqy/project`
- `DELETE /aqy/project/{ids}`
- `POST /aqy/project/export`

#### `AqySection`

断面信息，核心字段：

| 字段 | 含义 |
|---|---|
| `id` | 断面 ID |
| `projectId` | 所属项目 |
| `sectionName` | 断面名称 |
| `longitude` / `latitude` | 经纬度 |
| `eqmtTypeIds` | 前端传入/展示用设备类型集合 |
| `children` / `children2` | 断面下设备树 |

---

### 4.2 设备与设备类型

#### `AqyEquipmentType`

设备类型，对应 `aqy_equipment_type`：

| 字段 | 含义 |
|---|---|
| `id` | 设备类型 ID |
| `eqmtTypeName` | 设备类型名称 |
| `eqmtTypeSymbol` | 类型标识，如 `WY`、`LF`、`QJ`、`YL` |
| `uploadIntv` | 正常上传间隔 |
| `plusIntv` | 加报上传间隔 |

当前代码中用 `eqmtTypeSymbol` 决定原始数据解析和告警判定逻辑。

#### `AqyEquipment`

数据采集设备，对应 `aqy_equipment`：

| 字段 | 含义 |
|---|---|
| `id` | 设备 ID |
| `projectId` | 所属项目 |
| `gatwayId` | 所属智能网关 |
| `eqmtTypeId` | 设备类型 |
| `eqmtName` | 设备名称 |
| `eqmtCode` | 设备编码 |
| `sortNum` | 靶标序号/排序 |
| `longitude` / `latitude` / `elevation` | 坐标 |
| `initialX` / `initialY` / `initialH` | 初始值 |
| `accumulativeChangeValueX/Y/H` | 累计变化值 |
| `instantChangeValue` | 瞬时变化值 |
| `unitName` | 单位 |
| `alarmLevel` | 当前报警等级 ID，0 或 null 表示未报警 |
| `onlineStatus` | 在线状态 |
| `xOrY` | 位移设备方向 |
| `qrtzJobId` | 关联定时任务 |
| `canCatchImage` | 是否可以抓图 |

控制器：

- `AqyEquipmentController`

重要接口：

| 接口 | 作用 |
|---|---|
| `GET /aqy/aqyEquipment/list` | 设备分页列表 |
| `GET /aqy/aqyEquipment/listEqmtsGroupByType` | 按设备类型分组汇总 |
| `GET /aqy/aqyEquipment/queryEquipmentAlarmStatus` | 查询项目下设备报警状态 |
| `GET /aqy/aqyEquipment/selectAqyEquipmentListForReport` | 报表设备列表 |

---

### 4.3 原始监测数据

系统按设备类型拆分原始数据表和 Domain：

| 类型标识 | Domain | 含义 |
|---|---|---|
| `WY` | `AqyEquipmentWyRaw` | 位移监测数据 |
| `LF` | `AqyEquipmentLfRaw` | 裂缝监测数据 |
| `QJ` | `AqyEquipmentQjRaw` | 倾角监测数据 |
| `YL` | `AqyEquipmentYlRaw` | 雨量/水位监测数据 |

#### `AqyEquipmentWyRaw`

字段：

- `eqmtId`
- `eqmtCode`
- `eqmtName`
- `catchTime`
- `valueWy`
- `xOrY`
- `picture`
- `projectId`
- `initialX`

#### `AqyEquipmentLfRaw`

字段：

- `eqmtId`
- `eqmtCode`
- `eqmtName`
- `catchTime`
- `lfValue`
- `tempValue`
- `projectId`
- `initialX`

#### `AqyEquipmentQjRaw`

字段：

- `eqmtId`
- `eqmtCode`
- `eqmtName`
- `catchTime`
- `xValueQj`
- `yValueQj`
- `zValueQj`
- `tempValue`
- `initialX`
- `initialY`
- `initialH`

#### `AqyEquipmentYlRaw`

字段：

- `eqmtId`
- `eqmtCode`
- `eqmtName`
- `catchTime`
- `ylValue`
- `initialX`

报表入口：

- `AqyRawReportController`
- `GET /aqy/rawReport/listRealTime`

该接口根据 `eqmtTypeId` 查设备类型，再按 `eqmtTypeSymbol` 分发到不同 raw service。

---

### 4.4 告警模型

#### `AqyAlarm`

报警等级，对应 `aqy_alarm`：

| 字段 | 含义 |
|---|---|
| `id` | 报警等级配置 ID |
| `eqmtType` | 设备类型 ID |
| `alarmLevel` | 报警等级，前端约定一级 > 二级 > 三级 |
| `instantThresholdValue` | 每日/瞬时变化阈值 |
| `accumulativeThresholdValue` | 累计变化阈值 |
| `upperLimit` / `lowerLimit` | 上下限 |
| `alarmColor` | 报警颜色 |
| `alarmTemplate` | 报警内容模板 |
| `alarmCount` | 信息推送次数，当前未真正使用 |
| `cronExpress` | 采集任务频率，当前告警发送链路未使用 |

控制器：

- `AqyAlarmController`
- 路径：`/aqy/alarmLevel`

前端页面：

- `aqy-ui/src/views/aqy/alarmLevel/index.vue`

#### `AqyAlarmPerson`

报警联系人，对应 `aqy_alarm_person`：

| 字段 | 含义 |
|---|---|
| `id` | 联系人配置 ID |
| `projectId` | 工程项目 |
| `alarmLevel` | 报警等级 |
| `contactPerson` | 联系人姓名 |
| `contactPersonNumber` | 联系方式，短信发送应使用该字段 |
| `isDelete` | 删除标记 |

控制器：

- `AqyAlarmPersonController`
- 路径：`/aqy/alarmPerson`

前端页面：

- `aqy-ui/src/views/aqy/alarmPerson/index.vue`

#### `AqyAlarmRecord`

报警记录，对应 `aqy_alarm_record`：

| 字段 | 含义 |
|---|---|
| `id` | 报警记录 ID |
| `projectId` | 工程项目 |
| `qmtId` | 采集设备 ID |
| `eqmtName` | 设备名称 |
| `alarmId` | 报警等级配置 ID |
| `alarmLevel` | 报警等级 |
| `instantValue` | 瞬时报警值 |
| `accumulativeValue1/2/3` | 累计报警值 |
| `alarmColor` | 报警颜色 |
| `alarmContent` | 报警内容 |
| `recordTime` | 报警时间 |
| `remedialMeasures` | 处理措施 |
| `remedialTime` | 处理时间 |
| `remedialUid` | 处理人 |
| `eqmtTypeSymbol` | 设备类型标识，Mapper 查询时关联得到 |

控制器：

- `AqyAlarmRecordController`
- 路径：`/aqy/alarmRecord`

重要业务：

- `POST /aqy/alarmRecord/remedialAlarm`
  - 设置设备 `alarmLevel=0`
  - 更新报警记录处理信息
  - WebSocket 推送前端刷新

前端页面：

- `aqy-ui/src/views/aqy/alarmRecord/index.vue`

---

## 5. MQTT 接入链路分析

MQTT 模块位于：

- `aqy-mqtt`

### 5.1 MQTT 配置

核心配置类：

- `aqy-mqtt/src/main/java/com/ruoyi/mqtt/config/MqttConfig.java`

核心属性类：

- `aqy-mqtt/src/main/java/com/ruoyi/mqtt/properties/MqttProperties.java`

配置来源：

- `application.yml` 下 `mqtt.*`

订阅主题包括：

| 主题 | 含义 |
|---|---|
| `$send/+/+` | 平台下发远程命令主题 |
| `$reply/+/+` | 设备回复平台下发指令 |
| `$send/+/firmware` | 固件升级下发 |
| `$reply/+/firmware` | 固件升级回复 |
| `$data/+/raw` | 设备原始数据上传 |
| `$devstatus` | 设备状态上传 |
| `$request/+/getCurrentTime` | 网关请求当前时间 |
| `$response/+/getCurrentTime` | 网关获取时间回复 |

### 5.2 入站消息处理链路

整体链路：

```text
MQTT Broker
  -> MqttPahoMessageDrivenChannelAdapter
  -> mqttInboundChannel
  -> MqttConfig#handler()
  -> MqttCallbackHandle#handle(topic, payload)
  -> MessageService#processMessage(topic, payload)
  -> MessageHandlerRegistry#getHandler(topic)
  -> 具体 MessageHandlerStrategy#handle(payload)
```

策略注册：

- `MessageHandlerRegistry` 在构造时从 Spring 容器获取所有 `MessageHandlerStrategy` Bean。
- 通过每个策略的 `getTopicPattern()` 返回正则表达式来匹配 MQTT topic。

原始数据策略：

- `RawDataStrategy#getTopicPattern()` 返回 `\\$data/.*/raw`
- 即匹配 `$data/{device}/raw`

### 5.3 原始数据入库与告警触发

核心类：

- `aqy-mqtt/src/main/java/com/ruoyi/mqtt/strategy/RawDataStrategy.java`

逻辑摘要：

1. 收到 `$data/+/raw` 消息。
2. 将 payload 解析成嵌套 Map。
3. 按设备编码查询 `AqyEquipment`。
4. 根据设备类型 `eqmtTypeSymbol` 分支处理：
   - `LF`：裂缝数据，插入 `aqy_equipment_lf_raw`
   - `QJ`：倾角数据，插入 `aqy_equipment_qj_raw`
   - `YL`：水位/雨量数据，插入 `aqy_equipment_yl_raw`
   - `WY`：位移数据处理代码目前大段被注释，当前没有真正入库与告警
5. 调用 `AqyAlarmServiceImpl#check*RawDataWillAlarm(...)`。
6. WebSocket 推送前端刷新：
   - LF：`WebSocketUsers.pushMessage(3, -1, null)`
   - QJ：`WebSocketUsers.pushMessage(4, -1, null)`
   - YL：`WebSocketUsers.pushMessage(5, -1, null)`

---

## 6. 告警判定链路深度分析

核心类：

- `aqy-main/src/main/java/com/ruoyi/service/impl/AqyAlarmServiceImpl.java`

### 6.1 总体流程

以裂缝 `LF` 为例：

```text
RawDataStrategy 插入 LF 原始数据
  -> aqyAlarmService.checkLfRawDataWillAlarm(lfMap)
  -> 查询所有 AqyAlarm，并按 eqmtType 分组
  -> 遍历本次设备原始数据
  -> 查询设备 AqyEquipment
  -> 根据当前值 - 设备初始值计算累计变化
  -> 与该设备类型下的 accumulativeThresholdValue 比较
  -> 更新设备 accumulativeChangeValueX 和 alarmLevel
  -> 若触发报警，saveAlarmRecord(...)
  -> 插入 AqyAlarmRecord
  -> sendAlarmMessageToPerson(...)
```

### 6.2 各类型当前阈值判断

#### 位移 WY

方法：

- `checkWyRawDataWillAlarm(...)`
- `getWyAlaramLevel(...)`

逻辑：

- 按 `xOrY` 判断使用 X 或 Y。
- 当前代码中 Y 方向使用了 `aqyEquipment.getInitialX()`，应确认是否应为 `initialY`。
- 达到任一累计阈值则设定报警等级。
- 遍历到第一个满足条件的等级后 `break`。

注意：`RawDataStrategy` 中 WY 处理代码目前被注释，实际 MQTT 原始数据链路不会触发 WY 入库/告警。

#### 裂缝 LF

方法：

- `checkLfRawDataWillAlarm(...)`
- `getLfAlaramLevel(...)`

逻辑：

- `lfValue - initialX` 得到累计变化。
- 绝对值大于等于 `accumulativeThresholdValue` 时设置报警等级。

#### 倾角 QJ

方法：

- `checkQjRawDataWillAlarm(...)`
- `getQjAlaramLevel(...)`

逻辑：

- X/Y/Z 分别减 `initialX`、`initialY`、`initialH`。
- 任一轴达到阈值即触发报警。

#### 水位/雨量 YL

方法：

- `checkYlRawDataWillAlarm(...)`
- `getYlAlaramLevel(...)`

逻辑：

- `ylValue - initialX` 得到累计变化。
- 绝对值大于等于 `accumulativeThresholdValue` 时报警。

### 6.3 告警记录保存

方法：

- `AqyAlarmServiceImpl#saveAlarmRecord(Long levelId, AqyEquipment aqyEquipment, BigDecimal... diffValues)`

当前动作：

1. 通过 `levelId` 查询 `AqyAlarm`。
2. 构造 `AqyAlarmRecord`：
   - 项目 ID
   - 设备 ID
   - 设备名称
   - 报警等级 ID
   - 报警等级
   - 累计变化值
   - 报警颜色
   - 报警内容模板
   - 记录时间
3. 调用 `aqyAlarmRecordService.insertAqyAlarmRecord(...)`。
4. 调用 `sendAlarmMessageToPerson(aqyAlarm.getAlarmLevel())`。

### 6.4 当前告警链路存在的问题

| 问题 | 位置 | 影响 |
|---|---|---|
| 短信发送未实现 | `AqyAlarmServiceImpl#sendAlarmMessageToPerson` | 告警只入库，不通知联系人 |
| 异常被空 catch 吞掉 | `saveAlarmRecord` | 插入/短信失败不记录，排障困难 |
| 联系人未按项目过滤 | `sendAlarmMessageToPerson` | 不同项目同等级联系人可能被误通知 |
| `alarmCount` 未使用 | `AqyAlarm` / `alarmLevel` 页面 | 信息推送次数配置无效果 |
| `cronExpress` 未用于告警发送 | `AqyAlarm` / `alarmLevel` 页面 | 采集任务频率配置和告警联动不清晰 |
| WY MQTT 入库告警代码被注释 | `RawDataStrategy` | 位移设备实时链路可能不可用 |
| 告警等级排序语义需统一 | 前端写“一级 > 二级 > 三级”，后端部分逻辑按遍历顺序取值 | 若阈值列表排序不符合预期，可能取错等级 |
| 缺少短信发送日志 | 无 | 无法审计、重试、去重 |
| 缺少发送节流 | 无 | 持续超阈值时可能频繁生成记录/短信 |

---

## 7. 前端业务页面分析

AQY 前端接口封装在：

- `aqy-ui/src/api/aqy`

AQY 页面在：

- `aqy-ui/src/views/aqy`

### 7.1 告警等级页面

文件：

- `aqy-ui/src/views/aqy/alarmLevel/index.vue`
- `aqy-ui/src/api/aqy/alarmLevel.js`

能力：

- 按设备类型、报警等级查询
- 新增/修改/删除
- 配置：
  - 设备类型
  - 报警等级
  - 阈值
  - 信息推送次数
  - 采集任务频率 Cron 表达式

注意：

- 页面允许配置 `alarmCount`，但后端告警发送未使用。
- 页面允许配置 `cronExpress`，但当前未看到告警服务基于该字段调度短信发送。

### 7.2 告警联系人页面

文件：

- `aqy-ui/src/views/aqy/alarmPerson/index.vue`
- `aqy-ui/src/api/aqy/alarmPerson.js`

能力：

- 按项目、报警等级查询联系人
- 新增/修改/删除联系人
- 字段：
  - 工程项目
  - 报警等级
  - 联系人姓名
  - 联系方式

这张表已经可以作为“特定用户/特定联系人接收告警短信”的配置入口。

### 7.3 告警记录页面

文件：

- `aqy-ui/src/views/aqy/alarmRecord/index.vue`
- `aqy-ui/src/api/aqy/alarmRecord.js`

能力：

- 按项目、设备类型、设备、报警等级、报警时间查询
- 展示告警等级、累计变化值、处理状态、处理措施、处理时间、处理人
- 支持“报警处理”
- 支持“取消报警”

后端处理接口：

- `POST /aqy/alarmRecord/remedialAlarm`

处理后会：

- 设备 `alarmLevel` 置 0
- 更新报警记录处理字段
- WebSocket 推送前端刷新

---

## 8. 现有短信能力分析

短信相关代码在 `aqy-framework`：

| 文件 | 作用 |
|---|---|
| `SmsConfig.java` | 读取 `aliyun.sms.*` 配置 |
| `SmsService.java` | 短信发送接口 |
| `SmsServiceImpl.java` | 阿里云短信实现 |

当前接口：

```java
public interface SmsService {
    boolean sendSms(Map<String, Object> param, String phone);
}
```

实现逻辑：

1. 使用 `SmsConfig` 读取 AccessKey、签名、模板编码。
2. 创建阿里云 `DefaultAcsClient`。
3. 构建 `SendSmsRequest`。
4. 将 `param` 转 JSON 后设置为模板参数。
5. 调用 `client.getAcsResponse(request)`。
6. 返回 `response.getCode().equals("OK")`。

当前问题：

- 短信发送失败只 `printStackTrace`，没有项目统一日志。
- 返回 `false` 后调用方如果不处理，失败会被静默忽略。
- 没有短信发送日志表。
- 模板参数名需要和阿里云短信模板完全匹配，否则接口可能成功接收但模板渲染失败或被平台拒绝。
- 当前配置只支持一个全局短信模板，如后续不同告警级别/项目要不同模板，需要扩展配置或表字段。

---

## 9. “告警到了一定地步给特定用户发送短信”的实现方案

这里的“一定地步”可以有两种常见业务解释：

1. **达到某个报警等级**：例如只有一级/二级报警才短信通知，三级只在系统内展示。
2. **同一设备/同一报警持续或重复达到一定次数**：例如同一未处理告警连续出现 3 次后才短信通知，避免偶发波动刷短信。

当前代码中最贴近现有模型的是：

- 用 `AqyAlarm.alarmLevel` 表示告警严重度。
- 用 `AqyAlarm.alarmCount` 表示信息推送次数，但目前未使用。
- 用 `AqyAlarmPerson.projectId + alarmLevel` 表示“某项目某等级通知哪些联系人”。

因此推荐分两阶段实现。

---

## 10. 推荐方案 A：最小闭环版

### 10.1 适用场景

如果需求是：

> 只要某项目的某设备达到指定报警等级，就给这个项目下配置了该报警等级的联系人发送短信。

那么无需改表，直接复用：

- `aqy_alarm`
- `aqy_alarm_person`
- `aqy_alarm_record`
- `SmsService`

### 10.2 需要修改的后端代码

#### 1）修改 `AqyAlarmServiceImpl`

文件：

- `aqy-main/src/main/java/com/ruoyi/service/impl/AqyAlarmServiceImpl.java`

改造点：

1. 注入短信服务：

```java
@Autowired
private SmsService smsService;
```

2. `saveAlarmRecord(...)` 中保留 `aqyAlarmRecord` 对象，并把项目、设备、报警内容传给短信方法。

当前代码：

```java
sendAlarmMessageToPerson(aqyAlarm.getAlarmLevel());
```

建议改为：

```java
sendAlarmMessageToPerson(aqyAlarmRecord, aqyAlarm);
```

3. 修改短信发送方法签名：

```java
private void sendAlarmMessageToPerson(AqyAlarmRecord alarmRecord, AqyAlarm alarm)
```

4. 查询联系人时必须加项目过滤：

```java
AqyAlarmPerson query = new AqyAlarmPerson();
query.setProjectId(alarmRecord.getProjectId());
query.setAlarmLevel(alarm.getAlarmLevel());
List<AqyAlarmPerson> people = aqyAlarmPersonService.selectAqyAlarmPersonList(query);
```

5. 构造短信模板参数：

```java
Map<String, Object> params = new HashMap<>();
params.put("equipment", alarmRecord.getEqmtName());
params.put("level", alarmRecord.getAlarmLevel());
params.put("time", DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", alarmRecord.getRecordTime()));
params.put("content", alarmRecord.getAlarmContent());
```

6. 调用短信：

```java
for (AqyAlarmPerson person : people) {
    smsService.sendSms(params, person.getContactPersonNumber());
}
```

> 注意：`params` 的 key 必须和阿里云短信模板变量一致。比如模板是“设备${equipment}触发${level}级告警”，就必须传 `equipment` 和 `level`。

#### 2）建议修复 `saveAlarmRecord` 的异常处理

当前代码：

```java
try {
    ...
} catch (Exception ex) {

}
```

这是高风险静默失败。建议至少使用日志记录：

```java
log.error("保存或发送告警通知失败，eqmtId={}, levelId={}", aqyEquipment.getId(), levelId, ex);
```

需要给类加：

```java
@Slf4j
```

并引入：

```java
import lombok.extern.slf4j.Slf4j;
```

#### 3）建议检查联系人手机号

发送前校验：

- `contactPersonNumber` 非空
- 手机号格式合法

若项目中已有手机号工具类，优先复用；否则可以用若依已有 `StringUtils.isNotBlank` 做基础校验。

### 10.3 需要修改的配置

文件：

- `aqy-admin/src/main/resources/application.yml`

建议改为环境变量占位：

```yaml
aliyun:
  sms:
    accessKeyId: ${ALIYUN_SMS_ACCESS_KEY_ID}
    accessKeySecret: ${ALIYUN_SMS_ACCESS_KEY_SECRET}
    signName: ${ALIYUN_SMS_SIGN_NAME}
    templateCode: ${ALIYUN_SMS_TEMPLATE_CODE}
```

### 10.4 前端是否需要改

最小闭环版不需要改前端，因为告警联系人页面已经可以配置：

- 项目
- 报警等级
- 联系人姓名
- 联系方式

使用流程：

1. 在“报警等级”页面配置某设备类型的阈值。
2. 在“报警联系人”页面为项目和报警等级配置联系人手机号。
3. 设备数据触发对应等级告警。
4. 后端插入报警记录并发送短信。

### 10.5 这个版本的不足

- 不防重复短信。
- 不记录短信发送结果。
- 不能指定系统用户，只能指定联系人姓名和手机号。
- 不能控制“连续 N 次才发送”。
- 不能控制同一告警冷却时间。

---

## 11. 推荐方案 B：生产可用版

如果需求中的“一定地步”不仅是等级，还包括：

- 告警连续达到 N 次
- 告警持续 N 分钟
- 只给特定系统用户发
- 同一设备同一等级一段时间内只发一次
- 需要短信发送审计/失败重试

建议做生产可用版。

### 11.1 数据库改造建议

#### 方案 B-1：增强 `aqy_alarm_person`

如果仍然用“联系人表”管理接收人，可给 `aqy_alarm_person` 增加字段：

```sql
alter table aqy_alarm_person
    add column user_id bigint null comment '关联系统用户ID，可为空',
    add column sms_enabled tinyint default 1 comment '是否启用短信通知',
    add column min_alarm_level int null comment '最低通知等级，数值越小越严重时需统一语义',
    add column notify_threshold_count int default 1 comment '连续/重复达到多少次后通知',
    add column cooldown_minutes int default 30 comment '同设备同等级短信冷却分钟数';
```

说明：

- 如果使用 `user_id`，可以从 `sys_user.phonenumber` 获取手机号。
- 如果不使用 `user_id`，继续使用 `contact_person_number`。
- `notify_threshold_count` 用于“告警到一定地步”中的“次数阈值”。
- `cooldown_minutes` 避免短信轰炸。

#### 方案 B-2：新增短信发送日志表

建议新增：

```sql
create table aqy_alarm_notify_log (
    id bigint primary key auto_increment comment '主键',
    alarm_record_id bigint not null comment '报警记录ID',
    project_id bigint not null comment '项目ID',
    qmt_id bigint not null comment '设备ID',
    alarm_id bigint not null comment '报警等级配置ID',
    alarm_level int not null comment '报警等级',
    receiver_user_id bigint null comment '接收系统用户ID',
    receiver_name varchar(64) null comment '接收人姓名',
    receiver_phone varchar(32) not null comment '接收手机号',
    channel varchar(16) default 'SMS' comment '通知渠道',
    template_code varchar(64) null comment '短信模板编码',
    template_param varchar(1000) null comment '模板参数JSON',
    send_status varchar(16) not null comment 'PENDING/SUCCESS/FAILED/SKIPPED',
    send_result varchar(1000) null comment '发送结果或失败原因',
    retry_count int default 0 comment '重试次数',
    send_time datetime null comment '发送时间',
    create_time datetime default current_timestamp comment '创建时间'
) comment '报警通知发送日志';
```

收益：

- 可追踪每条短信发给谁、是否成功、失败原因。
- 可基于日志实现去重、冷却、补偿重试。
- 后续可扩展微信、站内信、邮件等渠道。

### 11.2 新增 Domain / Mapper / Service

建议新增：

| 层 | 文件 |
|---|---|
| Domain | `aqy-common/src/main/java/com/ruoyi/common/core/domain/aqy/AqyAlarmNotifyLog.java` |
| Mapper 接口 | `aqy-main/src/main/java/com/ruoyi/mapper/AqyAlarmNotifyLogMapper.java` |
| Mapper XML | `aqy-main/src/main/resources/mapper/AqyAlarmNotifyLogMapper.xml` |
| Service 接口 | `aqy-main/src/main/java/com/ruoyi/service/IAqyAlarmNotifyLogService.java` |
| Service 实现 | `aqy-main/src/main/java/com/ruoyi/service/impl/AqyAlarmNotifyLogServiceImpl.java` |

可选新增后台页面：

| 前端文件 | 作用 |
|---|---|
| `aqy-ui/src/api/aqy/alarmNotifyLog.js` | 查询通知日志 |
| `aqy-ui/src/views/aqy/alarmNotifyLog/index.vue` | 通知日志页面 |

### 11.3 告警通知服务拆分

建议不要把短信发送逻辑继续堆在 `AqyAlarmServiceImpl`。应新增一个告警通知服务：

```text
AqyAlarmServiceImpl
  -> saveAlarmRecord(...)
  -> aqyAlarmNotifyService.notify(alarmRecord, alarm, equipment)
       -> 判断是否需要发送
       -> 查询联系人/用户
       -> 写发送日志
       -> 调用 SmsService
       -> 更新发送日志状态
```

建议新增：

- `IAqyAlarmNotifyService`
- `AqyAlarmNotifyServiceImpl`

位置：

- `aqy-main/src/main/java/com/ruoyi/service/IAqyAlarmNotifyService.java`
- `aqy-main/src/main/java/com/ruoyi/service/impl/AqyAlarmNotifyServiceImpl.java`

这样可以让：

- 告警判定负责“是否产生告警”
- 告警通知负责“是否通知、通知谁、怎么通知”
- 短信服务负责“调用短信供应商”

职责更清晰，后续扩展微信/邮件/语音电话也更容易。

### 11.4 “一定地步”的判断规则建议

推荐规则：

#### 规则 1：等级阈值

如果系统约定：

- `1` = 一级 = 最严重
- `2` = 二级
- `3` = 三级

那么“达到二级及以上”可表达为：

```java
alarmLevel <= 2
```

注意：必须在代码、前端、数据库文档中统一“数值越小越严重”的语义。

#### 规则 2：连续次数阈值

用 `notify_threshold_count` 或现有 `alarmCount` 表示：

- 同一项目
- 同一设备
- 同一报警等级
- 未处理状态下
- 最近连续/累计达到 N 次

达到 N 次后才发短信。

需要在 `AqyAlarmRecordMapper` 新增查询：

```java
int countRecentUnresolvedSameAlarm(Long projectId, Long qmtId, Integer alarmLevel, Date sinceTime);
```

或在 `AqyAlarmNotifyServiceImpl` 里查最近未处理报警记录数量。

#### 规则 3：冷却时间

同一设备、同一等级、同一接收人，在 `cooldown_minutes` 内只发送一次。

需要在 `AqyAlarmNotifyLogMapper` 新增查询：

```java
AqyAlarmNotifyLog selectLatestSuccessLog(Long qmtId, Integer alarmLevel, String phone);
```

若最近一次成功发送时间距离当前不足冷却时间，则记录 `SKIPPED`，不再发送。

### 11.5 生产版核心伪代码

```java
public void notify(AqyAlarmRecord record, AqyAlarm alarm, AqyEquipment equipment) {
    if (!shouldNotifyByLevel(alarm.getAlarmLevel())) {
        return;
    }

    if (!reachThresholdCount(record, alarm)) {
        return;
    }

    List<AqyAlarmPerson> receivers = selectReceivers(record.getProjectId(), alarm.getAlarmLevel());

    for (AqyAlarmPerson receiver : receivers) {
        String phone = resolvePhone(receiver);
        if (StringUtils.isBlank(phone)) {
            saveSkippedLog(record, alarm, receiver, "手机号为空");
            continue;
        }

        if (withinCooldown(record, alarm, phone, receiver.getCooldownMinutes())) {
            saveSkippedLog(record, alarm, receiver, "冷却期内不重复发送");
            continue;
        }

        AqyAlarmNotifyLog log = savePendingLog(record, alarm, receiver, phone);

        boolean success = smsService.sendSms(buildTemplateParams(record, alarm, equipment), phone);

        updateLogResult(log, success);
    }
}
```

---

## 12. 最小闭环版具体代码改动清单

如果当前就要最快实现短信通知，建议优先改这些文件。

### 12.1 后端：`AqyAlarmServiceImpl.java`

文件：

- `aqy-main/src/main/java/com/ruoyi/service/impl/AqyAlarmServiceImpl.java`

改动：

1. 引入 `SmsService`：

```java
import com.ruoyi.framework.web.service.SmsService;
```

2. 注入：

```java
@Autowired
private SmsService smsService;
```

3. 将：

```java
sendAlarmMessageToPerson(aqyAlarm.getAlarmLevel());
```

改为：

```java
sendAlarmMessageToPerson(aqyAlarmRecord, aqyAlarm);
```

4. 实现：

```java
private void sendAlarmMessageToPerson(AqyAlarmRecord alarmRecord, AqyAlarm alarm) {
    AqyAlarmPerson query = new AqyAlarmPerson();
    query.setProjectId(alarmRecord.getProjectId());
    query.setAlarmLevel(alarm.getAlarmLevel());
    List<AqyAlarmPerson> people = aqyAlarmPersonService.selectAqyAlarmPersonList(query);

    Map<String, Object> params = new HashMap<>();
    params.put("equipment", alarmRecord.getEqmtName());
    params.put("level", alarmRecord.getAlarmLevel());
    params.put("time", DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", alarmRecord.getRecordTime()));
    params.put("content", alarmRecord.getAlarmContent());

    for (AqyAlarmPerson person : people) {
        if (StringUtils.isNotBlank(person.getContactPersonNumber())) {
            smsService.sendSms(params, person.getContactPersonNumber());
        }
    }
}
```

> 实际模板参数名要以阿里云短信模板为准，上面只是示例。

### 12.2 后端：`SmsServiceImpl.java`

文件：

- `aqy-framework/src/main/java/com/ruoyi/framework/web/service/SmsServiceImpl.java`

建议改动：

- 使用日志替代 `e.printStackTrace()`。
- 记录阿里云响应码和消息。
- 可考虑将失败原因返回给上层，生产版更建议返回一个结果对象而不是 boolean。

简单增强：

```java
log.warn("短信发送失败，phone={}, code={}, message={}", phone, response.getCode(), response.getMessage());
```

### 12.3 配置：`application.yml`

文件：

- `aqy-admin/src/main/resources/application.yml`

建议把 `aliyun.sms.*` 改成环境变量占位，不要提交明文凭据。

### 12.4 前端：无需改动

使用现有页面：

- `报警等级`：配置设备类型、等级、阈值。
- `报警联系人`：配置项目、等级、手机号。

---

## 13. 生产可用版具体代码改动清单

### 13.1 数据库

需要：

1. 给 `aqy_alarm_person` 增加短信开关、冷却时间、阈值次数字段。
2. 新增 `aqy_alarm_notify_log`。
3. 可选：给 `aqy_alarm_person` 增加 `user_id` 关联 `sys_user`。

### 13.2 Domain

修改：

- `AqyAlarmPerson`

新增字段：

- `userId`
- `smsEnabled`
- `notifyThresholdCount`
- `cooldownMinutes`

新增：

- `AqyAlarmNotifyLog`

### 13.3 Mapper

修改：

- `AqyAlarmPersonMapper.java`
- `AqyAlarmPersonMapper.xml`

新增：

- `AqyAlarmNotifyLogMapper.java`
- `AqyAlarmNotifyLogMapper.xml`

### 13.4 Service

新增：

- `IAqyAlarmNotifyService`
- `AqyAlarmNotifyServiceImpl`

修改：

- `AqyAlarmServiceImpl`

把短信逻辑从 `AqyAlarmServiceImpl` 拆出去：

```java
aqyAlarmNotifyService.notify(aqyAlarmRecord, aqyAlarm, aqyEquipment);
```

### 13.5 Controller

如果需要后台查看短信发送情况，新增：

- `AqyAlarmNotifyLogController`

接口：

- `GET /aqy/alarmNotifyLog/list`
- `GET /aqy/alarmNotifyLog/{id}`
- `POST /aqy/alarmNotifyLog/export`

### 13.6 前端

增强：

- `aqy-ui/src/views/aqy/alarmPerson/index.vue`

增加字段：

- 是否启用短信
- 连续/重复次数阈值
- 冷却时间
- 关联系统用户

新增：

- `aqy-ui/src/views/aqy/alarmNotifyLog/index.vue`
- `aqy-ui/src/api/aqy/alarmNotifyLog.js`

### 13.7 菜单与权限

需要在数据库 `sys_menu` 增加权限：

- `aqy:alarmNotifyLog:list`
- `aqy:alarmNotifyLog:query`
- `aqy:alarmNotifyLog:export`

如果修改联系人页面字段，无需新增权限；如果新增通知日志页面，需要新增菜单。

---

## 14. 建议的短信告警端到端流程

生产版推荐流程：

```text
设备上传 MQTT 原始数据
  -> RawDataStrategy 解析
  -> 原始数据入库
  -> AqyAlarmServiceImpl 判断阈值
  -> 更新设备当前报警等级
  -> 插入报警记录 AqyAlarmRecord
  -> AqyAlarmNotifyService 判断是否需要通知
       -> 是否达到配置等级
       -> 是否达到连续/累计次数
       -> 是否冷却期内
       -> 查项目+等级联系人
       -> 生成短信模板参数
       -> 写 PENDING 通知日志
       -> SmsService 发送短信
       -> 更新 SUCCESS/FAILED/SKIPPED
  -> WebSocket 推送前端刷新
```

---

## 15. 推荐优先级

### P0：必须先做

1. 移除/轮换配置文件中的明文敏感凭据。
2. 实现 `sendAlarmMessageToPerson(...)` 调用 `SmsService`。
3. 按 `projectId + alarmLevel` 查询联系人。
4. 修复 `saveAlarmRecord` 空 catch。

### P1：应尽快做

1. 增加短信发送日志表。
2. 增加短信发送失败日志。
3. 增加同设备同等级冷却时间。
4. 明确告警等级数值语义：一级是否永远是最严重。
5. 检查 WY 位移数据链路是否应该恢复。

### P2：后续增强

1. 联系人关联 `sys_user`。
2. 多渠道通知：短信、站内信、微信、邮件。
3. 通知模板按项目/等级/设备类型配置。
4. 失败重试定时任务。
5. 告警升级策略：三级持续 N 次升级二级，二级持续 N 次升级一级。

---

## 16. 关键风险与代码质量建议

### 16.1 空 catch 与异常吞掉

多个位置存在 catch 后只打印或吞掉异常，例如：

- `AqyAlarmServiceImpl#saveAlarmRecord`
- `RawDataStrategy#handle`
- `SmsServiceImpl#sendSms`

建议：

- 使用 SLF4J 统一日志。
- 关键链路失败必须记录上下文。
- 不要让告警保存失败变成静默成功。

### 16.2 告警等级选择逻辑需确认

当前 `AqyAlarmMapper#selectAqyAlarmList`：

```sql
order by eqmt_type, alarm_level asc
```

前端提示：

```text
报警严重程度：一级 > 二级 > 三级
```

如果一级数值为 1 且最严重，那么阈值配置通常应满足：

- 三级阈值最小
- 二级阈值居中
- 一级阈值最大

但是代码有些地方遇到满足条件会继续遍历，有些地方会 `break`。建议统一：

1. 先按阈值从高到低匹配最严重级别；或
2. 明确按 `alarm_level asc/desc` 及阈值关系匹配。

否则可能出现达到一级阈值却被记录成三级或反之。

### 16.3 告警重复问题

当前每次原始数据触发阈值都会插入报警记录。若设备持续超阈值，将持续产生记录。

建议：

- 若设备已经处于同一 `alarmLevel` 且未处理，不重复生成同等级报警记录。
- 或生成记录但短信通知做冷却。

### 16.4 业务表 SQL 缺失

`sql/` 目录中当前只看到若依基础表和 Quartz 表，没有 AQY 业务表建表 SQL。建议补充：

- `aqy_project`
- `aqy_section`
- `aqy_equipment`
- `aqy_equipment_type`
- `aqy_alarm`
- `aqy_alarm_person`
- `aqy_alarm_record`
- 各 raw 表
- MQTT 命令记录表
- 后续通知日志表

否则新环境部署不可复现。

### 16.5 短信模板参数强依赖外部配置

`SmsService` 接收 `Map<String, Object>`，但模板变量名由阿里云短信模板决定。建议在代码中集中定义模板参数生成器，避免各处手写不同 key。

---

## 17. 总结

当前项目已经完成了安全云平台的基础后台、设备接入、原始数据处理和告警记录闭环，但“短信通知”只完成了 SDK 接入，没有完成业务闭环。

如果只是快速实现“达到告警等级后给特定联系人发短信”，最小改造点是：

1. `AqyAlarmServiceImpl` 注入并调用 `SmsService`。
2. `sendAlarmMessageToPerson` 按 `projectId + alarmLevel` 查询 `AqyAlarmPerson`。
3. 使用 `contactPersonNumber` 发送短信。
4. 修复空 catch 和日志。
5. 将短信凭据改成环境变量。

如果要生产可用，建议进一步新增：

1. `AqyAlarmNotifyService`
2. `aqy_alarm_notify_log`
3. 通知冷却、次数阈值、发送审计、失败重试
4. 联系人关联系统用户
5. 通知日志后台页面

这样既能满足“告警到了一定地步给特定用户发送告警短信”，也能避免短信轰炸、失败不可查、跨项目误通知等生产风险。
