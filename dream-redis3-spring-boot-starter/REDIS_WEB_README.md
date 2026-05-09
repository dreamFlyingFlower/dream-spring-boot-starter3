# Redis Web管理功能

## 功能说明

本模块提供了通过Web API访问和管理Redis数据的功能，支持增删改查操作以及数据库切换。

## 主要特性

- ✅ 支持String、Hash、List、Set、ZSet五种数据类型
- ✅ 支持Key的查询、删除、过期时间设置
- ✅ 支持数据库切换（SELECT命令）
- ✅ 支持清空当前数据库或所有数据库
- ✅ 提供RESTful API接口
- ✅ 统一的响应格式

## API接口文档

### 1. Key管理

#### 获取所有Key
```
GET /api/redis/keys?pattern=*&useScan=false
```
参数：
- `pattern`: 匹配模式，默认`*`
- `useScan`: 是否使用SCAN命令，默认`false`（生产环境建议使用SCAN）

#### 判断Key是否存在
```
GET /api/redis/exists?key=test
```

#### 删除Key
```
DELETE /api/redis/delete?key=test
```

#### 批量删除Key
```
DELETE /api/redis/delete-batch
Content-Type: application/json

["key1", "key2", "key3"]
```

#### 设置Key过期时间
```
PUT /api/redis/expire?key=test&expireTime=3600
```

#### 获取Key剩余过期时间
```
GET /api/redis/ttl?key=test
```

### 2. String类型操作

#### 获取String值
```
GET /api/redis/get?key=test&dataType=string
```

#### 设置String值
```
POST /api/redis/set
Content-Type: application/json

{
  "key": "test",
  "value": "hello world",
  "expireTime": 3600
}
```

### 3. Hash类型操作

#### 获取Hash所有字段
```
GET /api/redis/get?key=myhash&dataType=hash
```

#### 获取Hash指定字段
```
GET /api/redis/get?key=myhash&dataType=hash&hashKey=field1
```

#### 设置Hash字段值
```
POST /api/redis/hash/set
Content-Type: application/json

{
  "key": "myhash",
  "hashKey": "field1",
  "value": "value1"
}
```

#### 删除Hash字段
```
DELETE /api/redis/hash/delete
Content-Type: application/json

{
  "key": "myhash",
  "value": ["field1", "field2"]
}
```

### 4. List类型操作

#### 获取List所有元素
```
GET /api/redis/get?key=mylist&dataType=list
```

#### 向List左侧添加元素
```
POST /api/redis/list/left-push
Content-Type: application/json

{
  "key": "mylist",
  "value": "item1"
}
```

#### 向List右侧添加元素
```
POST /api/redis/list/right-push
Content-Type: application/json

{
  "key": "mylist",
  "value": "item2"
}
```

### 5. Set类型操作

#### 获取Set所有成员
```
GET /api/redis/get?key=myset&dataType=set
```

#### 向Set添加成员
```
POST /api/redis/set/add
Content-Type: application/json

{
  "key": "myset",
  "value": ["member1", "member2", "member3"]
}
```

#### 从Set移除成员
```
DELETE /api/redis/set/remove
Content-Type: application/json

{
  "key": "myset",
  "value": ["member1", "member2"]
}
```

### 6. ZSet类型操作

#### 获取ZSet所有成员
```
GET /api/redis/get?key=myzset&dataType=zset
```

#### 向ZSet添加成员
```
POST /api/redis/zset/add
Content-Type: application/json

{
  "key": "myzset",
  "value": 100.5
}
```

#### 从ZSet移除成员
```
DELETE /api/redis/zset/remove
Content-Type: application/json

{
  "key": "myzset",
  "value": ["member1", "member2"]
}
```

### 7. 数据库管理

#### 切换数据库
```
POST /api/redis/select-db?dbIndex=1
```

#### 获取当前数据库索引
```
GET /api/redis/current-db
```

#### 清空当前数据库
```
DELETE /api/redis/flush-db
```

#### 清空所有数据库
```
DELETE /api/redis/flush-all
```

## 响应格式

所有API返回统一的JSON格式：

```json
{
  "success": true,
  "message": "操作成功",
  "data": {},
  "keys": [],
  "currentDbIndex": 0
}
```

字段说明：
- `success`: 操作是否成功
- `message`: 操作消息
- `data`: 操作结果数据
- `keys`: Key列表（仅keys接口返回）
- `currentDbIndex`: 当前数据库索引（仅相关接口返回）

## 使用示例

### 使用cURL测试

```bash
# 设置String值
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"test","value":"hello","expireTime":300}'

# 获取String值
curl -X GET "http://localhost:8080/api/redis/get?key=test&dataType=string"

# 切换数据库
curl -X POST "http://localhost:8080/api/redis/select-db?dbIndex=2"

# 获取所有Key
curl -X GET "http://localhost:8080/api/redis/keys?pattern=*"
```

### 使用Postman测试

1. 设置请求方法为POST/GET/DELETE/PUT
2. 设置请求URL
3. 对于POST/PUT请求，在Body中选择raw，格式选择JSON
4. 发送请求并查看响应

## 注意事项

1. **安全性**: 该模块提供了完整的Redis管理功能，建议在生产环境中添加权限控制
2. **性能**: 获取所有Key时，如果数据量大，建议使用SCAN模式（`useScan=true`）
3. **数据库切换**: Redis数据库切换是基于连接的，每次请求后会恢复到默认数据库
4. **数据类型**: 确保传入正确的dataType参数，否则可能无法正确获取数据

## 技术实现

- 基于Spring Boot 3.x
- 使用Spring Data Redis
- 支持Lombok简化代码
- 自动配置，无需手动注册Bean

## 作者

飞花梦影

## 日期

2026-05-09
