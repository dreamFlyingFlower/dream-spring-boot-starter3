# Change Log

## 2026-08-28

### BUG 修复

#### dream-localize3-spring-boot-starter LocalizeServiceImpl 核心方法批量修复

**变更原因:**
1. `getContents(List<String>, String)` 方法为批量从数据库读取国际化的核心方法,存在 4 个严重 BUG:
   - 第一次按 `localizeCode in (...) + fullLang` 批量查询后 if 块**完全为空**,命中结果未被提取,全部进入 fallback 流程
   - fallback 查询只按 `languageId` 过滤,**缺少 `localizeCode` 条件**,返回该语言下任意一条记录的 content,结果完全错误
   - fallback 循环内找到匹配后**未 `break`**,后续更宽泛的语言覆盖前面已匹配的精确内容,违反降级语义
   - fallback 采用 N+1 模式(每个 code 每个 fallback 语言单独 `selectOne`),性能极差
2. `getContent(String, String)` 单体查询对应 3 个 BUG:
   - 第一次精确查询命中 `localizeEntity != null` 时,**不返回 content**,反而在 entity==null 时返回 code,entity!=null 却进入 fallback,首查结果完全丢弃
   - fallback 查询同样缺 `localizeCode` 条件
   - fallback 命中未 `break`,被后续覆盖
3. `getAllMessages(String lang)`: 计算了 `formatLang`(标准化后的语言 tag)但 DB 查询使用的仍是原始 `lang` 参数,缓存 key 和查询条件不一致;`putMap(cacheKey, messageMap, 0, null)` 传 `ttl=0` + `TimeUnit=null`,在 `RedisLocalizeCache` 中会执行 `redisTemplate.expire(key, 0, null)`,可能抛 NPE 或立即过期,缓存形同虚设
4. `buildCacheKey(String lang)` 生成的 Hash key 以 `*` 结尾,被 `getAllMessages` 作为实际 Redis key 使用时不合理,仅 evict 时 pattern 才需要 `*`,两者混用导致 Hash key 含通配符污染
5. `getMesssges` 方法名拼写错误(3 个 `s`),且该 public 方法未在 `LocalizeService` 接口中声明;同时方法内的缓存读/写没有 try/catch,Redis 异常会直接抛出无法走 DB 降级
6. 所有 `e.printStackTrace()` 调用(getAllMessages/clearCache/evictCache 共 4 处)违反日志规范,应使用 log.error;`getMessage` 中 DB 查询异常的 catch 日志内容为 "cache ... from redis",上下文完全错误

