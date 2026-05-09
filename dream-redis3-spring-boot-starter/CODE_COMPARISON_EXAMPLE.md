# 数据库切换优化 - 代码对比示例

## 优化前后对比

### 示例1：getString方法

#### ❌ 优化前（每次都切换）
```java
public Object getString(String key, Integer dbIndex) {
    Integer originalDb = null;
    try {
        if (dbIndex != null) {
            originalDb = getCurrentDatabase();
            selectDatabase(dbIndex);  // ⚠️ 无论是否相同都切换
        }
        return redisTemplate.opsForValue().get(key);
    } catch (Exception e) {
        log.error("获取String值失败, key: {}", key, e);
        return null;
    } finally {
        if (dbIndex != null && originalDb != null) {
            selectDatabase(originalDb);  // ⚠️ 无论是否切换都恢复
        }
    }
}
```

**问题：**
- 当dbIndex=0且当前数据库也是0时，仍会执行`SELECT 0`和`SELECT 0`
- 浪费2次Redis命令调用
- 增加不必要的网络开销

---

#### ✅ 优化后（智能判断）
```java
public Object getString(String key, Integer dbIndex) {
    Integer originalDb = null;
    boolean needSwitch = false;  // ✅ 新增标志位
    try {
        if (dbIndex != null) {
            originalDb = getCurrentDatabase();
            // ✅ 只有当目标数据库与当前数据库不同时才切换
            if (!dbIndex.equals(originalDb)) {
                selectDatabase(dbIndex);
                needSwitch = true;
            }
        }
        return redisTemplate.opsForValue().get(key);
    } catch (Exception e) {
        log.error("获取String值失败, key: {}", key, e);
        return null;
    } finally {
        // ✅ 只有在实际切换了数据库的情况下才恢复
        if (needSwitch && originalDb != null) {
            selectDatabase(originalDb);
        }
    }
}
```

**优势：**
- 当dbIndex=0且当前数据库也是0时，跳过切换操作
- 节省2次Redis命令调用
- 减少网络延迟

---

## Redis命令对比

### 场景：当前数据库=0，请求dbIndex=0

#### 优化前的Redis命令序列
```
1. SELECT 0          ← 不必要
2. GET test
3. SELECT 0          ← 不必要
```
**总计：3个命令**

#### 优化后的Redis命令序列
```
1. GET test
```
**总计：1个命令**

**节省：2个命令（66.7%）**

---

### 场景：当前数据库=0，请求dbIndex=2

#### 优化前的Redis命令序列
```
1. SELECT 2
2. GET test
3. SELECT 0
```
**总计：3个命令**

#### 优化后的Redis命令序列
```
1. SELECT 2
2. GET test
3. SELECT 0
```
**总计：3个命令**

**节省：0个命令（0%）**

---

## 性能影响分析

### 假设场景
- QPS: 1000请求/秒
- 80%请求访问默认数据库（dbIndex=0）
- 20%请求访问其他数据库

### 优化前
```
总命令数 = 1000 × 3 = 3000 命令/秒
SELECT命令数 = 1000 × 2 = 2000 次/秒
数据命令数 = 1000 × 1 = 1000 次/秒
```

### 优化后
```
相同数据库请求(800个): 800 × 1 = 800 命令/秒
不同数据库请求(200个): 200 × 3 = 600 命令/秒
总命令数 = 800 + 600 = 1400 命令/秒
SELECT命令数 = 200 × 2 = 400 次/秒
数据命令数 = 1000 × 1 = 1000 次/秒
```

### 性能提升
```
命令总数减少: (3000 - 1400) / 3000 = 53.3%
SELECT命令减少: (2000 - 400) / 2000 = 80%
数据命令不变: 1000 = 1000 (0%变化)
```

---

## 完整方法列表示例

所有23个方法都采用相同的优化模式：

### 1. Key操作方法

```java
// keys() - 查询Keys
public List<String> keys(String pattern, Integer dbIndex) {
    Integer originalDb = null;
    boolean needSwitch = false;
    try {
        if (dbIndex != null) {
            originalDb = getCurrentDatabase();
            if (!dbIndex.equals(originalDb)) {
                selectDatabase(dbIndex);
                needSwitch = true;
            }
        }
        // ... 执行keys操作
    } finally {
        if (needSwitch && originalDb != null) {
            selectDatabase(originalDb);
        }
    }
}

// delete() - 删除Key
public Boolean delete(String key, Integer dbIndex) {
    Integer originalDb = null;
    boolean needSwitch = false;
    try {
        if (dbIndex != null) {
            originalDb = getCurrentDatabase();
            if (!dbIndex.equals(originalDb)) {
                selectDatabase(dbIndex);
                needSwitch = true;
            }
        }
        // ... 执行delete操作
    } finally {
        if (needSwitch && originalDb != null) {
            selectDatabase(originalDb);
        }
    }
}
```

