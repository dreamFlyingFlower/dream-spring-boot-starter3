# ReadMe



# dream-framework



# 介绍



* 利用SpringBoot3+JDK17版本开发的自动配置项目



# 软件架构




* dream-spring-boot-starter-cryption3:加密相关自动配置
* dream-spring-boot-starter-dict3:字典管理相关自动配置
* dream-spring-boot-starter-email3:邮件发送相关自动配置
* dream-spring-boot-starter-excel3:excel相关自动配置
* dream-spring-boot-starter-i18n3:国际化相关自动配置
* dream-spring-boot-starter-logger3:日志相关自动配置
* dream-spring-boot-starter-redis3:redis相关自动配置
* dream-spring-boot-starter-security3:安全相关自动配置
* dream-spring-boot-starter-storage3:存储相关自动配置
* dream-spring-boot-starter-web3:web相关自动配置




# 安装教程



* 直接引入即可使用



# cryption



## 概述



* 对第三方传递的数据进行解密
* 对传递给第三方的数据进行加密



## 简单使用



### 加密



* 引入当前starter
* 在需要加密的方法所属类上添加`CryptionController`
* 修改全局加密配置文件`EncryptResponseProperties`
* 在需要加密的方法上添加`EncryptResponse`



#### EncryptResponseProperties



* `secretKey`:全局加密密钥,默认1234567890qazwsx,长度必须是16的倍数
* `cryptType`:加密类型,默认AES
* `encryptClass`:需要加密的类型,如果为空,除void之外都加密



#### EncryptResponse



* `EncryptResponse#value()`:加密密钥,优先级高于`EncryptResponseProperties#secretKey`
* `EncryptResponse#cryptType()`:加密类型,优先级高于`EncryptResponseProperties#cryptType`



#### 注意



* 被`EncryptResponse`修饰的方法必须添加`org.springframework.web.bind.annotation.ResponseBody`或类上包含了该注解
* 被`EncryptResponse`修饰的方法若无返回值,不加密
* `EncryptResponse`会将方法返回值全部加密,而不会对单个数据加密



### 解密



* 引入当前starter
* 在需要解密的方法所属类上添加`CryptionController`
* 修改全局加密配置文件`DecryptRequestProperties`
* 在需要解密的方法上添加`DecryptRequest`



#### DecryptRequestProperties



* `secretKey`:全局加密密钥,默认1234567890qazwsx,长度必须是16的倍数
* `cryptType`:加密类型,默认AES



#### DecryptRequest



* `DecryptRequest#value()`:解密密钥,优先级高于`DecryptRequestProperties#secretKey`
* `DecryptRequest#cryptType()`:解密类型,优先级高于`DecryptRequestProperties#cryptType`



#### 注意



* 被`DecryptRequest`修饰的方法参数必须添加`org.springframework.web.bind.annotation.RequestBody`
* `DecryptRequest`会将参数全部解密,而不会对单个数据解密



## 相关类



* `CryptionController`:标识注解,在需要加密的方法所属的类上添加
* `EncryptResponse`:加密注解,在需要加密的方法上添加
* `EncryptResponseProperties`:全局加密配置
* `DecryptRequest`:解密注解,在需要解密的方法上添加
* `DecryptRequestProperties`:全局解密配置



# dict



## 概述



* 提供字典和字典项管理功能
* 支持 Redis 缓存预热和定时刷新
* 自动创建数据库表结构



## 配置项



* `dream.dict.enabled`:是否启用字典功能,默认true
* `dream.dict.cache-expire-hours`:缓存过期时间(小时),默认12
* `dream.dict.warmup-enabled`:是否启用缓存预热,默认true



## 使用方式



* 引入当前starter
* 配置数据库连接
* 系统启动时自动创建 sys_dict 和 sys_dict_item 表
* 注入 DictService 和 DictItemService 使用



# email



## 概述



* 提供基于模板的邮件发送功能
* 从数据库读取邮件模板配置
* 模板文件存储在服务器指定目录
* 支持 Thymeleaf 模板引擎
* 支持附件发送



## 配置项



* `dream.email.enabled`:是否启用邮件功能,默认true
* `dream.email.template-dir`:模板目录路径,默认email/templates
* `dream.email.default-from-email`:默认发件人邮箱
* `dream.email.default-from-name`:默认发件人名称
* `spring.mail.host`:SMTP服务器地址
* `spring.mail.port`:SMTP服务器端口
* `spring.mail.username`:SMTP用户名
* `spring.mail.password`:SMTP密码



## 使用方式



* 引入当前starter
* 配置数据库连接和邮件服务器信息
* 系统启动时自动创建 sys_email_template 表
* 在数据库中配置邮件模板(template_code, template_path, subject等)
* 将Thymeleaf模板文件放在配置的模板目录下
* 注入 EmailService 使用



### 示例代码



