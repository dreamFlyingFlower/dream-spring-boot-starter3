# Redis Web API 测试用例

## 测试环境准备

1. 确保Redis服务已启动
2. 在application.yml或application.properties中配置Redis连接信息：
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: # 如果有密码
    database: 0
```

3. 启动Spring Boot应用

## API测试用例

### 重要说明：数据库选择

**所有API都支持通过`dbIndex`参数指定目标数据库：**
- GET请求：使用查询参数 `?dbIndex=1`
- POST/PUT/DELETE请求：在JSON body中添加 `"dbIndex": 1` 或在URL中添加查询参数

**如果不指定dbIndex，则使用当前默认数据库（通常为0）**

### 1. 字符串(String)操作

#### 设置String值（指定数据库）
```bash
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{
    "key": "user:name",
    "value": "张三",
    "expireTime": 3600,
    "dbIndex": 1
  }'
```

预期响应：
```json
{
  "success": true,
  "message": "设置成功",
  "data": null
}
```

#### 获取String值（从指定数据库）
```bash
curl -X GET "http://localhost:8080/api/redis/get?key=user:name&dataType=string&dbIndex=1"
```

预期响应：
```json
{
  "success": true,
  "message": "操作成功",
  "data": "张三"
}
```

#### 设置不过期的String值（指定数据库2）
```bash
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{
    "key": "config:version",
    "value": "1.0.0",
    "dbIndex": 2
  }'
```

### 2. 哈希(Hash)操作

#### 设置Hash字段（指定数据库）
```bash
curl -X POST http://localhost:8080/api/redis/hash/set \
  -H "Content-Type: application/json" \
  -d '{
    "key": "user:1001",
    "hashKey": "name",
    "value": "李四",
    "dbIndex": 1
  }'
```

#### 设置多个Hash字段
```bash
curl -X POST http://localhost:8080/api/redis/hash/set \
  -H "Content-Type: application/json" \
  -d '{
    "key": "user:1001",
    "hashKey": "age",
    "value": 25
  }'

curl -X POST http://localhost:8080/api/redis/hash/set \
  -H "Content-Type: application/json" \
  -d '{
    "key": "user:1001",
    "hashKey": "email",
    "value": "lisi@example.com"
  }'
```

#### 获取Hash所有字段（从指定数据库）
```bash
curl -X GET "http://localhost:8080/api/redis/get?key=user:1001&dataType=hash&dbIndex=1"
```

预期响应：
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "name": "李四",
    "age": 25,
    "email": "lisi@example.com"
  }
}
```

#### 获取Hash指定字段
```bash
curl -X GET "http://localhost:8080/api/redis/get?key=user:1001&dataType=hash&hashKey=name"
```

#### 删除Hash字段
```bash
curl -X DELETE http://localhost:8080/api/redis/hash/delete \
  -H "Content-Type: application/json" \
  -d '{
    "key": "user:1001",
    "value": ["age"]
  }'
```

### 3. 列表(List)操作

#### 向List右侧添加元素
```bash
curl -X POST http://localhost:8080/api/redis/list/right-push \
  -H "Content-Type: application/json" \
  -d '{
    "key": "messages",
    "value": "第一条消息"
  }'

curl -X POST http://localhost:8080/api/redis/list/right-push \
  -H "Content-Type: application/json" \
  -d '{
    "key": "messages",
    "value": "第二条消息"
  }'
```

#### 向List左侧添加元素
```bash
curl -X POST http://localhost:8080/api/redis/list/left-push \
  -H "Content-Type: application/json" \
  -d '{
    "key": "messages",
    "value": "紧急消息"
  }'
```

#### 获取List所有元素
```bash
curl -X GET "http://localhost:8080/api/redis/get?key=messages&dataType=list"
```

预期响应：
```json
{
  "success": true,
  "message": "操作成功",
  "data": ["紧急消息", "第一条消息", "第二条消息"]
}
```

### 4. 集合(Set)操作

#### 向Set添加成员
```bash
curl -X POST http://localhost:8080/api/redis/set/add \
  -H "Content-Type: application/json" \
  -d '{
    "key": "tags",
    "value": ["Java", "Spring", "Redis"]
  }'
```

#### 获取Set所有成员
```bash
curl -X GET "http://localhost:8080/api/redis/get?key=tags&dataType=set"
```

#### 从Set移除成员
```bash
curl -X DELETE http://localhost:8080/api/redis/set/remove \
  -H "Content-Type: application/json" \
  -d '{
    "key": "tags",
    "value": ["Java"]
  }'
```

### 5. 有序集合(ZSet)操作

#### 向ZSet添加成员
```bash
curl -X POST http://localhost:8080/api/redis/zset/add \
  -H "Content-Type: application/json" \
  -d '{
    "key": "leaderboard",
    "value": 100.5
  }'
```

