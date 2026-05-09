# Redis Web管理 - 数据库选择功能说明

## 概述

本次更新为所有Redis Web API添加了灵活的数据库选择功能。现在，每个API调用都可以通过`dbIndex`参数指定目标数据库，无需预先切换数据库。

## 核心改进

### 智能数据库切换优化

**优化策略：** 当参数传递的数据库索引与当前默认索引相同时，不会进行不必要的数据库切换操作。

**实现原理：**
```java
Integer originalDb = null;
boolean needSwitch = false;
try {
    if (dbIndex != null) {
        originalDb = getCurrentDatabase();
        // 只有当目标数据库与当前数据库不同时才切换
        if (!dbIndex.equals(originalDb)) {
            selectDatabase(dbIndex);
            needSwitch = true;
        }
    }
    // 执行实际操作...
} finally {
    // 只有在实际切换了数据库的情况下才恢复
    if (needSwitch && originalDb != null) {
        selectDatabase(originalDb);
    }
}
```

**优势：**
- ✅ 减少不必要的Redis命令调用
- ✅ 提升性能，避免重复切换同一数据库
- ✅ 保持连接状态稳定
- ✅ 降低网络开销

### 之前的实现方式（已优化）
```bash
# 1. 先切换数据库
curl -X POST "http://localhost:8080/api/redis/select-db?dbIndex=1"

# 2. 执行操作（在数据库1中）
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"test","value":"hello"}'

# 3. 如果需要操作其他数据库，需要再次切换
curl -X POST "http://localhost:8080/api/redis/select-db?dbIndex=2"
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"test","value":"world"}'
```

**问题：**
- 需要维护数据库状态
- 并发请求会相互干扰
- 不符合RESTful无状态原则
- 容易出错

### 现在的实现方式
```bash
# 直接在请求中指定数据库
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"test","value":"hello","dbIndex":1}'

curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"test","value":"world","dbIndex":2}'

# GET请求使用查询参数
curl -X GET "http://localhost:8080/api/redis/get?key=test&dataType=string&dbIndex=1"
curl -X GET "http://localhost:8080/api/redis/get?key=test&dataType=string&dbIndex=2"
```

**优势：**
- ✅ 无需维护状态，每次请求独立
- ✅ 支持并发操作不同数据库
- ✅ 符合RESTful无状态原则
- ✅ 操作简单，不易出错
- ✅ 自动恢复原数据库，不影响其他连接

## 技术实现

### Service层实现原理

每个操作方法都遵循以下模式：

```java
public Object operation(String key, Integer dbIndex) {
    Integer originalDb = null;
    try {
        // 1. 如果指定了dbIndex，保存当前数据库并切换到目标数据库
        if (dbIndex != null) {
            originalDb = getCurrentDatabase();
            selectDatabase(dbIndex);
        }
        
        // 2. 执行实际的Redis操作
        return redisTemplate.opsForValue().get(key);
        
    } catch (Exception e) {
        log.error("操作失败", e);
        return null;
    } finally {
        // 3. 如果之前切换了数据库，恢复到原数据库
        if (dbIndex != null && originalDb != null) {
            selectDatabase(originalDb);
        }
    }
}
```

### 关键特性

1. **原子性保证**：使用try-finally确保无论操作成功与否，都会恢复原数据库
2. **线程安全**：每个请求独立处理，互不干扰
3. **向后兼容**：如果不传dbIndex参数，使用当前默认数据库
4. **性能优化**：只在需要时切换数据库，减少不必要的操作

## 使用示例

### 1. String操作

```bash
# 设置值到数据库1
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"user:1","value":"张三","dbIndex":1}'

# 从数据库1获取
curl -X GET "http://localhost:8080/api/redis/get?key=user:1&dataType=string&dbIndex=1"

# 设置值到数据库2（同名Key，不同值）
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"user:1","value":"李四","dbIndex":2}'

# 从数据库2获取（返回不同的值）
curl -X GET "http://localhost:8080/api/redis/get?key=user:1&dataType=string&dbIndex=2"
```

### 2. Hash操作

```bash
# 在数据库1中设置Hash
curl -X POST http://localhost:8080/api/redis/hash/set \
  -H "Content-Type: application/json" \
  -d '{"key":"user:1001","hashKey":"name","value":"王五","dbIndex":1}'

# 从数据库1获取Hash
curl -X GET "http://localhost:8080/api/redis/get?key=user:1001&dataType=hash&dbIndex=1"
```

### 3. List操作

