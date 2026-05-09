# Redis Web API - dbIndex参数快速参考

## 核心原则

**所有API都支持`dbIndex`参数来指定目标数据库**

- **GET请求**：使用查询参数 `?dbIndex=1`
- **POST/PUT/DELETE请求**：在JSON body中添加 `"dbIndex": 1` 或使用查询参数

---

## API快速参考表

### Key管理

| API | 方法 | dbIndex用法 | 示例 |
|-----|------|------------|------|
| 获取Keys | GET | 查询参数 | `/api/redis/keys?pattern=*&dbIndex=1` |
| 判断存在 | GET | 查询参数 | `/api/redis/exists?key=test&dbIndex=1` |
| 删除Key | DELETE | 查询参数 | `/api/redis/delete?key=test&dbIndex=1` |
| 批量删除 | DELETE | 查询参数+Body | `/api/redis/delete-batch?dbIndex=1` + Body: `["k1","k2"]` |
| 设置TTL | PUT | 查询参数 | `/api/redis/expire?key=test&expireTime=3600&dbIndex=1` |
| 获取TTL | GET | 查询参数 | `/api/redis/ttl?key=test&dbIndex=1` |

### String操作

| API | 方法 | dbIndex用法 | 示例 |
|-----|------|------------|------|
| 获取值 | GET | 查询参数 | `/api/redis/get?key=test&dataType=string&dbIndex=1` |
| 设置值 | POST | JSON Body | Body: `{"key":"test","value":"hello","dbIndex":1}` |

### Hash操作

| API | 方法 | dbIndex用法 | 示例 |
|-----|------|------------|------|
| 获取Hash | GET | 查询参数 | `/api/redis/get?key=h1&dataType=hash&dbIndex=1` |
| 获取字段 | GET | 查询参数 | `/api/redis/get?key=h1&dataType=hash&hashKey=f1&dbIndex=1` |
| 设置字段 | POST | JSON Body | Body: `{"key":"h1","hashKey":"f1","value":"v1","dbIndex":1}` |
| 删除字段 | DELETE | JSON Body | Body: `{"key":"h1","value":["f1","f2"],"dbIndex":1}` |

### List操作

| API | 方法 | dbIndex用法 | 示例 |
|-----|------|------------|------|
| 获取List | GET | 查询参数 | `/api/redis/get?key=l1&dataType=list&dbIndex=1` |
| 左添加 | POST | JSON Body | Body: `{"key":"l1","value":"item","dbIndex":1}` |
| 右添加 | POST | JSON Body | Body: `{"key":"l1","value":"item","dbIndex":1}` |

### Set操作

| API | 方法 | dbIndex用法 | 示例 |
|-----|------|------------|------|
| 获取Set | GET | 查询参数 | `/api/redis/get?key=s1&dataType=set&dbIndex=1` |
| 添加成员 | POST | JSON Body | Body: `{"key":"s1","value":["m1","m2"],"dbIndex":1}` |
| 移除成员 | DELETE | JSON Body | Body: `{"key":"s1","value":["m1"],"dbIndex":1}` |

### ZSet操作

| API | 方法 | dbIndex用法 | 示例 |
|-----|------|------------|------|
| 获取ZSet | GET | 查询参数 | `/api/redis/get?key=z1&dataType=zset&dbIndex=1` |
| 添加成员 | POST | JSON Body | Body: `{"key":"z1","value":100.5,"dbIndex":1}` |
| 移除成员 | DELETE | JSON Body | Body: `{"key":"z1","value":["m1"],"dbIndex":1}` |

### 数据库管理

| API | 方法 | 说明 | 示例 |
|-----|------|------|------|
| 切换数据库 | POST | 全局切换（不推荐） | `/api/redis/select-db?dbIndex=1` |
| 当前数据库 | GET | 获取当前数据库 | `/api/redis/current-db` |
| 清空当前库 | DELETE | 清空当前数据库 | `/api/redis/flush-db` |
| 清空所有库 | DELETE | 清空所有数据库 | `/api/redis/flush-all` |

---

## 常用场景示例

### 场景1：多租户数据隔离

```bash
# 租户A的数据存储在数据库1
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"user:1001","value":"Tenant A User","dbIndex":1}'

# 租户B的数据存储在数据库2
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"user:1001","value":"Tenant B User","dbIndex":2}'

# 分别查询（返回不同结果）
curl -X GET "http://localhost:8080/api/redis/get?key=user:1001&dataType=string&dbIndex=1"
curl -X GET "http://localhost:8080/api/redis/get?key=user:1001&dataType=string&dbIndex=2"
```

### 场景2：环境隔离

```bash
# 开发环境使用数据库0
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"config","value":"dev-config","dbIndex":0}'

# 测试环境使用数据库1
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"config","value":"test-config","dbIndex":1}'

# 生产环境使用数据库2
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"config","value":"prod-config","dbIndex":2}'
```

### 场景3：缓存分层

```bash
# 热点数据在数据库0（最快）
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"hot:data","value":"hot value","expireTime":60,"dbIndex":0}'

# 普通数据在数据库1
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"normal:data","value":"normal value","expireTime":3600,"dbIndex":1}'

# 冷数据在数据库2
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"cold:data","value":"cold value","expireTime":86400,"dbIndex":2}'
```

### 场景4：数据迁移