注意：当前实现中，value既是成员名也是分数。如需更灵活的ZSet操作，可以扩展API。

#### 获取ZSet所有成员
```bash
curl -X GET "http://localhost:8080/api/redis/get?key=leaderboard&dataType=zset"
```

### 6. Key管理

#### 获取所有Key
```bash
curl -X GET "http://localhost:8080/api/redis/keys?pattern=*"
```

#### 使用SCAN获取Key（推荐生产环境）
```bash
curl -X GET "http://localhost:8080/api/redis/keys?pattern=user:*&useScan=true"
```

#### 判断Key是否存在
```bash
curl -X GET "http://localhost:8080/api/redis/exists?key=user:name"
```

预期响应：
```json
{
  "success": true,
  "message": "操作成功",
  "data": true
}
```

#### 设置Key过期时间
```bash
curl -X PUT "http://localhost:8080/api/redis/expire?key=user:name&expireTime=600"
```

#### 获取Key剩余过期时间
```bash
curl -X GET "http://localhost:8080/api/redis/ttl?key=user:name"
```

预期响应：
```json
{
  "success": true,
  "message": "操作成功",
  "data": 580
}
```

#### 删除单个Key（从指定数据库）
```bash
curl -X DELETE "http://localhost:8080/api/redis/delete?key=user:name&dbIndex=1"
```

#### 批量删除Key
```bash
curl -X DELETE http://localhost:8080/api/redis/delete-batch \
  -H "Content-Type: application/json" \
  -d '["key1", "key2", "key3"]'
```

### 7. 数据库管理

#### 获取当前数据库索引
```bash
curl -X GET "http://localhost:8080/api/redis/current-db"
```

预期响应：
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "dbIndex": 0
  }
}
```

#### 切换数据库
```bash
curl -X POST "http://localhost:8080/api/redis/select-db?dbIndex=1"
```

预期响应：
```json
{
  "success": true,
  "message": "切换数据库成功",
  "data": {
    "dbIndex": 1
  }
}
```

#### 清空当前数据库（谨慎使用！）
```bash
curl -X DELETE "http://localhost:8080/api/redis/flush-db"
```

#### 清空所有数据库（谨慎使用！）
```bash
curl -X DELETE "http://localhost:8080/api/redis/flush-all"
```

## 完整测试流程示例

```bash
# 1. 在数据库1中设置一个String值
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"test:key","value":"test value in db1","expireTime":300,"dbIndex":1}'

# 2. 从数据库1获取该值
curl -X GET "http://localhost:8080/api/redis/get?key=test:key&dataType=string&dbIndex=1"

# 3. 在数据库2中设置同名Key（不同值）
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"test:key","value":"test value in db2","expireTime":300,"dbIndex":2}'

# 4. 从数据库2获取该值（应该返回不同的值）
curl -X GET "http://localhost:8080/api/redis/get?key=test:key&dataType=string&dbIndex=2"

# 5. 查看数据库1中的TTL
curl -X GET "http://localhost:8080/api/redis/ttl?key=test:key&dbIndex=1"

# 6. 查看数据库1中的所有Key
curl -X GET "http://localhost:8080/api/redis/keys?pattern=test:*&dbIndex=1"

# 7. 从数据库1删除该Key
curl -X DELETE "http://localhost:8080/api/redis/delete?key=test:key&dbIndex=1"

# 8. 验证数据库1中是否已删除
curl -X GET "http://localhost:8080/api/redis/exists?key=test:key&dbIndex=1"

# 9. 验证数据库2中的Key仍然存在
curl -X GET "http://localhost:8080/api/redis/exists?key=test:key&dbIndex=2"
```

**注意：** 以上示例展示了如何在不同数据库中独立操作相同的Key，互不影响！

## 错误处理

当操作失败时，会返回如下格式的错误响应：

```json
{
  "success": false,
  "message": "错误描述信息",
  "data": null
}
```

常见错误：
- Redis连接失败
- Key不存在
- 数据类型不匹配
- 参数错误

## 性能建议

1. **生产环境使用SCAN**: 获取Key时使用`useScan=true`避免阻塞Redis
2. **批量操作**: 尽量使用批量删除等批量操作接口
3. **合理设置过期时间**: 避免Redis内存无限增长
4. **监控Redis性能**: 关注Redis的内存使用和响应时间

## 安全建议

1. **添加权限控制**: 在生产环境中为API添加认证和授权
2. **限制危险操作**: 考虑禁用或限制`flush-db`和`flush-all`操作
3. **使用HTTPS**: 在生产环境中使用HTTPS保护数据传输
4. **IP白名单**: 限制访问API的IP地址范围