```java
@Autowired
private EmailService emailService;

// 发送普通邮件
Map<String, Object> variables = new HashMap<>();
variables.put("username", "张三");
variables.put("code", "123456");
emailService.sendEmail("user@example.com", "verification_code", variables);

// 发送带附件的邮件
FileSystemResource attachment = new FileSystemResource(new File("/path/to/file.pdf"));
emailService.sendEmailWithAttachments("user@example.com", "notification", variables, attachment);
```



# i18n (dream-localize3-spring-boot-starter)



## 概述



* 提供国际化消息管理功能
* 支持多语言切换
* 基于数据库(system_language / sys_localize) + Flyway 建表
* 支持 Redis 缓存优化性能
* 支持 Session / Cookie / Accept-Header / Fixed 4 种 Locale 解析策略切换
* 提供本地缓存写操作自动 evict 钩子 + 可选手动强制清缓存 REST API



## 依赖要求



### 1. MyBatis-Plus 逻辑删除配置

当前 starter 中的 `sys_language`、`sys_localize` 表都使用了 `deleted TINYINT UNSIGNED` 作为逻辑删除字段,但本 starter **不强制要求显式过滤 deleted=0**。逻辑删除行为由引用 starter 的宿主工程在 MyBatis-Plus 全局配置中完成。建议宿主工程添加如下 MP 全局逻辑删除配置(示例):

```yaml
mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

启用后,所有通过 MP BaseMapper / AbstractServiceImpl 生成的 SQL 都会自动追加 `deleted=0`,不需要每个查询手写,已在 Service 层移除所有显式 deleted 条件。



## 配置项(dream.localize.*)



* `dream.localize.enabled`:是否启用国际化自动配置,默认 true
* `dream.localize.enabled-endpoint`:是否启用 LocalizeEndpoint / LanguageEndpoint CRUD + 前端联动接口,默认 true
* `dream.localize.enabled-cache-endpoint`:是否启用国际化缓存强制清理 REST API,默认 **false**(安全考虑,生产需手动开启)
* `dream.localize.default-locale`:默认语言,推荐使用 BCP-47 格式 `zh-CN`,同时兼容 `zh_CN`
* `dream.localize.supported-locales`:支持的语言列表,默认 `[zh-CN, en-US]`,Accept-Header 解析器严格匹配
* `dream.localize.locale-resolver`:语言解析策略,枚举类型,可选 `SESSION` / `COOKIE` / `HEADER`(默认,优先使用,基于自定义命名的请求头,key 见下 header-name)/ `ACCEPT_HEADER`(HTTP 标准 Accept-Language 加权列表,与 HEADER 不同) / `FIXED`;yml 中大小写不敏感,IDEA 通过 spring-boot-configuration-processor 可直接提示枚举值;HEADER 模式对 REST/JWT 无会话场景最友好
* `dream.localize.header-name`:当 `locale-resolver=HEADER` 时使用的自定义请求头 key 名,默认 `X-App-Language`,前端在每次请求时将当前语言标签(如 `zh-CN`)写入此 header;若该 header 未传或解析失败,自动回退到 Servlet 容器对 Accept-Language 的解析,再失败用 defaultLocale
* `dream.localize.locale-change-param-name`:URL 参数切换语言时的参数名,默认 `lang`(仅 SESSION/COOKIE 模式会响应此参数切换语言,HEADER/ACCEPT_HEADER/FIXED 为只读模式,不支持通过 param 切换)
* `dream.localize.ignore-invalid-locale`:非法 locale 输入是否忽略回退默认,默认 true
* `dream.localize.cookie-name`:Cookie 解析器的 cookie 名,默认 `dream_lang`
* `dream.localize.cookie-path`:Cookie 路径,默认 `/`
* `dream.localize.cookie-max-age`:Cookie 过期时间,默认 7d
* `dream.localize.cookie-http-only`:Cookie HttpOnly 标志,默认 true
* `dream.localize.expire`:国际化 Redis 缓存过期时间,默认 24h
* `dream.localize.enabled-api`:是否启用 knife4j/springdoc API 文档分组,默认 true
* `dream.localize.api-group / api-group-name / api-package-scan`:springdoc GroupedOpenApi 分组参数



## 使用方式



### 1. 引入 starter

```xml
<dependency>
  <groupId>dream.flying.flower</groupId>
  <artifactId>dream-localize3-spring-boot-starter</artifactId>