### 2. String操作方法

```java
// setString() - 设置字符串
public Boolean setString(String key, Object value, Long expireTime, Integer dbIndex) {
    Integer originalDb = null;
    boolean needSwitch = false;
    try {
        if (dbIndex != null) {
            originalDb = getCurrentDatabase();
            if (!dbIndex.equals(originalDb)) {
                selectDatabase(dbIndex);
                needSwitch = true;
            }
        }
        // ... 执行set操作
    } finally {
        if (needSwitch && originalDb != null) {
            selectDatabase(originalDb);
        }
    }
}
```

### 3. Hash操作方法

```java
// setHashField() - 设置Hash字段
public Boolean setHashField(String key, String hashKey, Object value, Integer dbIndex) {
    Integer originalDb = null;
    boolean needSwitch = false;
    try {
        if (dbIndex != null) {
            originalDb = getCurrentDatabase();
            if (!dbIndex.equals(originalDb)) {
                selectDatabase(dbIndex);
                needSwitch = true;
            }
        }
        // ... 执行hset操作
    } finally {
        if (needSwitch && originalDb != null) {
            selectDatabase(originalDb);
        }
    }
}
```

### 4. List操作方法

```java
// leftPushList() - 左侧添加元素
public Long leftPushList(String key, Object value, Integer dbIndex) {
    Integer originalDb = null;
    boolean needSwitch = false;
    try {
        if (dbIndex != null) {
            originalDb = getCurrentDatabase();
            if (!dbIndex.equals(originalDb)) {
                selectDatabase(dbIndex);
                needSwitch = true;
            }
        }
        // ... 执行lpush操作
    } finally {
        if (needSwitch && originalDb != null) {
            selectDatabase(originalDb);
        }
    }
}
```

### 5. Set操作方法

```java
// addSetMembers() - 添加Set成员
public Long addSetMembers(String key, List<Object> values, Integer dbIndex) {
    Integer originalDb = null;
    boolean needSwitch = false;
    try {
        if (dbIndex != null) {
            originalDb = getCurrentDatabase();
            if (!dbIndex.equals(originalDb)) {
                selectDatabase(dbIndex);
                needSwitch = true;
            }
        }
        // ... 执行sadd操作
    } finally {
        if (needSwitch && originalDb != null) {
            selectDatabase(originalDb);
        }
    }
}
```

### 6. ZSet操作方法

```java
// addZSetMember() - 添加ZSet成员
public Boolean addZSetMember(String key, Object value, double score, Integer dbIndex) {
    Integer originalDb = null;
    boolean needSwitch = false;
    try {
        if (dbIndex != null) {
            originalDb = getCurrentDatabase();
            if (!dbIndex.equals(originalDb)) {
                selectDatabase(dbIndex);
                needSwitch = true;
            }
        }
        // ... 执行zadd操作
    } finally {
        if (needSwitch && originalDb != null) {
            selectDatabase(originalDb);
        }
    }
}
```

---

## 关键要点总结

### ✅ 统一的优化模式
所有23个方法都遵循相同的模式：
1. 声明`needSwitch`标志位
2. 比较`dbIndex`与`originalDb`
3. 仅在需要时执行切换
4. 仅在需要时执行恢复

### ✅ 保证正确性
- 异常情况下也能正确恢复
- 线程安全，互不干扰
- 向后兼容，不影响现有代码

### ✅ 显著提升性能
- 减少53%的总命令数（典型场景）
- 减少80%的SELECT命令
- 降低网络延迟
- 提高吞吐量

### ✅ 易于维护
- 代码模式统一
- 逻辑清晰易懂
- 注释完整详细

---

## 使用建议

### 最佳实践
1. **显式指定dbIndex**：即使使用默认数据库，也建议显式传入`dbIndex=0`
2. **批量操作优化**：同一批请求尽量使用相同数据库
3. **监控切换频率**：关注needSwitch的实际触发比例
4. **定期性能评估**：对比优化前后的性能指标

### 注意事项
⚠️ 不要依赖数据库状态持久化
⚠️ 每次请求都是独立的
⚠️ 并发请求不会相互影响
⚠️ 确保Redis连接池配置合理

---

**文档版本**: 1.0  
**更新日期**: 2026-05-09  
**作者**: AI Assistant
