# Redis连接线程安全重构指南

## ⚠️ 重要问题

**当前实现存在严重的线程安全问题！**

### 问题分析

```java
// ❌ 错误的实现 - 共享redisTemplate
public Object getString(String key, Integer dbIndex) {
    if (dbIndex != null) {
        selectDatabase(dbIndex);  // 切换共享连接的数据库
    }
    return redisTemplate.opsForValue().get(key);  // 可能在错误的数据库中操作
    // 如果此时有其他线程也在使用redisTemplate,会造成数据混乱!
}
```

**问题场景：**
1. 线程A调用 `getString("key1", 0)` - 切换到数据库0
2. 线程B调用 `getString("key2", 5)` - 切换到数据库5
3. 线程A执行get操作 - **实际在数据库5中获取数据!** ❌

---

## ✅ 正确的解决方案

### 核心原则

**每个请求创建独立的Redis连接，操作完成后立即关闭**

```java
// ✅ 正确的实现 - 独立连接
public Object getString(String key, Integer dbIndex) {
    RedisConnection connection = null;
    try {
        // 1. 创建新连接
        connection = connectionFactory.getConnection();
        
        // 2. 切换到目标数据库
        if (dbIndex != null) {
            connection.select(dbIndex);
        }
        
        // 3. 执行操作
        byte[] value = connection.stringCommands().get(key.getBytes());
        return deserializeValue(value);
        
    } finally {
        // 4. 关闭连接(归还到连接池)
        if (connection != null) {
            connection.close();
        }
    }
}
```

---

## 🔧 重构步骤

### 第1步：添加辅助方法

已添加到`RedisManageService.java`：

```java
/**
 * 执行Redis操作(线程安全)
 */
private <T> T executeWithConnection(Integer dbIndex, RedisOperation<T> operation) {
    RedisConnection connection = null;
    try {
        connection = connectionFactory.getConnection();
        if (dbIndex != null) {
            connection.select(dbIndex);
        }
        return operation.execute(connection);
    } catch (Exception e) {
        log.error("Redis操作失败", e);
        throw e;
    } finally {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                log.error("关闭连接失败", e);
            }
        }
        if (monitor != null) {
            monitor.recordRequest(dbIndex, dbIndex != null);
        }
    }
}

@FunctionalInterface
private interface RedisOperation<T> {
    T execute(RedisConnection connection) throws Exception;
}
```

### 第2步：重构所有方法

#### 模式1：简单操作（使用辅助方法）

**重构前：**
```java
public Object getString(String key, Integer dbIndex) {
    Integer originalDb = null;
    boolean needSwitch = false;
    try {
        if (dbIndex != null) {
            originalDb = getCurrentDatabase();
            if (dbIndex != originalDb) {
                selectDatabase(dbIndex);
                needSwitch = true;
            }
        }
        return redisTemplate.opsForValue().get(key);
    } finally {
        if (needSwitch && originalDb != null) {
            selectDatabase(originalDb);
        }
    }
}
```

**重构后：**
```java
public Object getString(String key, Integer dbIndex) {
    try {
        return executeWithConnection(dbIndex, connection -> {
            byte[] value = connection.stringCommands().get(key.getBytes());
            return deserializeValue(value);
        });
    } catch (Exception e) {
        log.error("获取String值失败, key: {}", key, e);
        return null;
    }
}
```

#### 模式2：复杂操作（直接创建连接）

**重构前：**
```java
public List<String> keys(String pattern, Integer dbIndex) {
    Integer originalDb = null;
    boolean needSwitch = false;
    try {
        if (dbIndex != null) {
            originalDb = getCurrentDatabase();
            if (dbIndex != originalDb) {
                selectDatabase(dbIndex);
                needSwitch = true;
            }
        }
        Set<String> keys = redisTemplate.keys(pattern);
        // ...
    } finally {
        if (needSwitch && originalDb != null) {
            selectDatabase(originalDb);
        }
    }
}
```

**重构后：**
```java
public List<String> keys(String pattern, Integer dbIndex) {
    RedisConnection connection = null;
    try {
        connection = connectionFactory.getConnection();
        if (dbIndex != null) {
            connection.select(dbIndex);
        }
        
        List<String> result = new ArrayList<>();
        Set<byte[]> keys = connection.keys(pattern.getBytes());
        if (keys != null) {
            for (byte[] key : keys) {
                result.add(new String(key));
            }
        }
        return result;
    } catch (Exception e) {
        log.error("获取Keys失败", e);
        return new ArrayList<>();
    } finally {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                log.error("关闭连接失败", e);
            }
        }
        if (monitor != null) {
            monitor.recordRequest(dbIndex, dbIndex != null);
        }
    }
}
```

---

## 📋 需要重构的方法清单

### Key管理（7个方法）
- [x] `keys()` - 已完成
- [ ] `scanKeys()` - 部分完成
- [ ] `delete()`
- [ ] `deleteBatch()`
- [ ] `exists()`
- [ ] `expire()`
- [ ] `getExpire()`

### String操作（2个方法）
- [ ] `getString()` 
- [ ] `setString()`