</dependency>
```

### 2. 数据库与缓存

* 系统启动时 Flyway 自动执行 `V1.0.0__Create_localize_table.sql`(建表) + `V1.0.1__Fix_localize_constraints.sql`(UNIQUE/INDEX)
* 如已存在重复数据,V1.0.1 会失败,请先清理脏数据(脚本头注释附检测 SQL)

### 3. 后端接口使用

* `MessageSource.getMessage(code, args, locale)`:Spring 标准 MessageSource API,DB 驱动
* 注入 `LocalizeService`:直接使用 `getMessage / getAllMessages / getMessages` 等 API

### 4. 前端联动接口(启用 enabled-endpoint=true 时)

* 语言下拉:`GET /language/list?enabled=1` → 启用语言传 `enabled=1`(由 LanguageQuery.enabled 接收,传 null/不传返回所有)
* 启动一次性全量拉取词条:`GET /localize/messages?lang=zh-CN`(lang 不传则用当前 LocaleContextHolder 语言)
* 懒加载模块批量拉取:`POST /localize/messages/batch?lang=xx` Body: `["code1","code2"]`

### 5. 缓存管理接口(启用 enabled-cache-endpoint=true 时)

* 全清空:`DELETE /localize-cache`
* 单语言清空:`DELETE /localize-cache/zh-CN`

### 6. 语言切换

* Session / Cookie 解析器:通过 URL `?lang=zh-CN` 切换(LocaleChangeInterceptor 自动拦截,参数名由 locale-change-param-name 配置)
* Header 解析器(默认):不支持 URL 参数切换。前端把 `zh-CN` 等语言标签写入配置的 `header-name` 请求头(默认 `X-App-Language`),每次请求带这个 header 值即可
* Accept-Header 解析器:完全由浏览器/HTTP 客户端的标准 `Accept-Language` 请求头决定
* Fixed:固定语言,无法切换

### 备注:关于 enabled 过滤

`sys_language.enabled` 作为业务启用状态由前端通过 `LanguageQuery.enabled` 透传查询;但国际化 fallback 匹配链路中,只有 `enabled=1` 的语言会被纳入回退链,这是 Service 内部规则,由框架在 fallback 查询阶段自动过滤。此时若把某语言禁用,管理端仍可查询管理,但实时 fallback 不会再匹配它,保证管理员可控。

### 常见问题

**Q1:启动报错 `BeanDefinitionOverrideException: Invalid bean definition with name 'localeResolver' / 'messageSource' / 'localeChangeInterceptor' defined in class path resource [...] already registered in ...`**

**原因**: Spring Boot 通过 `MessageSourceAutoConfiguration`(默认注册 `messageSource`)和 `WebMvcAutoConfiguration`(默认注册 `localeResolver`),以及宿主工程可能手写的同名 `@Bean`,当三方同时声明时由于 Spring Boot 3.x 默认 `spring.main.allow-bean-definition-overriding=false`,会直接抛出冲突。

**解决方案:** starter 侧已按 Spring Boot 官方最佳实践做了三层兜底修复(无需宿主改任何代码),冲突应当自动消失。如果仍报冲突,按以下优先级排查:

1. **优先推荐:** 宿主工程删除自己手写的 `@Bean LocaleResolver` / `@Bean MessageSource` / `@Bean LocaleChangeInterceptor`,让我方 starter 的策略 Bean 生效;我方 starter 的 LocaleResolver 支持 5 种策略切换,且 MessageSource 是**数据库驱动 + Redis 缓存**实现,覆盖了默认 ResourceBundle/Properties 方案,功能更丰富
2. **若宿主确实需要自定义 LocaleResolver/MessageSource**(例如嵌入特殊 SaaS 多租户逻辑):保留宿主的同名 `@Bean` 定义即可。我方 starter 已为这 3 个 Bean 全部加上 `@ConditionalOnMissingBean(name="xxx")` 条件装配 —— 检测到宿主工程已存在同名 Bean,我方会自动跳过装配,不再冲突
3. **极端情况 2 份 starter 同时被引入:** 保证 Maven 依赖中只有一个 `dream-localize3-spring-boot-starter` 实例,检查 `dependencyManagement` 版本冲突,或临时 `application.yml` 加 `spring.main.allow-bean-definition-overriding=true` 应急(不推荐长期开启)

**装配顺序保证:** 我方 `LocalizeAutoConfiguration` 类声明了 `@AutoConfiguration(before = { MessageSourceAutoConfiguration.class, WebMvcAutoConfiguration.class })`,确保 starter 先注册 `messageSource` / `localeResolver`,Spring Boot 默认的自动装配会因 `@ConditionalOnMissingBean(name=xxx)` 检测到我方 Bean 已存在而**主动跳过**,不会产生冲突。我方 `messageSource` Bean 同时加了 `@Primary`,保证在宿主工程显式使用 `@Qualifier("messageSource")` 或框架查找 `MessageSource` 类型时,DB 驱动实现优先注入。



# excel



# redis



# security



# storage



# web



## 相关类



* `AsyncExecutorAutoConfiguration`:该类中的`defaultAsyncTaskExecutor()`在SpringBoot2中可以使用,自定义异步任务等需要使用线程池的配置.SpringBoot3中已由`TaskExecutionAutoConfiguration`,`TaskExecutorConfigurations`,`TaskExecutorConfiguration`,`SimpleAsyncTaskExecutorBuilderConfiguration`等相关类进行优化,直接在配置文件中配置`spring.task.execution`即可.该自动配置删除



