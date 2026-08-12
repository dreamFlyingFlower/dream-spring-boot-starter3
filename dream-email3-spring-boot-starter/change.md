# dream-email3-spring-boot-starter 变更记录

## 2026-08-12 EmailSendLog/EmailSendRecipient 补全 Query/VO/Convert 并对齐 EmailTemplate 模式

### 变更原因

`EmailSendLogEntity` 与 `EmailSendRecipientEntity` 的相关类未与 `EmailTemplate*` 模式对齐:

- 缺少 `Query`, `VO`, `Convert` 三层类, 无法支持统一的查询/视图/转换链路.
- `Mapper` 仅继承 `BaseMapper<Entity>`, `ServiceImpl` 仅继承 `ServiceImpl<Mapper, Entity>`, 未接入框架的 `BaseMappers` 与 `AbstractServiceImpl`, 导致通用 CRUD 能力缺失.
- `EmailAutoConfiguration` 未声明 `EmailSendLogService` 的 `@Bean` 方法 (autoconfigure 模块不走组件扫描, `@Service` 不会生效), 存在潜在的 Bean 缺失问题.
- 多个类的类级注释为英文, 缺少 git 地址, `@date` 仅有日期没有时分秒.

### 变更内容

1. 新增 6 个类:
   - `query/EmailSendLogQuery.java`
   - `query/EmailSendRecipientQuery.java`
   - `vo/EmailSendLogVO.java`
   - `vo/EmailSendRecipientVO.java`
   - `convert/EmailSendLogConvert.java`
   - `convert/EmailSendRecipientConvert.java`

2. 修改 `mapper/EmailSendLogMapper.java`, `mapper/EmailSendRecipientMapper.java`: 继承由 `BaseMapper` 改为 `BaseMappers<Entity, VO, Query>`.

3. 修改 `service/impl/EmailSendLogServiceImpl.java`, `service/impl/EmailSendRecipientServiceImpl.java`: 继承由 `ServiceImpl<Mapper, Entity>` 改为 `AbstractServiceImpl<Entity, VO, Query, Convert, Mapper>`, 移除注入的 mapper 字段, 改用基类 `baseMapper`.

4. 修改 `EmailAutoConfiguration.java`:
   - 新增 `emailSendLogService()` Bean 方法, 修复此前缺失的 `EmailSendLogService` Bean.
   - `emailSendRecipientService()` 移除 mapper 参数, 改为无参构造, 与 `AbstractServiceImpl` 模式一致.
   - 移除不再使用的 `EmailSendRecipientMapper` 导入.

5. 注释统一规范 (涉及以上所有类及 `entity/EmailSendLogEntity`, `entity/EmailSendRecipientEntity`, `service/EmailSendLogService`, `service/EmailSendRecipientService`):
   - 类级描述改为中文.
   - 补充 `@git {@link https://github.com/dreamFlyingFlower}`.
   - `@date` 仅日期的补齐时分秒 (原 `2026-05-25` 补为 `2026-05-25 13:25:57`); 新增类使用 `2026-08-12 14:43:04`.
   - `@Schema` 注解与字段 Javadoc 注释翻译为中文.

### 修复结果

- `EmailSendLog*` 与 `EmailSendRecipient*` 的 Entity/Query/VO/Convert/Mapper/Service/ServiceImpl 七层结构与 `EmailTemplate*` 一致.
- `EmailAutoConfiguration` 完整声明 `EmailService`, `EmailSendLogService`, `EmailSendRecipientService`, `EmailTemplateService` 四个 Bean, 修复了 `EmailSendLogService` 缺失 Bean 的潜在问题.
- `EmailServiceImpl` 调用的 `saveLog`/`updateLogStatus`/`batchSave`/`findBySendLogId` 方法签名保持不变, 不影响现有调用.
- 类注释统一为中文 + 作者 + 时分秒日期 + git 地址.
