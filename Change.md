# Change Log

## 2026-05-18

### 新增功能

#### 1. dream-i18n3-spring-boot-starter（国际化 Starter）

**变更原因：**
- 提供通用的国际化消息管理功能，支持多语言切换
- 基于数据库存储国际化消息，便于动态管理
- 集成 Redis 缓存优化性能

**变更内容：**
- 创建 `dream-i18n3-spring-boot-starter` 模块
- 实现核心类：
  - `LocalizationEntity`: 国际化消息实体类
  - `LocalizationMapper`: MyBatis Mapper 接口
  - `I18nService`: 国际化服务类，提供消息查询和缓存管理
  - `I18nProperties`: 配置属性类
  - `I18nAutoConfiguration`: Spring Boot 自动配置类
- 数据库迁移脚本：`V1.0.0__Create_localization_table.sql`
- Spring Boot 自动配置声明：`spring.factories`

**特性：**
- 支持通过 URL 参数 `?lang=en_US` 切换语言
- 默认语言为简体中文 (zh_CN)
- 缓存过期时间可配置（默认 24 小时）
- 自动创建 `sys_localization` 表
- 提供 `MessageSource` Bean，与 Spring 框架无缝集成

**配置项：**
```yaml
dream:
  i18n:
    enabled: true                    # 是否启用
    default-locale: zh_CN           # 默认语言
    cache-expire-hours: 24          # 缓存过期时间（小时）
```

---

#### 2. dream-dict3-spring-boot-starter（字典管理 Starter）

**变更原因：**
- 提供通用的字典和字典项管理功能
- 支持数据字典的集中管理和维护
- 集成 Redis 缓存预热和定时刷新机制

**变更内容：**
- 创建 `dream-dict3-spring-boot-starter` 模块
- 实现核心类：
  - `DictEntity`: 字典实体类
  - `DictItemEntity`: 字典项实体类
  - `DictMapper`: 字典 Mapper 接口
  - `DictItemMapper`: 字典项 Mapper 接口
  - `DictService`: 字典服务类
  - `DictItemService`: 字典项服务类
  - `DictCacheWarmupService`: 缓存预热服务，支持启动时预热和定时刷新
  - `DictProperties`: 配置属性类
  - `DictAutoConfiguration`: Spring Boot 自动配置类
- 数据库迁移脚本：`V1.0.0__Create_dict_tables.sql`
- Spring Boot 自动配置声明：`spring.factories`

**特性：**
- 支持字典和字典项的 CRUD 操作
- 启动时自动预热字典缓存到 Redis
- 定时任务每天凌晨 2 点刷新缓存
- 缓存过期时间可配置（默认 12 小时）
- 自动创建 `sys_dict` 和 `sys_dict_item` 表

**配置项：**
```yaml
dream:
  dict:
    enabled: true                    # 是否启用
    cache-expire-hours: 12          # 缓存过期时间（小时）
    warmup-enabled: true            # 是否启用缓存预热
```

---

### 技术要点

1. **遵循 Spring Boot 3 自动配置标准**
   - 使用 `@AutoConfiguration` 注解
   - 通过 `spring.factories` 声明自动配置类
   - 使用 `@ConditionalOnProperty` 控制功能启用/禁用

2. **数据库版本管理**
   - 使用 Flyway 进行数据库迁移
   - 自动创建所需的表结构

3. **缓存优化**
   - 集成 Redis 缓存提升性能
   - 字典模块支持缓存预热和定时刷新
   - 优雅处理 Redis 连接失败场景

4. **代码规范**
   - 所有注释使用英文标点符号
   - 方法长度控制在 100 行以内
   - 使用常量代替魔法值

5. **依赖管理**
   - 依赖 `dream-mybatis-plus3-spring-boot-starter` 提供数据库访问能力
   - 依赖 `dream-redis3-spring-boot-starter` 提供缓存能力
   - 依赖 `spring-boot-starter-web` 提供 Web 支持

---

### 编译验证

两个模块均已通过编译测试：
```bash
mvn clean compile -q
```

编译结果：成功 ✓

---

### 使用说明

在项目中使用这两个 starter，只需在 `pom.xml` 中添加依赖：

```xml
<!-- 国际化 -->
<dependency>
    <groupId>dream.flying.flower</groupId>
    <artifactId>dream-i18n3-spring-boot-starter</artifactId>
    <version>0.0.1</version>
</dependency>

<!-- 字典管理 -->
<dependency>
    <groupId>dream.flying.flower</groupId>
    <artifactId>dream-dict3-spring-boot-starter</artifactId>
    <version>0.0.1</version>
</dependency>
```

然后在 `application.yml` 中配置数据库连接即可使用。
