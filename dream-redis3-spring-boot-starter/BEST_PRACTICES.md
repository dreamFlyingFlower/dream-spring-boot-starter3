# Redis Web管理 - 最佳实践指南

## 📚 目录

1. [快速开始](#快速开始)
2. [核心概念](#核心概念)
3. [使用场景](#使用场景)
4. [性能优化](#性能优化)
5. [安全建议](#安全建议)
6. [常见问题](#常见问题)

---

## 快速开始

### 1. 添加依赖

在您的Spring Boot项目中添加依赖：

```xml
<dependency>
    <groupId>dream.flying.flower</groupId>
    <artifactId>dream-redis3-spring-boot-starter</artifactId>
    <version>0.0.1</version>
</dependency>
```

### 2. 配置Redis

在`application.yml`中配置：

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your_password  # 如果有密码
    database: 0  # 默认数据库
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms
```

### 3. 启动应用

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 4. 测试API

```bash
# 测试设置值
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"test","value":"hello","dbIndex":0}'

# 测试获取值
curl -X GET "http://localhost:8080/api/redis/get?key=test&dataType=string&dbIndex=0"
```

---

## 核心概念

### 智能数据库切换

本模块实现了**智能数据库切换机制**：

```
请求到达 → 检查dbIndex参数
           ↓
    dbIndex == 当前数据库？
     ↙              ↘
   YES              NO
    ↓                ↓
 直接执行       切换到目标数据库
 Redis操作      执行Redis操作
               恢复原数据库
```

**优势：**
- ✅ 减少不必要的SELECT命令
- ✅ 提升性能（最多节省53%的命令调用）
- ✅ 保持连接状态稳定
- ✅ 线程安全，互不干扰

### 数据类型支持

| 类型 | 说明 | 典型用途 |
|------|------|----------|
| String | 字符串 | 缓存、计数器 |
| Hash | 哈希表 | 对象存储 |
| List | 列表 | 消息队列 |
| Set | 集合 | 标签、好友关系 |
| ZSet | 有序集合 | 排行榜 |

---

## 使用场景

### 场景1：多租户数据隔离

**需求：** 不同租户的数据存储在独立的数据库中

```bash
# 租户A（数据库1）
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"user:1001","value":"Tenant A User","dbIndex":1}'

# 租户B（数据库2）
curl -X POST http://localhost:8080/api/redis/set \
  -H "Content-Type: application/json" \
  -d '{"key":"user:1001","value":"Tenant B User","dbIndex":2}'

# 查询租户A的数据
curl -X GET "http://localhost:8080/api/redis/get?key=user:1001&dataType=string&dbIndex=1"
# 返回: "Tenant A User"

# 查询租户B的数据
curl -X GET "http://localhost:8080/api/redis/get?key=user:1001&dataType=string&dbIndex=2"
# 返回: "Tenant B User"
```

**Java代码示例：**

```java
@Service
public class TenantService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String REDIS_BASE_URL = "http://localhost:8080/api/redis";
    
    public void setUserData(Long tenantId, String userId, String userData) {
        // 根据租户ID选择数据库
        int dbIndex = tenantId.intValue() % 16;  // 假设最多16个数据库
        
        Map<String, Object> request = new HashMap<>();
        request.put("key", "user:" + userId);
        request.put("value", userData);
        request.put("dbIndex", dbIndex);
        
        restTemplate.postForObject(
            REDIS_BASE_URL + "/set", 
            request, 
            Map.class
        );
    }
    
    public String getUserData(Long tenantId, String userId) {
        int dbIndex = tenantId.intValue() % 16;
        
        ResponseEntity<Map> response = restTemplate.getForEntity(
            REDIS_BASE_URL + "/get?key=user:{userId}&dataType=string&dbIndex={dbIndex}",
            Map.class,
            userId,
            dbIndex
        );
        
        return (String) response.getBody().get("data");
    }
}
```

### 场景2：环境隔离

**需求：** 开发、测试、生产环境使用不同的数据库

```yaml
# application-dev.yml
app:
  redis:
    db-index: 0  # 开发环境

# application-test.yml
app:
  redis:
    db-index: 1  # 测试环境

# application-prod.yml
app:
  redis:
    db-index: 2  # 生产环境
```

```java
@Component
public class RedisConfigProvider {
    
    @Value("${app.redis.db-index}")
    private Integer defaultDbIndex;
    
    public Integer getDefaultDbIndex() {
        return defaultDbIndex;
    }
}
```

### 场景3：缓存分层

**需求：** 根据数据热度分层存储

```java
@Service
public class CacheService {
    
    private static final int HOT_DB = 0;      // 热点数据
    private static final int NORMAL_DB = 1;   // 普通数据
    private static final int COLD_DB = 2;     // 冷数据
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * 根据访问频率选择数据库
     */
    public void cacheData(String key, Object value, int accessCount) {
        int dbIndex;
        long expireTime;
        
        if (accessCount > 1000) {
            // 热点数据：数据库0，过期时间短
            dbIndex = HOT_DB;
            expireTime = 300;  // 5分钟
        } else if (accessCount > 100) {
            // 普通数据：数据库1，过期时间中等
            dbIndex = NORMAL_DB;
            expireTime = 3600;  // 1小时
        } else {
            // 冷数据：数据库2，过期时间长
            dbIndex = COLD_DB;
            expireTime = 86400;  // 24小时
        }
        
        Map<String, Object> request = new HashMap<>();
        request.put("key", key);
        request.put("value", value);
        request.put("expireTime", expireTime);
        request.put("dbIndex", dbIndex);
        
        restTemplate.postForObject(
            "http://localhost:8080/api/redis/set",
            request,
            Map.class
        );
    }
}
```

### 场景4：会话管理

**需求：** 用户会话存储，支持快速查询和清理

```java
@Service
public class SessionService {
    
    private static final int SESSION_DB = 3;
    private static final long SESSION_TIMEOUT = 1800;  // 30分钟
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * 创建会话
     */
    public String createSession(String userId, Map<String, Object> sessionData) {
        String sessionId = UUID.randomUUID().toString();
        String key = "session:" + sessionId;
        
        // 存储会话数据
        Map<String, Object> request = new HashMap<>();
        request.put("key", key);
        request.put("value", sessionData);
        request.put("expireTime", SESSION_TIMEOUT);
        request.put("dbIndex", SESSION_DB);
        
        restTemplate.postForObject(
            "http://localhost:8080/api/redis/set",
            request,
            Map.class
        );
        
        return sessionId;
    }
    
    /**
     * 获取会话
     */
    public Map<String, Object> getSession(String sessionId) {
        String key = "session:" + sessionId;
        
        ResponseEntity<Map> response = restTemplate.getForEntity(
            "http://localhost:8080/api/redis/get?key={key}&dataType=string&dbIndex={dbIndex}",
            Map.class,
            key,
            SESSION_DB
        );
        
        return (Map<String, Object>) response.getBody().get("data");
    }
    
    /**
     * 销毁会话
     */
    public void destroySession(String sessionId) {
        String key = "session:" + sessionId;
        
        restTemplate.delete(
            "http://localhost:8080/api/redis/delete?key={key}&dbIndex={dbIndex}",
            key,
            SESSION_DB
        );
    }
}
```

---

## 性能优化

### 1. 批量操作

**❌ 不推荐：循环单个操作**

```java
// 性能差：每次请求都有网络开销
for (String key : keys) {
    restTemplate.delete(
        "http://localhost:8080/api/redis/delete?key=" + key + "&dbIndex=1"
    );
}
```

**✅ 推荐：使用批量接口**

```java
// 性能好：一次请求删除多个Key
List<String> keys = Arrays.asList("key1", "key2", "key3");
Map<String, Object> request = new HashMap<>();
request.put("keys", keys);
request.put("dbIndex", 1);

restTemplate.exchange(
    "http://localhost:8080/api/redis/delete-batch?dbIndex=1",
    HttpMethod.DELETE,
    new HttpEntity<>(request),
    Map.class
);
```

### 2. 合理设置过期时间

**❌ 不推荐：永不过期**

```json
{
  "key": "cache:data",
  "value": "some value"
  // 没有设置expireTime
}
```

**✅ 推荐：设置合理的过期时间**

```json
{
  "key": "cache:data",
  "value": "some value",
  "expireTime": 3600  // 1小时后自动过期
}
```

### 3. 使用SCAN代替KEYS

**❌ 不推荐：KEYS命令（阻塞Redis）**

```bash
curl -X GET "http://localhost:8080/api/redis/keys?pattern=*"
```

**✅ 推荐：SCAN命令（非阻塞）**

```bash
curl -X GET "http://localhost:8080/api/redis/keys?pattern=*&useScan=true"
```

### 4. 连接池优化

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

---

## 安全建议

### 1. 添加认证

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/api/redis/**").hasRole("ADMIN")  // 仅管理员可访问
            .anyRequest().authenticated()
            .and()
            .httpBasic();
    }
}
```

### 2. 限制危险操作

```java
@Component
public class RedisOperationFilter implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) {
        String uri = request.getRequestURI();
        
        // 禁止清空所有数据库
        if (uri.contains("/flush-all")) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return false;
        }
        
        return true;
    }
}
```

### 3. IP白名单

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info
  server:
    address: 127.0.0.1  # 仅本地访问
```

### 4. 使用HTTPS

```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: changeit
    key-store-type: PKCS12
```

---

## 常见问题

### Q1: 为什么设置了dbIndex但数据不在预期数据库？

**A:** 检查以下几点：
1. 确认dbIndex在有效范围内（默认0-15）
2. 查看Redis配置的database数量：`CONFIG GET databases`
3. 使用MONITOR命令观察实际执行的SELECT命令

```bash
redis-cli MONITOR
```

### Q2: 如何监控数据库切换的性能？

**A:** 可以添加自定义指标：

```java
@Component
public class RedisMetrics {
    
    private final MeterRegistry meterRegistry;
    private final AtomicLong switchCount = new AtomicLong(0);
    private final AtomicLong skipCount = new AtomicLong(0);
    
    public void recordSwitch() {
        switchCount.incrementAndGet();
    }
    
    public void recordSkip() {
        skipCount.incrementAndGet();
    }
    
    @Scheduled(fixedRate = 60000)  // 每分钟上报
    public void reportMetrics() {
        meterRegistry.counter("redis.db.switch.count").increment(switchCount.get());
        meterRegistry.counter("redis.db.skip.count").increment(skipCount.get());
        
        switchCount.set(0);
        skipCount.set(0);
    }
}
```

### Q3: 并发访问会相互影响吗？

**A:** 不会。每个请求都是独立的：
- 使用局部变量保存originalDb
- finally块确保恢复数据库
- 连接池保证连接隔离

### Q4: 如何选择数据库数量？

**A:** 建议：
- 小规模应用：4-8个数据库
- 中等规模：8-16个数据库
- 大规模：考虑使用Redis Cluster而非多数据库

### Q5: 数据库切换会影响性能吗？

**A:** 优化后影响很小：
- 相同数据库：0额外开销
- 不同数据库：2次SELECT命令（约0.1ms）
- 相比网络延迟（1-10ms）可忽略不计

---

## 总结

### ✅ 最佳实践清单

- [x] 显式指定dbIndex参数
- [x] 使用批量操作接口
- [x] 设置合理的过期时间
- [x] 使用SCAN代替KEYS
- [x] 配置合适的连接池
- [x] 添加API认证
- [x] 限制危险操作
- [x] 监控性能指标
- [x] 定期清理无用数据
- [x] 文档化数据库用途

### 📊 性能对比

| 场景 | 优化前 | 优化后 | 提升 |
|------|--------|--------|------|
| 同数据库操作 | 3命令 | 1命令 | **67%** |
| 异数据库操作 | 3命令 | 3命令 | 0% |
| 混合场景(80%同库) | 3000命令/s | 1400命令/s | **53%** |

### 🚀 下一步

1. 阅读[API文档](API_TEST_EXAMPLES.md)
2. 查看[测试指南](DB_SWITCH_OPTIMIZATION_TEST.md)
3. 参考[代码示例](CODE_COMPARISON_EXAMPLE.md)
4. 运行单元测试验证功能

---

**文档版本**: 1.0  
**更新日期**: 2026-05-09  
**作者**: AI Assistant
