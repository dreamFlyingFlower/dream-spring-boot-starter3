# dream-config3-spring-boot-starter 实施计划

## 1. 项目概述

参照 `dream-dict3-spring-boot-starter` 的架构模式,在 `dream-spring-boot-starter3` 下新建 `dream-config3-spring-boot-starter` 模块。

**主要功能**:存储可配置项到数据库,替代部分 `application.yml` 的作用。支持通过数据库管理系统配置参数,实现配置的中心化管理和动态刷新。

## 2. 数据库表设计

### sys\_config 配置表

| 字段            | 类型               | 说明                             |
| ------------- | ---------------- | ------------------------------ |
| id            | BIGINT UNSIGNED  | 主键                             |
| config\_key   | VARCHAR(128)     | 配置键(唯一)                        |
| config\_value | TEXT             | 配置值                            |
| config\_type  | VARCHAR(32)      | 值类型:string/number/boolean/json |
| category      | VARCHAR(64)      | 配置分类                           |
| description   | VARCHAR(512)     | 配置描述                           |
| sort\_index   | INT              | 排序                             |
| status        | INT              | 状态:0-禁用,1-启用                   |
| remark        | VARCHAR(256)     | 备注                             |
| tenant\_id    | BIGINT UNSIGNED  | 租户ID                           |
| created\_by   | BIGINT UNSIGNED  | 创建人                            |
| created\_at   | DATETIME         | 创建时间                           |
| updated\_by   | BIGINT UNSIGNED  | 更新人                            |
| updated\_at   | DATETIME         | 更新时间                           |
| deleted       | TINYINT UNSIGNED | 删除标志                           |

## 3. 新建文件清单

### 3.1 Maven 配置

* `dream-config3-spring-boot-starter/pom.xml` — 参照 dict 模块的 pom.xml

### 3.2 Java 源码

* `src/main/java/dream/flying/flower/autoconfigure/config/ConfigAutoConfiguration.java` — 自动配置类

* `src/main/java/dream/flying/flower/autoconfigure/config/properties/DreamConfigProperties.java` — 配置属性

* `src/main/java/dream/flying/flower/autoconfigure/config/constant/ConstConfig.java` — 模块常量

* `src/main/java/dream/flying/flower/autoconfigure/config/entity/ConfigEntity.java` — 配置实体

* `src/main/java/dream/flying/flower/autoconfigure/config/mapper/ConfigMapper.java` — Mapper 接口

* `src/main/java/dream/flying/flower/autoconfigure/config/service/ConfigService.java` — Service 接口

* `src/main/java/dream/flying/flower/autoconfigure/config/service/impl/ConfigServiceImpl.java` — Service 实现

* `src/main/java/dream/flying/flower/autoconfigure/config/convert/ConfigConvert.java` — 实体/VO 转换器

* `src/main/java/dream/flying/flower/autoconfigure/config/query/ConfigQuery.java` — 查询参数

* `src/main/java/dream/flying/flower/autoconfigure/config/vo/ConfigVO.java` — VO 类

* `src/main/java/dream/flying/flower/autoconfigure/config/endpoint/ConfigEndpoint.java` — REST 端点

* `src/main/java/dream/flying/flower/autoconfigure/config/cache/ConfigCacheWarmupService.java` — 缓存预热服务

### 3.3 资源文件

* `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` — 自动配置导入

* `src/main/resources/db/migration/V1.0.0__Create_config_tables.sql` — 建表 SQL

### 3.4 父项目修改

* 修改 `dream-spring-boot-starter3/pom.xml` — 在 modules 中添加 `dream-config3-spring-boot-starter`

## 4. 核心实现思路

### 4.1 配置加载流程

1. 应用启动时,`ConfigAutoConfiguration` 自动装配
2. `ConfigCacheWarmupService` 作为 `CommandLineRunner` 预热配置缓存到 Redis
3. 通过 `@ConfigurationProperties` 绑定配置时可从数据库获取值
4. 提供 REST API 动态管理配置项,修改后自动刷新缓存

### 4.2 配置获取方式

* **方式一**:通过 `ConfigService.getByKey(key)` 从 Redis 缓存获取配置值

* **方式二**:\*\*(后续扩展)\*\*实现 `EnvironmentPostProcessor` 或 `BeanFactoryPostProcessor`,将数据库配置注入 Spring Environment,替代 application.yml 中的部分配置项

### 4.3 缓存策略

* 启动时全量加载配置到 Redis

* 定时任务(每小时)刷新缓存

* 通过 API 修改配置时主动刷新对应缓存

* 缓存 key 格式:`config:{configKey}`

### 4.4 配置属性(DreamConfigProperties)

* `enabled`:是否启用 config 模块,默认 true

* `enabledEndpoint`:是否启用 REST 端点,默认 true

* `cacheExpireHours`:缓存过期时间(小时),默认 24

* `warmupEnabled`:是否启动时预热缓存,默认 true

## 5. 依赖项

* `dream-mybatis-plus3-spring-boot-starter` — 数据访问

* `dream-redis3-spring-boot-starter` — 缓存支持

* `spring-boot-starter-web` — Web 支持

* `flyway-core` / `flyway-mysql` — 数据库迁移

* `lombok` — 简化代码

* `spring-boot-configuration-processor` — 配置元数据生成

## 6. 风险与注意事项

1. **常量定义**:由于 `ConstConfig.Auto.CONFIG` 在框架层定义,本模块使用本地 `ConstConfig` 接口定义常量,避免跨模块依赖问题
2. **配置生效时机**:数据库配置加载需在 Spring 上下文初始化之后,因此不能完全替代 `application.yml` 中的数据源等基础配置,适合替代业务配置
3. **与现有 GlobalConfig 的关系**:此模块是通用 starter,与 cummins-loto 项目中的 `GlobalConfigEntity` 无直接关系
4. **多租户支持**:实体继承 `AbstractTenantEntity`,自动支持多租户

## 7. 后续扩展方向(本次不实现)

* 实现 `PropertySourceLocator` 将数据库配置注入 Spring Environment

* 支持配置变更通知(类似 Nacos 的配置推送)

* 支持配置分组和命名空间

* 支持配置历史版本和回滚