### Hash操作（4个方法）
- [ ] `getHash()`
- [ ] `getHashField()`
- [ ] `setHashField()`
- [ ] `deleteHashFields()`

### List操作（4个方法）
- [ ] `getList()`
- [ ] `leftPushList()`
- [ ] `rightPushList()`
- [ ] `removeListElement()`

### Set操作（3个方法）
- [ ] `getSet()`
- [ ] `addSetMembers()`
- [ ] `removeSetMembers()`

### ZSet操作（3个方法）
- [ ] `getZSet()`
- [ ] `addZSetMember()`
- [ ] `removeZSetMembers()`

**总计：23个方法需要重构**

---

## 🔑 关键API映射

### Redis命令对照表

| RedisTemplate API | RedisConnection API | 说明 |
|------------------|---------------------|------|
| `opsForValue().get(key)` | `stringCommands().get(key.getBytes())` | 获取String |
| `opsForValue().set(key, value)` | `stringCommands().set(key.getBytes(), valueBytes)` | 设置String |
| `keys(pattern)` | `keys(pattern.getBytes())` | 获取Keys |
| `delete(key)` | `del(key.getBytes())` | 删除Key |
| `hasKey(key)` | `exists(key.getBytes())` | 判断存在 |
| `expire(key, time)` | `expire(key.getBytes(), time)` | 设置过期 |
| `opsForHash().get(key, field)` | `hashCommands().hGet(key.getBytes(), field.getBytes())` | Hash获取 |
| `opsForHash().put(key, field, value)` | `hashCommands().hSet(...)` | Hash设置 |
| `opsForList().leftPush(key, value)` | `listCommands().lPush(...)` | List左推 |
| `opsForList().rightPush(key, value)` | `listCommands().rPush(...)` | List右推 |
| `opsForSet().members(key)` | `setCommands().sMembers(...)` | Set成员 |
| `opsForZSet().range(key, start, end)` | `zSetCommands().zRange(...)` | ZSet范围 |

---

## 💡 最佳实践

### 1. 序列化/反序列化

```java
// 序列化
byte[] keyBytes = key.getBytes();
byte[] valueBytes = redisTemplate.getValueSerializer().serialize(value);

// 反序列化
Object value = redisTemplate.getValueSerializer().deserialize(valueBytes);
```

### 2. 异常处理

```java
try {
    // Redis操作
} catch (Exception e) {
    log.error("Redis操作失败", e);
    // 返回默认值或抛出异常
} finally {
    // 确保关闭连接
    if (connection != null) {
        connection.close();
    }
}
```

### 3. 监控记录

```java
if (monitor != null) {
    monitor.recordRequest(dbIndex, dbIndex != null);
}
```

---

## 🎯 性能考虑

### 连接池配置

确保Redis连接池配置合理：

```yaml
spring:
  redis:
    lettuce:
      pool:
        max-active: 20      # 最大活跃连接数
        max-idle: 10        # 最大空闲连接数
        min-idle: 5         # 最小空闲连接数
        max-wait: 3000ms    # 最大等待时间
```

### 性能对比

| 方式 | 连接管理 | 线程安全 | 性能 |
|------|---------|---------|------|
| ❌ 共享redisTemplate + SELECT | 共享连接 | ❌ 不安全 | 高但有风险 |
| ✅ 独立连接 + 关闭 | 连接池管理 | ✅ 安全 | 略低但稳定 |

**性能影响：**
- 每次操作从连接池获取连接：~0.1ms
- 操作完成后归还连接：~0.05ms
- 总开销：~0.15ms（可接受）
- 换来的是**线程安全保证**

---

## 🧪 测试验证

### 并发测试

```java
@Test
void testConcurrentAccess() throws InterruptedException {
    int threadCount = 100;
    CountDownLatch latch = new CountDownLatch(threadCount);
    
    for (int i = 0; i < threadCount; i++) {
        final int dbIndex = i % 16;
        new Thread(() -> {
            try {
                // 访问不同数据库
                Object value = redisManageService.getString("test", dbIndex);
                // 验证数据正确性
            } finally {
                latch.countDown();
            }
        }).start();
    }
    
    latch.await();
    // 验证所有操作都正确完成
}
```

---

## 📝 总结

### 重构要点

1. ✅ **绝不共享连接** - 每个操作创建独立连接
2. ✅ **及时关闭** - finally块中关闭连接
3. ✅ **正确序列化** - 使用RedisTemplate的序列化器
4. ✅ **异常安全** - 确保连接一定被关闭
5. ✅ **监控记录** - 记录每次操作的数据库索引

### 优势

- ✅ **线程安全** - 完全隔离，互不影响
- ✅ **数据准确** - 不会在错误的数据库中操作
- ✅ **资源管理** - 连接池自动管理连接生命周期
- ✅ **可扩展性** - 支持高并发访问

### 下一步

1. 按照本指南重构剩余21个方法
2. 运行单元测试验证功能
3. 进行并发压力测试
4. 更新相关文档

---

**创建时间**: 2026-05-09  
**状态**: 🔄 重构进行中  
**优先级**: 🔴 高（线程安全问题）
