package dream.flying.flower.web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import dream.flying.flower.autoconfigure.redis.helper.RedisFactoryHelpers;
import dream.flying.flower.web.dto.RedisDataRequest;
import dream.flying.flower.web.monitor.RedisDbSwitchMonitor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis管理服务类
 *
 * @author 飞花梦影
 * @date 2026-05-09 15:36:00
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Service
public class RedisManageService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private RedisConnectionFactory defaultConnectionFactory;

	@Autowired
	private RedisProperties redisProperties;

	@Autowired
	private RedisFactoryHelpers redisFactoryHelpers;

	@Autowired(required = false)
	private RedisDbSwitchMonitor monitor;

	/**
	 * 执行Redis操作(线程安全)
	 * <p>
	 * 为每个操作创建独立的连接,操作完成后关闭连接
	 * </p>
	 *
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @param operation Redis操作函数
	 * @return 操作结果
	 * @throws Exception
	 */
	private <T> T executeWithConnection(Integer dbIndex, RedisOperation<T> operation) throws Exception {
		try (RedisConnection connection = redisFactoryHelpers.getConnectionFactory(dbIndex).getConnection();) {
			// 执行操作
			return operation.execute(connection);
		} catch (Exception e) {
			log.error("Redis操作失败", e);
			throw e;
		} finally {
			// 记录监控数据
			if (monitor != null) {
				monitor.recordRequest(dbIndex, dbIndex != null);
			}
		}
	}

	/**
	 * Redis操作函数式接口
	 */
	@FunctionalInterface
	private interface RedisOperation<T> {

		T execute(RedisConnection connection) throws Exception;
	}

	/**
	 * 反序列化值
	 *
	 * @param value 字节数组
	 * @return 反序列化后的对象
	 */
	private Object deserializeValue(byte[] value) {
		if (value == null) {
			return null;
		}
		try {
			return redisTemplate.getValueSerializer().deserialize(value);
		} catch (Exception e) {
			// 如果反序列化失败,尝试转换为字符串
			return new String(value);
		}
	}

	/**
	 * 获取所有Key
	 *
	 * @param pattern 匹配模式
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return Key列表
	 */
	public List<String> keys(String pattern, Integer dbIndex) {
		RedisConnection connection = null;
		try {
			// 创建新的连接
			connection = defaultConnectionFactory.getConnection();

			// 如果指定了数据库索引,切换到目标数据库
			if (dbIndex != null) {
				connection.select(dbIndex);
			}

			List<String> result = new ArrayList<>();
			Set<byte[]> keys = connection.keyCommands().keys(pattern.getBytes());
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
			// 关闭连接
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					log.error("关闭连接失败", e);
				}
			}
			// 记录监控数据
			if (monitor != null) {
				monitor.recordRequest(dbIndex, dbIndex != null);
			}
		}
	}

	/**
	 * 使用SCAN命令获取Key(推荐用于生产环境)
	 *
	 * @param pattern 匹配模式
	 * @param count 每次扫描数量
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return Key列表
	 */
	public List<String> scanKeys(String pattern, int count, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				List<String> result = new ArrayList<>();
				ScanOptions options = ScanOptions.scanOptions().match(pattern).count(count).build();
				Cursor<byte[]> cursor = connection.keyCommands().scan(options);
				while (cursor.hasNext()) {
					result.add(new String(cursor.next()));
				}
				cursor.close();
				return result;
			});
		} catch (Exception e) {
			log.error("扫描Keys失败", e);
			return new ArrayList<>();
		}
	}

	/**
	 * 获取字符串值
	 *
	 * @param key 键
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return 值
	 */
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

	@SuppressWarnings("unchecked")
	private byte[] serialize(Object value) {
		return ((RedisSerializer<Object>) redisTemplate.getValueSerializer()).serialize(value);
	}

	/**
	 * 设置字符串值
	 *
	 * @param key 键
	 * @param value 值
	 * @param expireTime 过期时间(秒)
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return 是否成功
	 */
	public <T> Boolean setString(String key, T value, Long expireTime, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				byte[] keyBytes = key.getBytes();
				byte[] valueBytes = serialize(value);

				if (expireTime != null && expireTime > 0) {
					connection.stringCommands().setEx(keyBytes, expireTime, valueBytes);
				} else {
					connection.stringCommands().set(keyBytes, valueBytes);
				}
				connection.close();
				return true;
			});
		} catch (Exception e) {
			log.error("设置String值失败, key: {}", key, e);
			return false;
		}
	}

	/**
	 * 删除Key
	 *
	 * @param key 键
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return 是否成功
	 */
	public Boolean delete(String key, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				Long result = connection.keyCommands().del(key.getBytes());
				return result != null && result > 0;
			});
		} catch (Exception e) {
			log.error("删除Key失败, key: {}", key, e);
			return false;
		}
	}

	/**
	 * 批量删除Key
	 *
	 * @param keys 键列表
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return 删除数量
	 */
	public Long deleteBatch(List<String> keys, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				byte[][] keyBytes = keys.stream().map(String::getBytes).toArray(byte[][]::new);
				return connection.keyCommands().del(keyBytes);
			});
		} catch (Exception e) {
			log.error("批量删除Key失败", e);
			return 0L;
		}
	}

	/**
	 * 判断Key是否存在
	 *
	 * @param key 键
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return 是否存在
	 */
	public Boolean exists(String key, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				return connection.keyCommands().exists(key.getBytes());
			});
		} catch (Exception e) {
			log.error("判断Key存在性失败, key: {}", key, e);
			return false;
		}
	}

	/**
	 * 设置Key过期时间
	 *
	 * @param key 键
	 * @param expireTime 过期时间(秒)
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return 是否成功
	 */
	public Boolean expire(String key, Long expireTime, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				return connection.keyCommands().expire(key.getBytes(), expireTime);
			});
		} catch (Exception e) {
			log.error("设置过期时间失败, key: {}", key, e);
			return false;
		}
	}

	/**
	 * 获取Key剩余过期时间
	 *
	 * @param key 键
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return 剩余时间(秒)
	 */
	public Long getExpire(String key, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				return connection.keyCommands().ttl(key.getBytes());
			});
		} catch (Exception e) {
			log.error("获取过期时间失败, key: {}", key, e);
			return -1L;
		}
	}

	/**
	 * 获取Hash所有字段
	 *
	 * @param key 键
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return Hash数据
	 */
	public Map<Object, Object> getHash(String key, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				Map<byte[], byte[]> entries = connection.hashCommands().hGetAll(key.getBytes());
				if (entries == null || entries.isEmpty()) {
					return new java.util.HashMap<>();
				}
				Map<Object, Object> result = new java.util.HashMap<>();
				for (Map.Entry<byte[], byte[]> entry : entries.entrySet()) {
					Object fieldKey = deserializeValue(entry.getKey());
					Object fieldValue = deserializeValue(entry.getValue());
					result.put(fieldKey, fieldValue);
				}
				return result;
			});
		} catch (Exception e) {
			log.error("获取Hash数据失败, key: {}", key, e);
			return null;
		}
	}

	/**
	 * 获取Hash指定字段值
	 *
	 * @param key 键
	 * @param hashKey 字段键
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return 字段值
	 */
	public Object getHashField(String key, String hashKey, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				byte[] value = connection.hashCommands().hGet(key.getBytes(), hashKey.getBytes());
				return deserializeValue(value);
			});
		} catch (Exception e) {
			log.error("获取Hash字段值失败, key: {}, hashKey: {}", key, hashKey, e);
			return null;
		}
	}

	/**
	 * 设置Hash字段值
	 *
	 * @param key 键
	 * @param hashKey 字段键
	 * @param value 值
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return 是否成功
	 */
	public Boolean setHashField(String key, String hashKey, Object value, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				byte[] keyBytes = key.getBytes();
				byte[] fieldBytes = hashKey.getBytes();
				byte[] valueBytes = serialize(value);
				connection.hashCommands().hSet(keyBytes, fieldBytes, valueBytes);
				return true;
			});
		} catch (Exception e) {
			log.error("设置Hash字段值失败, key: {}, hashKey: {}", key, hashKey, e);
			return false;
		}
	}

	/**
	 * 删除Hash字段
	 *
	 * @param key 键
	 * @param hashKeys 字段键列表
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return 删除数量
	 */
	public Long deleteHashFields(String key, List<String> hashKeys, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				byte[] keyBytes = key.getBytes();
				byte[][] fieldBytes = hashKeys.stream().map(String::getBytes).toArray(byte[][]::new);
				return connection.hashCommands().hDel(keyBytes, fieldBytes);
			});
		} catch (Exception e) {
			log.error("删除Hash字段失败, key: {}", key, e);
			return 0L;
		}
	}

	/**
	 * 获取List所有元素
	 *
	 * @param key 键
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return List数据
	 */
	public List<Object> getList(String key, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				List<byte[]> values = connection.listCommands().lRange(key.getBytes(), 0, -1);
				if (values == null || values.isEmpty()) {
					return new ArrayList<>();
				}
				List<Object> result = new ArrayList<>();
				for (byte[] value : values) {
					result.add(deserializeValue(value));
				}
				return result;
			});
		} catch (Exception e) {
			log.error("获取List数据失败, key: {}", key, e);
			return null;
		}
	}

	/**
	 * 向List左侧添加元素
	 *
	 * @param key 键
	 * @param value 值
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return List长度
	 */
	public Long leftPushList(String key, Object value, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				byte[] keyBytes = key.getBytes();
				byte[] valueBytes = serialize(value);
				return connection.listCommands().lPush(keyBytes, valueBytes);
			});
		} catch (Exception e) {
			log.error("左侧添加List元素失败, key: {}", key, e);
			return 0L;
		}
	}

	/**
	 * 向List右侧添加元素
	 *
	 * @param key 键
	 * @param value 值
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return List长度
	 */
	public Long rightPushList(String key, Object value, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				byte[] keyBytes = key.getBytes();
				byte[] valueBytes = serialize(value);
				return connection.listCommands().rPush(keyBytes, valueBytes);
			});
		} catch (Exception e) {
			log.error("右侧添加List元素失败, key: {}", key, e);
			return 0L;
		}
	}

	/**
	 * 从List中移除元素
	 *
	 * @param key 键
	 * @param count 移除数量
	 * @param value 值
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return 移除数量
	 */
	public Long removeListElement(String key, long count, Object value, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				byte[] keyBytes = key.getBytes();
				byte[] valueBytes = serialize(value);
				return connection.listCommands().lRem(keyBytes, count, valueBytes);
			});
		} catch (Exception e) {
			log.error("移除List元素失败, key: {}", key, e);
			return 0L;
		}
	}

	/**
	 * 获取Set所有成员
	 *
	 * @param key 键
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return Set数据
	 */
	public Set<Object> getSet(String key, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				Set<byte[]> members = connection.setCommands().sMembers(key.getBytes());
				if (members == null || members.isEmpty()) {
					return new java.util.HashSet<>();
				}
				Set<Object> result = new java.util.HashSet<>();
				for (byte[] member : members) {
					result.add(deserializeValue(member));
				}
				return result;
			});
		} catch (Exception e) {
			log.error("获取Set数据失败, key: {}", key, e);
			return null;
		}
	}

	/**
	 * 向Set添加成员
	 *
	 * @param key 键
	 * @param values 值列表
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return 添加数量
	 */
	public Long addSetMembers(String key, List<Object> values, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				byte[] keyBytes = key.getBytes();
				byte[][] valueBytes = values.stream().map(v -> serialize(v)).toArray(byte[][]::new);
				return connection.setCommands().sAdd(keyBytes, valueBytes);
			});
		} catch (Exception e) {
			log.error("添加Set成员失败, key: {}", key, e);
			return 0L;
		}
	}

	/**
	 * 从Set移除成员
	 *
	 * @param key 键
	 * @param values 值列表
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return 移除数量
	 */
	public Long removeSetMembers(String key, List<Object> values, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				byte[] keyBytes = key.getBytes();
				byte[][] valueBytes = values.stream().map(v -> serialize(v)).toArray(byte[][]::new);
				return connection.setCommands().sRem(keyBytes, valueBytes);
			});
		} catch (Exception e) {
			log.error("移除Set成员失败, key: {}", key, e);
			return 0L;
		}
	}

	/**
	 * 获取ZSet所有成员(带分数)
	 *
	 * @param key 键
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return ZSet数据
	 */
	public Set<Object> getZSet(String key, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				Set<byte[]> members = connection.zSetCommands().zRange(key.getBytes(), 0, -1);
				if (members == null || members.isEmpty()) {
					return new java.util.HashSet<>();
				}
				Set<Object> result = new java.util.HashSet<>();
				for (byte[] member : members) {
					result.add(deserializeValue(member));
				}
				return result;
			});
		} catch (Exception e) {
			log.error("获取ZSet数据失败, key: {}", key, e);
			return null;
		}
	}

	/**
	 * 向ZSet添加成员
	 *
	 * @param key 键
	 * @param value 值
	 * @param score 分数
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return 是否成功
	 */
	public Boolean addZSetMember(String key, Object value, double score, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				byte[] keyBytes = key.getBytes();
				byte[] valueBytes = serialize(value);
				return connection.zSetCommands().zAdd(keyBytes, score, valueBytes);
			});
		} catch (Exception e) {
			log.error("添加ZSet成员失败, key: {}", key, e);
			return false;
		}
	}

	/**
	 * 从ZSet移除成员
	 *
	 * @param key 键
	 * @param values 值列表
	 * @param dbIndex 数据库索引,null则使用默认数据库
	 * @return 移除数量
	 */
	public Long removeZSetMembers(String key, List<Object> values, Integer dbIndex) {
		try {
			return executeWithConnection(dbIndex, connection -> {
				byte[] keyBytes = key.getBytes();
				byte[][] valueBytes = values.stream().map(v -> serialize(v)).toArray(byte[][]::new);
				return connection.zSetCommands().zRem(keyBytes, valueBytes);
			});
		} catch (Exception e) {
			log.error("移除ZSet成员失败, key: {}", key, e);
			return 0L;
		}
	}

	/**
	 * 获取当前数据库索引
	 *
	 * @return 数据库索引
	 */
	public Integer getCurrentDatabase() {
		return redisProperties.getDatabase();
	}

	/**
	 * 清空数据库,若不指定数据库索引,清除当前数据库
	 *
	 * @return 是否成功
	 */
	public Boolean flushDb(Integer databaseIndex) {
		try {
			RedisConnection connection = redisFactoryHelpers.getConnectionFactory(databaseIndex).getConnection();
			connection.serverCommands().flushDb();
			connection.close();
			log.info("清空当前数据库成功");
			return true;
		} catch (Exception e) {
			log.error("清空数据库失败", e);
			return false;
		}
	}

	/**
	 * 清空所有数据库
	 *
	 * @return 是否成功
	 */
	public Boolean flushAll() {
		try {
			RedisConnection connection = redisFactoryHelpers.getConnectionFactory().getConnection();
			connection.serverCommands().flushAll();
			connection.close();
			log.info("清空所有数据库成功");
			return true;
		} catch (Exception e) {
			log.error("清空所有数据库失败", e);
			return false;
		}
	}

	/**
	 * 根据数据类型执行通用操作
	 *
	 * @param request 请求参数
	 * @return 操作结果
	 */
	public Object executeByDataType(RedisDataRequest request) {
		String requestType = request.getDataType();
		Integer dbIndex = request.getDbIndex();
		if (requestType == null) {
			return getString(request.getKey(), dbIndex);
		}

		DataType dataType = DataType.fromCode(requestType);

		switch (dataType) {
		case STRING:
			return getString(request.getKey(), dbIndex);
		case HASH:
			if (request.getHashKey() != null) {
				return getHashField(request.getKey(), request.getHashKey(), dbIndex);
			}
			return getHash(request.getKey(), dbIndex);
		case LIST:
			return getList(request.getKey(), dbIndex);
		case SET:
			return getSet(request.getKey(), dbIndex);
		case ZSET:
			return getZSet(request.getKey(), dbIndex);
		default:
			return getString(request.getKey(), dbIndex);
		}
	}
}