```bash
# 1. 从源数据库读取
SOURCE_DATA=$(curl -s "http://localhost:8080/api/redis/get?key=mydata&dataType=string&dbIndex=1")

# 2. 提取值（假设返回格式为 {"success":true,"data":"value"}）
VALUE=$(echo $SOURCE_DATA | jq -r '.data')

# 3. 写入目标数据库
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d "{\"key\":\"mydata\",\"value\":\"$VALUE\",\"dbIndex\":2}"
```

---

## cURL命令模板

### GET请求模板
```bash
curl -X GET "http://localhost:8080/api/redis/YOUR_ENDPOINT?param1=value1&dbIndex=1"
```

### POST请求模板（JSON Body）
```bash
curl -X POST http://localhost:8080/api/redis/YOUR_ENDPOINT \
  -H "Content-Type: application/json" \
  -d '{"param1":"value1","param2":"value2","dbIndex":1}'
```

### DELETE请求模板
```bash
curl -X DELETE "http://localhost:8080/api/redis/YOUR_ENDPOINT?param1=value1&dbIndex=1"
```

### PUT请求模板
```bash
curl -X PUT "http://localhost:8080/api/redis/YOUR_ENDPOINT?param1=value1&dbIndex=1"
```

---

## JavaScript/Fetch示例

```javascript
// GET请求
const getData = async (key, dbIndex) => {
  const response = await fetch(
    `/api/redis/get?key=${key}&dataType=string&dbIndex=${dbIndex}`
  );
  return await response.json();
};

// POST请求
const setData = async (key, value, dbIndex) => {
  const response = await fetch('/api/redis/set', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ key, value, dbIndex })
  });
  return await response.json();
};

// 使用示例
getData('user:1', 1).then(console.log);
setData('user:1', '张三', 1).then(console.log);
```

---

## Python示例

```python
import requests
import json

BASE_URL = "http://localhost:8080/api/redis"

# GET请求
def get_data(key, db_index=0):
    response = requests.get(
        f"{BASE_URL}/get",
        params={"key": key, "dataType": "string", "dbIndex": db_index}
    )
    return response.json()

# POST请求
def set_data(key, value, db_index=0):
    response = requests.post(
        f"{BASE_URL}/set",
        json={"key": key, "value": value, "dbIndex": db_index}
    )
    return response.json()

# 使用示例
print(get_data("user:1", 1))
print(set_data("user:1", "张三", 1))
```

---

## Java示例（RestTemplate）

```java
@RestController
public class RedisClientExample {
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String BASE_URL = "http://localhost:8080/api/redis";
    
    // GET请求
    public ResponseEntity<Map> getData(String key, Integer dbIndex) {
        String url = String.format("%s/get?key=%s&dataType=string&dbIndex=%d", 
            BASE_URL, key, dbIndex);
        return restTemplate.getForEntity(url, Map.class);
    }
    
    // POST请求
    public ResponseEntity<Map> setData(String key, Object value, Integer dbIndex) {
        String url = BASE_URL + "/set";
        Map<String, Object> body = new HashMap<>();
        body.put("key", key);
        body.put("value", value);
        body.put("dbIndex", dbIndex);
        return restTemplate.postForEntity(url, body, Map.class);
    }
}
```

---

## 最佳实践

1. ✅ **始终显式指定dbIndex** - 避免依赖默认数据库
2. ✅ **使用有意义的数据库编号** - 如0=开发, 1=测试, 2=生产
3. ✅ **文档化数据库用途** - 在团队内明确每个数据库的用途
4. ✅ **定期清理无用数据** - 避免Redis内存浪费
5. ✅ **监控数据库使用情况** - 关注每个数据库的内存和Key数量
6. ❌ **不要在生产环境使用flush-all** - 非常危险
7. ❌ **避免在循环中频繁切换数据库** - 影响性能

---

## 故障排查

### 问题1：设置了dbIndex但数据不在预期数据库

**检查：**
```bash
# 确认当前在哪个数据库
curl -X GET "http://localhost:8080/api/redis/current-db"

# 检查各个数据库中是否有数据
curl -X GET "http://localhost:8080/api/redis/keys?pattern=*&dbIndex=0"
curl -X GET "http://localhost:8080/api/redis/keys?pattern=*&dbIndex=1"
curl -X GET "http://localhost:8080/api/redis/keys?pattern=*&dbIndex=2"
```

### 问题2：dbIndex参数不生效

**可能原因：**
- Redis配置限制了数据库数量
- dbIndex超出范围（默认0-15）

**解决：**
```bash
# 检查Redis配置的database数量
redis-cli CONFIG GET databases

# 确保dbIndex在有效范围内
# 如果配置了16个数据库，dbIndex应该是0-15
```

### 问题3：并发操作相互干扰

**说明：**
现在的实现已经解决了这个问题，每个请求都会自动恢复原数据库。如果仍有问题，检查是否使用了旧的`select-db`接口。

**建议：**
完全弃用`select-db`接口，所有操作都通过dbIndex参数指定数据库。

---

## 总结

- 🎯 **所有API都支持dbIndex参数**
- 🔒 **线程安全，自动恢复原数据库**
- 🚀 **简单易用，减少出错**
- 📚 **向后兼容，平滑迁移**

记住：**在每次请求中直接指定dbIndex，而不是先调用select-db！**