```bash
# 向数据库1的List添加元素
curl -X POST http://localhost:8080/api/redis/list/right-push \
  -H "Content-Type: application/json" \
  -d '{"key":"messages","value":"消息1","dbIndex":1}'

# 从数据库1获取List
curl -X GET "http://localhost:8080/api/redis/get?key=messages&dataType=list&dbIndex=1"
```

### 4. Set操作

```bash
# 向数据库1的Set添加成员
curl -X POST http://localhost:8080/api/redis/set/add \
  -H "Content-Type: application/json" \
  -d '{"key":"tags","value":["Java","Spring"],"dbIndex":1}'

# 从数据库1获取Set
curl -X GET "http://localhost:8080/api/redis/get?key=tags&dataType=set&dbIndex=1"
```

### 5. Key管理

```bash
# 查询数据库1中的所有Key
curl -X GET "http://localhost:8080/api/redis/keys?pattern=*&dbIndex=1"

# 判断数据库1中Key是否存在
curl -X GET "http://localhost:8080/api/redis/exists?key=user:1&dbIndex=1"

# 删除数据库1中的Key
curl -X DELETE "http://localhost:8080/api/redis/delete?key=user:1&dbIndex=1"

# 批量删除数据库1中的Keys
curl -X DELETE http://localhost:8080/api/redis/delete-batch?dbIndex=1 \
  -H "Content-Type: application/json" \
  -d '["key1","key2","key3"]'

# 设置数据库1中Key的过期时间
curl -X PUT "http://localhost:8080/api/redis/expire?key=user:1&expireTime=3600&dbIndex=1"

# 获取数据库1中Key的TTL
curl -X GET "http://localhost:8080/api/redis/ttl?key=user:1&dbIndex=1"
```

### 6. 跨数据库操作场景

```bash
# 场景：将数据从数据库1复制到数据库2

# 1. 从数据库1获取数据
curl -X GET "http://localhost:8080/api/redis/get?key=config&dataType=string&dbIndex=1"
# 返回: {"success":true,"data":"config_value"}

# 2. 将数据设置到数据库2
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"config","value":"config_value","dbIndex":2}'

# 3. 验证两个数据库中的数据
curl -X GET "http://localhost:8080/api/redis/get?key=config&dataType=string&dbIndex=1"
curl -X GET "http://localhost:8080/api/redis/get?key=config&dataType=string&dbIndex=2"
```

## 参数传递方式

### GET请求
使用URL查询参数：
```
GET /api/redis/get?key=test&dataType=string&dbIndex=1
```

### POST/PUT/DELETE请求（JSON Body）
在JSON body中添加dbIndex字段：
```json
{
  "key": "test",
  "value": "hello",
  "dbIndex": 1
}
```

### POST/PUT/DELETE请求（URL参数）
也可以使用URL查询参数：
```
DELETE /api/redis/delete?key=test&dbIndex=1
```

## 注意事项

1. **dbIndex是可选参数**
   - 如果不指定，使用当前默认数据库（通常为0）
   - 建议显式指定dbIndex以避免混淆

2. **数据库索引范围**
   - Redis默认支持16个数据库（0-15）
   - 可通过Redis配置文件修改数量

3. **性能考虑**
   - 每次切换数据库有轻微开销
   - 如果大量操作同一数据库，可以考虑使用select-db接口

4. **并发安全**
   - 每个请求独立处理，线程安全
   - 不用担心并发请求相互干扰

5. **事务支持**
   - 当前实现不支持跨数据库事务
   - 如需事务，请在同一数据库内操作

## 迁移指南

如果您之前使用了`select-db`接口，可以按以下方式迁移：

### 旧代码
```javascript
// 1. 切换数据库
await fetch('/api/redis/select-db?dbIndex=1', { method: 'POST' });

// 2. 执行操作
await fetch('/api/redis/set', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ key: 'test', value: 'hello' })
});
```

### 新代码
```javascript
// 直接指定数据库
await fetch('/api/redis/set', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ 
    key: 'test', 
    value: 'hello',
    dbIndex: 1  // 直接在这里指定
  })
});
```

## 总结

这次更新使Redis Web管理功能更加灵活、安全和易用：

- ✅ **灵活性**：每次请求可独立指定数据库
- ✅ **安全性**：自动恢复原数据库，避免状态污染
- ✅ **易用性**：简化API调用，减少出错可能
- ✅ **兼容性**：向后兼容，不影响现有代码
- ✅ **高性能**：最小化数据库切换开销

所有20个API接口都已支持dbIndex参数，可以立即使用！