**变更内容:**
1. **修复 `getContents(List, String)` (文件 [LocalizeServiceImpl.java](file:///d:/person/repository/dream-spring-boot-starter3/dream-localize3-spring-boot-starter/src/main/java/dream/flying/flower/autoconfigure/localize/service/impl/LocalizeServiceImpl.java))**
   - Step 1: 批量查询后,先将所有 code 默认值设为 code 本身,再将命中且 content 非空的项写入 result,并从 `unresolvedKeys` 中移除
   - Step 2: 仅对 `unresolvedKeys`(未命中集合)进入 fallback;提前短路全命中场景
   - Step 3: 按 fallback chain 顺序构造 `orderedLanguageIds`(保证最精确语言优先)
   - Step 4: 将原先 N+1 次 `selectOne` 改为 **1 次批量 in 查询**:`languageId IN (fallbackIds) AND localizeCode IN (unresolvedKeys)`,构造 `(languageId, localizeCode) -> content` 查找表
   - Step 5: 按 fallback 优先级顺序依次填充 unresolved codes,已填充的 code 跳过(`resolvedSet`),全部解决后立即 `break`,避免覆盖
2. **修复 `getContent(String, String)`**
   - 首次精确查询命中且 content 非空,立即 `return content`;只有完全未命中或 content 为空才走 fallback
   - fallback 查询补充 `.eq(LocalizeEntity::getLocalizeCode, localizeCode)` 条件
3. **修复 `getAllMessages`**
   - DB 查询改用 `formatLang` 而不是原始 `lang`,和缓存 key 对齐
   - `getMap` 结果加上非空判断,空 map 视为缓存未命中再查库
   - `putMap` TTL 从 `0, null` 改为从 `dreamLocalizeProperties.getExpire().getSeconds()` + `TimeUnit.SECONDS` 获取合法值(默认 24h)
4. **修复缓存 key 构造**
   - `buildCacheKey(String lang)` 改为返回 Hash 存储 key,后缀使用 `"ALL"` 而不是 `"*"`
   - 新增 `buildCachePattern(String lang)` 方法专门用于 evict,保留后缀 `"*"`
   - `evictCache` 调用改为使用 `buildCachePattern(lang)`
5. **修复批量接口名拼写与鲁棒性**
   - 重命名 `getMesssges` → `getMessages`,修正 3 个 `s` 的拼写错误
   - 方法内 `localizeCache.get(cacheKeys)` 和 `localizeCache.put(cacheEntries, expire)` 增加 try/catch,Redis 异常时回退 DB 或忽略
   - 在 [LocalizeService.java](file:///d:/person/repository/dream-spring-boot-starter3/dream-localize3-spring-boot-starter/src/main/java/dream/flying/flower/autoconfigure/localize/service/LocalizeService.java) 接口中新增 `getMessages(List<String>, String)` 方法声明
6. **统一日志与异常处理**
   - 把 4 处 `e.printStackTrace()`(getAllMessages L137/L155、clearCache L166、evictCache L178)全部替换为 `log.error` 带上下文参数
   - `getMessage` 中 DB 异常 catch 日志改为正确的 "Localize database query failed" 并附 code/lang
7. **getMessages 缓存优化**:仅当结果 content != localizeCode(确实查到翻译)才写入缓存,避免用 code 本身占缓存空间

**修复结果:**
- `getContents` 首次查询结果正确使用,fallback 查询返回的是对应 code 的内容而不是随机内容;fallback 首次匹配即生效不被覆盖;查询次数从 O(N*M) 降为 2~3 次批量 in,性能大幅提升
- `getContent` 首次精确查询 100% 返回正确 content,不会无故进入 fallback;降级结果也受 `localizeCode` 条件约束,不再乱匹配
- `getAllMessages` 缓存 key 与 DB 查询语言一致,命中率恢复;缓存 24h 正常过期,不再因 TTL=0/null 立即失效
- Hash 缓存 key 不再含通配符,Redis key 空间干净不污染;evict 功能通过独立 pattern 方法正常工作
- 公开的批量国际化查询接口 `getMessages` 名称正确、接口声明完整、Redis 异常不中断业务
- 全模块日志不再有 printStackTrace,异常可通过 logback 按天正确记录;日志内容与异常类型一致,不误导排查
- 所有修改未删除任何原有注释,仅新增必要说明,符合项目注释规则

---

## 2026-08-15

### BUG 修复

#### dream-config3-spring-boot-starter 编译失败修复

**变更原因:**
- dream-config3-spring-boot-starter 编译时报 `ConstStarter`、`ConstConfig.ENABLED_WARMUP`、`ConstConfig.Auto.CONFIG` 等常量找不到
- 根因是本地仓库中的 `dream-framework-constant3` jar 包为旧版本,未包含最新新增的常量定义
- 同时发现 `maven-compiler-plugin` 的 `annotationProcessorPaths` 中 `lombok` 未指定版本,导致 maven-compiler-plugin 3.8.1 解析时报 `The version cannot be empty`

**变更内容:**
- 重新 `mvn clean install -DskipTests` 源码项目 `dream-framework-constant3`,将最新 jar 安装到本地仓库,使 `ConstStarter`、`ConstConfig` 等常量可用
- 修改 [dream-config3-spring-boot-starter/pom.xml](file:///d:/person/repository/dream-spring-boot-starter3/dream-config3-spring-boot-starter/pom.xml): 为 `annotationProcessorPaths` 中的 `lombok` 显式指定版本 `1.18.42` (与 spring-boot 3.5.11 管理的版本一致)

**修复结果:**
- `dream-config3-spring-boot-starter` `mvn compile` 成功,14 个源文件全部编译通过
- 常量引用问题彻底解决,无需在模块内复制常量

**技术细节:**
- `spring-boot-dependencies` 通过 `<scope>import</scope>` 引入,其 `<properties>` 不会传递给子模块,因此子模块的 `annotationProcessorPaths` 无法直接使用 `${lombok.version}`,必须硬编码版本号
- 升级 `dream-framework-constant3` jar 必须使用 `clean install` 强制重新编译,普通 `install` 会因 "all classes are up to date" 跳过编译,导致旧 class 被重新打包

---

## 2026-05-25

### 优化改进

#### SendStatus 常量改为枚举

**变更原因：**
- 使用枚举比接口常量更类型安全，避免魔法值
- 枚举可以包含描述信息，便于理解和维护
- 符合项目规范：枚举以 Status 结尾可以不用添加 Enum 后缀
- 枚举应该放在 `enums` 包中，而不是 `constant` 包中

**变更内容：**
- 将 `SendStatus` 从接口改为枚举类型
- 移动文件位置：`constant/SendStatus.java` → `enums/SendStatus.java`
- 添加 code 和 desc 字段，分别表示状态码和描述
- 提供 `getCode()`、`getDesc()` 和 `fromCode()` 方法
- 更新 `EmailServiceImpl` 中的 import 语句
- 删除空的 `constant` 包

**技术细节：**
- PENDING(1, "待发送")
- SUCCESS(2, "成功")
- FAILED(3, "失败")
- 通过 `fromCode(int code)` 方法可以根据代码获取对应的枚举实例

#### EmailProperties templateDir 默认值修改

**变更原因：**
- 允许用户不配置 templateDir 时使用 Thymeleaf 的默认模板配置
- 提高灵活性，支持多种模板目录配置方式

**变更内容：**
- **EmailProperties**: 将 `templateDir` 默认值从 `"email/templates"` 改为 `null`
- **EmailServiceImpl**: 在 `processTemplate` 方法中添加 null 检查逻辑
  - 当 `templateDir` 不为 null 且不为空时，使用配置的模板目录拼接路径
  - 当 `templateDir` 为 null 或空时，直接使用 templatePath，让 Thymeleaf 使用自己的默认配置
  - 如果模板不存在，Thymeleaf 会抛出 TemplateInputException 异常

**技术细节：**
- templateDir 为 null 时，Thymeleaf 会根据 `spring.thymeleaf.prefix` 等配置查找模板
- 保持向后兼容，如果配置了 templateDir，仍然使用该配置

#### 创建 EmailTemplateService 业务类

**变更原因：**
- EmailTemplateEntity 只有 Mapper，缺少对应的 Service 层
- 按照标准分层架构，每个实体类都应该有对应的 Service 来处理 CRUD 操作
- 分离关注点：EmailService 负责邮件发送，EmailTemplateService 负责模板管理

**变更内容：**
- **创建 EmailTemplateService 接口**：定义模板管理的标准接口
  - `saveTemplate()`: 保存模板
  - `updateTemplate()`: 更新模板
  - `deleteTemplate()`: 删除模板
  - `getTemplateById()`: 根据 ID 查询模板
  - `getTemplateByCode()`: 根据模板编码查询模板
  - `listEnabledTemplates()`: 查询所有启用的模板
  - `listAllTemplates()`: 查询所有模板
  - `enableTemplate()`: 启用模板
  - `disableTemplate()`: 禁用模板

- **创建 EmailTemplateServiceImpl 实现类**：
  - 继承 `ServiceImpl<EmailTemplateMapper, EmailTemplateEntity>`
  - 实现所有接口方法
  - 添加日志记录

- **更新 EmailAutoConfiguration**：注册 EmailTemplateService Bean

#### 数据库表重命名为 sys_email_send_recipient

**变更原因：**
- 统一命名规范，明确表名与业务功能的对应关系
- SQL 建表语句已在 V1.0.0 文件中

**变更内容：**
- 将 `sys_email_recipient` 表及相关功能统一改名为 `sys_email_send_recipient`
- 重命名实体类：`EmailRecipientEntity` → `EmailSendRecipientEntity`
- 重命名 Mapper：`EmailRecipientMapper` → `EmailSendRecipientMapper`
- 重命名 Service：`EmailRecipientService` → `EmailSendRecipientService`
- 重命名 ServiceImpl：`EmailRecipientServiceImpl` → `EmailSendRecipientServiceImpl`
- 更新 `EmailServiceImpl` 和 `EmailAutoConfiguration` 中的所有引用

#### 唯一字段标注和注解规范化

**变更原因：**
- SQL 建表语句中需要标明哪些字段是唯一字段（不创建唯一索引）
- 统一使用基础包的 `dream.flying.flower.db.annotation.Unique` 注解

**变更内容：**
- **SQL 文件修改**：
  - `sys_email_template.template_code`: 标注“(唯一,与tenant_id组合)”
  - `sys_email_send_log.template_code`: 标注“(唯一,与created_at组合)”
  - `sys_email_send_recipient` 三个字段都添加唯一性标注
  
- **实体类修改**：
  - `EmailTemplateEntity`: 移除类级别注解，在 templateCode 和 tenantId 字段添加 `@Unique`
  - `EmailSendLogEntity`: 替换 import，移除类级别注解，在 templateCode 和 createdAt 字段添加 `@Unique`
  - `EmailSendRecipientEntity`: 添加 import，在 sendLogId、email、recipientType 字段添加 `@Unique`

**技术细节：**
- SQL 中不创建唯一索引，只在注释中标明
- 对于组合唯一的情况，在每个组成字段上都添加 `@Unique` 注解
- 所有实体类统一使用基础包注解

#### 实体类继承 AbstractTenantEntity

**变更原因：**
- 统一实体类结构，复用租户、审计等公共字段
- 使用 Lombok 新注解提升代码质量

**变更内容：**
- 三个实体类都继承 `AbstractTenantEntity`
- 移除重复的 id、tenantId、deleted、createdBy、createdAt、updatedBy、updatedAt 字段
- 使用 `@Getter`、`@Setter`、`@ToString`、`@SuperBuilder` 替代 `@Data` 和 `@Builder`
- 添加必要的 import 语句

**技术细节：**
- `AbstractTenantEntity` 来自 `dream.flying.flower.framework.mybatis.plus.entity` 包
- 使用 `@SuperBuilder` 支持继承类的 Builder 模式
- 保留业务特有字段的 `@Unique` 注解

#### dream-email3-spring-boot-starter 邮件发送记录功能重构

**变更原因：**
- 原设计中收件人字段（toEmail, ccEmails, bccEmails）存储在发送记录表中，不符合数据库范式
- 需要支持多个收件人、抄送人、密送人的灵活管理
- EmailService 未采用接口+实现类的标准模式
- 状态值从0开始，不符合项目规范（应从1开始）

**变更内容：**
- **数据库结构调整**：
  - 创建 `sys_email_recipient` 表存储收件人信息
  - 移除 `sys_email_send_log` 表中的 toEmail, ccEmails, bccEmails 字段
  - 通过 recipient_type 字段区分接收人(1)、抄送人(2)、密送人(3)
  
- **新增枚举和常量**：
  - `RecipientType`: 收件人类型枚举（TO=1, CC=2, BCC=3）
  - `SendStatus`: 发送状态常量接口（PENDING=1, SUCCESS=2, FAILED=3）
  
- **实体类调整**：
  - 创建 `EmailRecipientEntity`: 邮件收件人实体类
  - 修改 `EmailSendLogEntity`: 移除收件人字段，更新状态注释
  
- **Service 层重构**：
  - 将 `EmailService` 改为接口
  - 创建 `EmailServiceImpl` 实现类
  - 创建 `EmailRecipientService` 接口和 `EmailRecipientServiceImpl` 实现类
  - 实现批量保存收件人功能
  
- **自动配置更新**：
  - 修改 `EmailAutoConfiguration` 注册新的 Bean

**技术细节：**
- 发送邮件时先插入发送记录获取 ID，再批量插入收件人记录
- 使用 SendStatus 常量替代硬编码的状态值
- 支持单个或多个收件人、抄送人、密送人
- 状态值从1开始：1-待发送, 2-成功, 3-失败

#### Spring Boot 3 自动配置迁移

**变更原因：**
- Spring Boot 3 废弃了 `spring.factories` 中的 `EnableAutoConfiguration` 机制
- 采用新的 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 标准
- 提升启动性能，简化自动配置发现机制

**变更内容：**
- 为所有 starter 模块创建新的自动配置文件
- 删除旧的 `spring.factories` 文件
- 迁移的模块包括：
  - dream-captcha3-spring-boot-starter
  - dream-cryption3-spring-boot-starter
  - dream-dict3-spring-boot-starter
  - dream-email3-spring-boot-starter
  - dream-i18n3-spring-boot-starter
  - dream-logger3-spring-boot-starter
  - dream-mybatis-plus3-spring-boot-starter
  - dream-redis3-spring-boot-starter
  - dream-security3-spring-boot-starter
  - dream-web3-spring-boot-starter

**技术细节：**
- 新文件位置：`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- 文件格式：每行一个自动配置类的全限定名
- 兼容性：Spring Boot 3+ 完全支持，Spring Boot 2.7+ 部分支持

### 新增功能

#### dream-email3-spring-boot-starter（邮件发送 Starter）

**变更原因：**
- 提供基于模板的邮件发送功能
- 从数据库读取邮件模板配置，支持动态管理
- 模板文件存储在服务器指定目录，便于维护和版本控制
- 支持 Thymeleaf 模板引擎和附件发送
- 需要记录邮件发送历史，便于追踪和审计

**变更内容：**
- 创建 `dream-email3-spring-boot-starter` 模块
- 实现核心类：
  - `EmailTemplateEntity`: 邮件模板实体类
  - `EmailSendLogEntity`: 邮件发送记录实体类
  - `EmailTemplateMapper`: 邮件模板 Mapper 接口
  - `EmailSendLogMapper`: 邮件发送记录 Mapper 接口
  - `EmailService`: 邮件服务类，提供模板邮件发送功能
  - `EmailSendLogService`: 邮件发送记录服务接口
  - `EmailSendLogServiceImpl`: 邮件发送记录服务实现类
  - `EmailProperties`: 配置属性类
  - `EmailAutoConfiguration`: Spring Boot 自动配置类
  - `@Unique`: 自定义唯一约束注解（用于逻辑删除场景）
- 数据库迁移脚本：
  - `V1.0.0__Create_email_template_table.sql`: 创建邮件模板表
  - `V1.0.1__Create_email_send_log_table.sql`: 创建邮件发送记录表
- Spring Boot 自动配置声明：`spring.factories`

**特性：**
- 从数据库读取邮件模板配置（template_code, template_path, subject等）
- 模板文件存储在服务器指定目录（默认 email/templates）
- 支持 Thymeleaf 模板引擎渲染 HTML 内容
- 支持普通邮件和带附件邮件发送
- 可配置默认发件人邮箱和名称
- 自动记录每次邮件发送的结果（成功/失败）
- 记录发送时间、错误信息、附件数量等详细信息
- 自动创建 `sys_email_template` 和 `sys_email_send_log` 表
- 使用 @Unique 注解标记逻辑删除场景下的唯一字段组合

**配置项：**
```yaml
dream:
  email:
    enabled: true                    # 是否启用
    template-dir: email/templates   # 模板目录路径
    default-from-email: noreply@example.com  # 默认发件人邮箱
    default-from-name: System       # 默认发件人名称

spring:
  mail:
    host: smtp.example.com          # SMTP服务器
    port: 587                       # SMTP端口
    username: user@example.com      # SMTP用户名
    password: your-password         # SMTP密码
```

**使用示例：**
```java
@Autowired
private EmailService emailService;

// 发送验证码邮件
Map<String, Object> variables = new HashMap<>();
variables.put("username", "张三");
variables.put("code", "123456");
emailService.sendEmail("user@example.com", "verification_code", variables);

// 发送带附件的通知邮件
FileSystemResource attachment = new FileSystemResource(new File("/path/to/report.pdf"));
emailService.sendEmailWithAttachments("user@example.com", "monthly_report", variables, attachment);
```

---

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
