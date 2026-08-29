# dream-localize3-spring-boot-starter 修复计划

## 一、代码调研结论

### 核心文件结构

* `LocalizeServiceImpl.java` - 国际化服务实现,主要业务逻辑所在(L89-L334)

* `LocalizeService.java` - 服务接口定义

* `LocalizeHelpers.java` - 语言解析、降级链构建工具

* `LocalizeCache.java` / `RedisLocalizeCache.java` - Redis 缓存接口与实现

* `LocalizeEntity.java` - `sys_localize` 表实体(localizeCode, languageId, fullLang, content)

* `LanguageEntity.java` - `sys_language` 表实体(lang, fullLang, enabled)

### 已发现的问题列表

#### 1. `getContents(List<String>, String)` 方法 (LocalizeServiceImpl.java L288-325) - **用户指定修复**

* **Bug A (空 if 块)**: L292-294,首次批量查询 `localizeEntitys` 成功后,if 块内为空,完全没有提取命中的结果,导致所有结果都进入 fallback 流程,性能极差且结果不正确。

* **Bug B (降级查询缺条件)**: L316,fallback 查询只按 `languageId` 过滤,**缺少** **`localizeCode`** **条件**,会查出该语言下任意一条记录的 content,而不是当前循环 `localizeCode` 对应的值,返回完全错误的数据。

* **Bug C (无 break 导致覆盖)**: L318-319,fallback 链中找到匹配后没有 `break`,后续更宽泛的语言(优先级更低)会覆盖前面已匹配到的更精确内容,违反降级语义。

* **Bug D (重复查询)**: 首次命中的 code 仍然会进入 fallback 循环再次查库,完全多余。

#### 2. `getContent(String, String)` 方法 (L89-124)

* **Bug A (首查结果不返回)**: L90-97,首次按 `localizeCode + fullLang` 查到 `localizeEntity` 后,不直接返回 content,反而在 entity==null 时直接返回 code;entity!=null 时却落入 fallback 循环,**首查结果被完全忽略**,正确值根本不返回。

* **Bug B (降级查询缺条件)**: 同 `getContents`,L115-116 只按 `languageId` 查询,缺 `localizeCode`。

* **Bug C (无 break)**: L117-119,fallback 命中后不 break,被后续覆盖。

#### 3. `getMesssges` 方法名拼写错误 (L253)

* 方法名 `getMesssges` 含 3 个 `s`,应为 `getMessages`。另外该方法未在 `LocalizeService` 接口中声明,需要确认是否公开。

#### 4. `getAllMessages(String)` 方法 (L127-159)

* **Bug A (参数不一致)**: L142 计算了 `formatLang` 标准化后的语言,但实际 DB 查询用的仍是原始 `lang` 参数,导致缓存 key 与 DB 查询条件不匹配,缓存命中率低甚至 key/data 不对应。

* **Bug B (缓存过期参数非法)**: L151 调用 `putMap(cacheKey, messageMap, 0, null)`,`ttl=0` + `TimeUnit=null` 在 `RedisLocalizeCache.putMap` L93-95 中会执行 `redisTemplate.expire(key, 0, null)`,可能抛 NPE 或导致立即过期,缓存立刻失效。

#### 5. `buildCacheKey(String lang)` (L327-329)

* 生成的 key 以 `*` 结尾: `dream:localize:{lang}:*`。

  * 在 `evictCache` L175 中作为 pattern 使用是正确的。

  * 但在 `getAllMessages` L130/L151 中作为 **Hash 真实 key** 使用是错误的,`*` 不能出现在实际存储的 Redis key 名中,会造成 key 污染且和 pattern 语义冲突。

* 需要拆分为两个方法:一个生成 Hash 存储 key(不含 `*`),一个生成 evict pattern(含 `*`)。

#### 6. `getMessage` 日志错误 (L82-85)

* catch 块捕获的是 DB 查询异常,但日志内容写的是 "cache ... query failed from redis",上下文完全错误,误导排查。

#### 7. `LocalizeMessageSource.resolveCodeWithoutArguments` (L25)

* 传的是 `locale.toString()`(格式如 `zh_CN`),而 `getMessage` 内会调用 `LocalizeHelpers.parse(lang).toLanguageTag()`。虽然 parse 能兼容下划线,但 `CustomMessageSource.getMessage` L115 也使用 `locale.getLanguage() + "_" + locale.getCountry()`,保持一致即可,暂不修改。

***

## 二、需要修改的文件

| 文件                                                                                                                                   | 修改内容                                                                                 |
| ------------------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------ |
| `dream-localize3-spring-boot-starter/src/main/java/dream/flying/flower/autoconfigure/localize/service/impl/LocalizeServiceImpl.java` | 修复 getContents / getContent / getAllMessages / buildCacheKey / getMesssges 拼写 / 异常日志 |
| `dream-localize3-spring-boot-starter/src/main/java/dream/flying/flower/autoconfigure/localize/service/LocalizeService.java`          | 可选:如需要公开批量方法,添加 `getMessages(List<String>, String)` 接口                               |
| `Change.md`(项目根)                                                                                                                     | 按用户规则记录所有变更原因、内容、结果                                                                  |

***

## 三、修改步骤

### Step 1. 修复 `getContents(List<String>, String)` (核心)

