# Change Log

## 2026-05-09

### 新增功能
- 添加Redis Web管理功能，提供REST API接口访问Redis
- 支持增删改查Redis中的数据
- 支持切换Redis数据库
- 支持多种数据类型操作：String、Hash、List、Set、ZSet
- **所有操作支持通过dbIndex参数指定目标数据库**

### 性能优化
- **智能数据库切换优化**：当参数传递的数据库索引与当前默认索引相同时，跳过不必要的数据库切换操作
- 减少Redis命令调用次数，提升性能
- 降低网络开销，保持连接状态稳定
- **代码优化**：数字比较使用`==`运算符而非`equals()`方法，利用Java自动拆箱特性

### 🔴 重要修复 - 线程安全问题
- **问题**: 原实现共享redisTemplate并切换数据库，在高并发下会导致数据错乱
- **解决**: 为每个请求创建独立的Redis连接，操作完成后立即关闭
- **优势**: 
  - ✅ 完全线程安全，各请求互不影响
  - ✅ 数据准确性保证，不会在错误数据库中操作
  - ✅ 连接池管理，资源自动回收
- **影响**: 已重构所有23个Service方法
  - String操作: getString, setString (2个)
  - Key管理: keys, scanKeys, delete, deleteBatch, exists, expire, getExpire (7个)
  - Hash操作: getHash, getHashField, setHashField, deleteHashFields (4个)
  - List操作: getList, leftPushList, rightPushList, removeListElement (4个)
  - Set操作: getSet, addSetMembers, removeSetMembers (3个)
  - ZSet操作: getZSet, addZSetMember, removeZSetMembers (3个)
- **文档**: 详见[THREAD_SAFETY_REFACTORING.md](THREAD_SAFETY_REFACTORING.md)和[REFACTORING_PROGRESS.md](REFACTORING_PROGRESS.md)

### 变更内容
1. **新增DTO类**
   - `RedisDataRequest.java`: Redis数据请求DTO
   - `RedisDataResponse.java`: Redis数据响应DTO

2. **新增服务类**
   - `RedisManageService.java`: Redis管理服务类，提供完整的Redis操作功能
   - **所有方法增加dbIndex参数支持，操作前切换到指定数据库，操作后恢复原数据库**

3. **新增控制器**
   - `RedisManageController.java`: Redis管理控制器，提供REST API接口
   - **所有接口增加dbIndex可选参数，从请求中获取并传递给Service层**

4. **新增自动配置类**
   - `RedisWebAutoConfiguration.java`: Redis Web管理自动配置类

5. **更新配置文件**
   - `spring.factories`: 添加RedisWebAutoConfiguration到自动配置列表

### 重要改进
**数据库切换机制优化：**
- 之前的实现：需要先调用`/api/redis/select-db`切换数据库，然后执行操作
- 现在的实现：每个API都可以直接通过`dbIndex`参数指定目标数据库
- 优势：
  1. 无需预先切换数据库，每次操作独立指定
  2. 操作完成后自动恢复到原数据库，不影响其他连接
  3. 支持并发操作不同数据库，互不干扰
  4. 更符合RESTful无状态特性

### API接口列表
- GET `/api/redis/keys`: 获取所有Key
- GET `/api/redis/get`: 获取数据
- POST `/api/redis/set`: 设置字符串数据
- POST `/api/redis/hash/set`: 设置Hash字段值
- POST `/api/redis/list/left-push`: 向List左侧添加元素
- POST `/api/redis/list/right-push`: 向List右侧添加元素
- POST `/api/redis/set/add`: 向Set添加成员
- POST `/api/redis/zset/add`: 向ZSet添加成员
- DELETE `/api/redis/delete`: 删除Key
- DELETE `/api/redis/delete-batch`: 批量删除Key
- DELETE `/api/redis/hash/delete`: 删除Hash字段
- DELETE `/api/redis/set/remove`: 从Set移除成员
- DELETE `/api/redis/zset/remove`: 从ZSet移除成员
- GET `/api/redis/exists`: 判断Key是否存在
- PUT `/api/redis/expire`: 设置Key过期时间
- GET `/api/redis/ttl`: 获取Key剩余过期时间
- POST `/api/redis/select-db`: 切换数据库
- GET `/api/redis/current-db`: 获取当前数据库索引
- DELETE `/api/redis/flush-db`: 清空当前数据库
- DELETE `/api/redis/flush-all`: 清空所有数据库

### 修复结果
- 成功实现Redis Web管理功能
- 所有API接口均可正常访问
- 支持数据库切换功能
- 支持多种数据类型的增删改查操作
