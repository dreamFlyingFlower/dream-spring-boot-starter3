# DictCacheEndpoint 修改计划

## 研究结论

### 当前状态

**DictCacheEndpoint** 已有 3 个端点:
- `GET /config-cache/warmup` → `dictCacheWarmupRunner.warmup()` ✅ 可用
- `GET /config-cache/refresh` → `dictCacheWarmupRunner.refresh(configKey)` ❌ **DictCacheWarmupRunner 没有 refresh 方法,编译会失败**
- `DELETE /config-cache/evict` → `dictCacheWarmupRunner.evict(configKey)` ✅ 可用

**DictCacheWarmupRunner** 已有方法:
- `warmup()` — 全量预热 ✅
- `cacheDict(dict)` — 缓存单个字典 ✅
- `cacheDictItems(dictId)` — 缓存字典项 ✅
- `evict(dictCode)` — 删除字典缓存 ✅
- **缺失**: `refresh(dictCode)` — 根据字典编码从 DB 重新加载单个字典及字典项

### 问题分析

1. `DictCacheEndpoint.refresh()` 已调用 `dictCacheWarmupRunner.refresh(configKey)`,但 `DictCacheWarmupRunner` 中不存在该方法
2. `evict(dictCode)` 只删除了字典本身的缓存,没有级联删除字典项缓存
3. 端点参数名 `configKey` 在字典上下文中应为 `dictCode`

## 需要修改的文件

### 1. DictCacheWarmupRunner.java
**路径**: `dream-dict3-spring-boot-starter/src/main/java/dream/flying/flower/autoconfigure/dict/cache/DictCacheWarmupRunner.java`

**修改内容**:
- 新增 `refresh(String dictCode)` 方法:
  - 根据 dictCode 从 DB 查询字典
  - 找到: 调用 `cacheDict(dict)` 和 `cacheDictItems(dict.getId())`
  - 未找到: 调用 `evict(dictCode)` 并删除字典项缓存
  - 异常捕获并记录日志
- 修改 `evict(String dictCode)` 方法: 增加级联删除字典项缓存的逻辑

### 2. DictCacheEndpoint.java
**路径**: `dream-dict3-spring-boot-starter/src/main/java/dream/flying/flower/autoconfigure/dict/endpoint/DictCacheEndpoint.java`

**修改内容**:
- 将 `refresh()` 和 `evict()` 方法的参数名从 `configKey` 改为 `dictCode`(语义更清晰)
- 更新 `@Parameter` 描述

**不修改**: `@ConditionalOnProperty` 注解中的 prefix/name 属性

## 实施步骤

1. 在 `DictCacheWarmupRunner` 中添加 `refresh(dictCode)` 方法
2. 修复 `evict(dictCode)` 增加字典项缓存级联删除
3. 在 `DictCacheEndpoint` 中将参数名 `configKey` 改为 `dictCode`
4. 编译验证
5. 运行测试

## 风险处理

- 如果 `DictCacheWarmupRunner` 中使用了框架层的 `ConstCache.buildRedisKey()` 和 `ConstStarter.PROJECT_NAME`,但这些在框架中可能不存在。如果编译失败,需要参照 config 模块的做法替换为本地字符串拼接
- 如果 `Result.error(String)` 不存在(之前遇到过),改用 `Result.error()` 或其他可用方法