1. 首次批量 DB 查询 `in(localizeCode) + eq(fullLang)` 后,构造 `Map<localizeCode, content>` 记录直接命中项。
2. 对所有 `localizeCodes` 默认值先设为 `localizeCode` 本身。
3. 将直接命中的结果覆盖到 result map。
4. 只对**未命中**的 codes 进入 fallback 流程,避免重复查库。
5. fallback 查询改为批量:一次性查出 `languageId in (fallback语言ID列表) AND localizeCode in (未命中codes)` 的所有记录,**而不是每条 code 循环中单条 selectOne**(N+1 问题)。
6. 按 fallbackChain 的**优先级顺序**(从最精确到最宽泛),依次尝试填充未命中 code,一旦填充即 break(或通过 LinkedHashMap 顺序保证首次赋值不被覆盖)。
7. 最终 result 中保证每个输入 code 都有值(默认 localizeCode 或降级 content)。

### Step 2. 修复 `getContent(String, String)`

1. 首次查询 `localizeCode + fullLang` 命中且 content 非空,**直接 return content**。
2. 未命中才进入 fallback。
3. fallback 查询补齐 `.eq(LocalizeEntity::getLocalizeCode, localizeCode)` 条件。
4. fallback 命中后 `break` 退出循环。

### Step 3. 修复 `getAllMessages`

1. L142 DB 查询使用 `formatLang` 而不是原始 `lang`。
2. 修复缓存过期:把 `putMap(cacheKey, messageMap, 0, null)` 改为使用 `dreamLocalizeProperties.getExpire()` 中的 Duration 合理值。由于 putMap 接口签名是 `(long ttl, TimeUnit unit)`,需要把 Duration 拆分为对应数值+TimeUnit(或参考 `put(Duration)` 做法)。
3. 增加 `getMap` 返回空 map 时的判断,只有空才查库。

### Step 4. 修复 `buildCacheKey`

1. 保留现有的 `buildCacheKey(String lang, String code)` 用于单 key 缓存,不变。
2. 将 `buildCacheKey(String lang)` 改为**返回无** **`*`** **结尾**的 Hash key,用于 getMap/putMap。
3. 在 evictCache 中直接内联构造 pattern,或新增 `buildCachePattern(lang)` 方法返回带 `*` 的 pattern。

### Step 5. 修复 `getMesssges` 方法

1. 重命名为 `getMessages`(修正 3 个 s 的拼写)。
2. 检查该方法是否需要加入 `LocalizeService` 接口:如仅内部调用则保持 private/包可见;如外部要使用,添加到接口。从当前 grep 结果看只在本类出现,但由于是 public 方法,建议加入接口保持一致性。

### Step 6. 修复 `getMessage` 异常日志

1. L70-72 catch(Redis 查询):日志保持 "Localize cache query failed, retrieving from database"。
2. L82-85 catch(DB 查询):修改日志为 "Localize database query failed" 或更准确的内容。

### Step 7. 全方法一致性检查

* 所有方法的异常处理都不要用 `e.printStackTrace()`,改用 `log.error` / `log.info`,保持和其他 catch 块一致风格(getAllMessages L137 / L155, clearCache L166, evictCache L178 共 4 处)。

* 检查缓存 put 操作都有 try/catch 包裹:已满足。

* 确认所有 fullLang 传参都经过 `LocalizeHelpers.parse(lang).toLanguageTag()` 标准化。

### Step 8. 更新 Change.md

按用户规则记录:

* 变更原因(BUG 列表)

* 变更内容(对应 step)

* 修复结果(方法正确返回、性能提升、缓存正确、日志无误导等)

***

## 四、依赖与注意事项

1. **MyBatis-Plus list 批量查询**:依赖 `baseMapper.selectList` / `this.list` 方法,项目已继承 `AbstractServiceImpl`,可用。
2. **LocalizeHelpers.buildFallback**:返回顺序是从最精确到最宽泛(含 `-` 和 `_` 两种格式),修复后仍需按此优先级顺序填充。
3. **LanguageEntity.fullLang vs lang**:`buildFallback` 返回的 tag 经 `parse().toLanguageTag()` 标准化后与 `LanguageEntity.fullLang` 匹配,逻辑正确。
4. **缓存空值/不存在**:当前代码对未找到的 code 返回 `localizeCode` 本身,且**不缓存空结果**。如后续需要缓存穿透防护,可单独讨论,本次不改动此行为。
5. **性能优先原则(用户规则)**:fallback 部分由 N+1(每条 code 循环查 DB)改为 1-2 次批量 in 查询,显著提升性能,符合要求。
6. **注释规则(用户规则)**:已有注释不删,仅新增/修改本人生成的注释,不删除原有作者写的英文注释。

***

## 五、风险与处理

| 风险                                  | 影响             | 处理                                                          |
| ----------------------------------- | -------------- | ----------------------------------------------------------- |
| buildCacheKey 改名导致 Hash 缓存 key 格式变化 | 已有缓存命中率短期下降    | 旧 key 带 `*` 本身就是错的,实际没被正确使用过,影响可忽略;或通过 evictPattern 清理旧 key |
| putMap 从 0/null TTL 改为正常过期          | 内存占用上升         | 24h 过期与单条缓存 expire 一致,合理                                    |
| fallback 改批量查询 SQL 复杂度增加            | 首次 miss 延迟略有波动 | in 查询命中 languageId + code 联合索引,远优于 N+1                      |
| getMesssges 改名被外部调用者依赖              | 编译错误           | 目前 grep 全项目仅 1 处定义无外部调用,改名安全;若接口新增则是兼容扩展                    |

