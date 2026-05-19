package dream.flying.flower.web.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import dream.flying.flower.autoconfigure.redis.helper.RedisFactoryHelpers;

/**
 * Redis管理服务测试类 - 数据库切换优化验证
 *
 * @author 飞花梦影
 * @date 2026-05-09 16:00:00
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@ExtendWith(MockitoExtension.class)
class RedisManageServiceTest {

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private RedisConnectionFactory defaultConnectionFactory;

	@Mock
	private RedisConnection redisConnection;

	@Mock
	private RedisProperties redisProperties;

	@Mock
	private ValueOperations<String, Object> valueOperations;

	@InjectMocks
	private RedisManageService redisManageService;

	@InjectMocks
	private RedisFactoryHelpers redisFactoryHelpers;

	RedisStandaloneConfiguration getBaseConfig() {
		RedisStandaloneConfiguration config = null;

		if (defaultConnectionFactory instanceof LettuceConnectionFactory) {
			// 1. 处理 LettuceConnectionFactory
			LettuceConnectionFactory factory = (LettuceConnectionFactory) defaultConnectionFactory;
			config = factory.getStandaloneConfiguration();
		} else if (defaultConnectionFactory instanceof JedisConnectionFactory) {
			// 2. 处理 JedisConnectionFactory
			JedisConnectionFactory factory = (JedisConnectionFactory) defaultConnectionFactory;
			config = factory.getStandaloneConfiguration();
		}

		// 如果还是获取不到配置,创建默认配置
		if (config == null) {
			config = new RedisStandaloneConfiguration();
			config.setHostName(redisProperties.getHost());
			config.setPort(redisProperties.getPort());
		}

		return config;
	}

	/**
	 * 获取指定数据库的连接工厂（兼容 Jedis 和 Lettuce）
	 */
	public RedisConnectionFactory getConnectionFactoryForDatabase(Integer databaseIndex) {
		if (databaseIndex == null) {
			RedisStandaloneConfiguration baseConfig = getBaseConfig();
			databaseIndex = baseConfig.getDatabase();
		}

		// 复制基础配置
		RedisStandaloneConfiguration newConfig = new RedisStandaloneConfiguration();
		RedisStandaloneConfiguration baseConfig = getBaseConfig();

		newConfig.setHostName(baseConfig.getHostName());
		newConfig.setPort(baseConfig.getPort());
		newConfig.setPassword(baseConfig.getPassword());
		if (baseConfig.getUsername() != null) {
			newConfig.setUsername(baseConfig.getUsername());
		}
		newConfig.setDatabase(databaseIndex);

		// 根据原连接工厂的类型，创建相同类型的连接工厂
		if (defaultConnectionFactory instanceof LettuceConnectionFactory) {
			LettuceConnectionFactory newFactory = new LettuceConnectionFactory(newConfig);
			newFactory.afterPropertiesSet();
			return newFactory;
		} else if (defaultConnectionFactory instanceof JedisConnectionFactory) {
			// Jedis 连接工厂的创建方式
			JedisConnectionFactory newFactory = new JedisConnectionFactory(newConfig);
			newFactory.afterPropertiesSet();
			return newFactory;
		}

		throw new IllegalStateException(
				"Unsupported RedisConnectionFactory type: " + defaultConnectionFactory.getClass().getName());
	}

	@BeforeEach
	void setUp() {
		when(redisTemplate.getConnectionFactory()).thenReturn(defaultConnectionFactory);
		when(defaultConnectionFactory.getConnection()).thenReturn(redisConnection);
	}

	/**
	 * 测试场景1：dbIndex与当前数据库相同，不应执行切换
	 */
	@Test
	void testGetString_SameDatabase_NoSwitch() {
		// 模拟当前数据库为0
		when(redisFactoryHelpers.getDefaultConfiguration().getDatabase()).thenReturn(0);
		when(redisTemplate.opsForValue()).thenReturn(mock(valueOperations));
		when(redisTemplate.opsForValue().get("test")).thenReturn("value");

		// 调用dbIndex=0的操作
		Object result = redisManageService.getString("test", 0);

		// 验证结果
		assertNotNull(result);
		assertEquals("value", result);

		// 验证selectDatabase未被调用（因为dbIndex与当前数据库相同）
		verify(redisConnection, never()).select(anyInt());

		// 验证只执行了get操作
		verify(redisTemplate.opsForValue(), times(1)).get("test");
	}

	/**
	 * 测试场景2：dbIndex与当前数据库不同，应执行切换和恢复
	 */
	@Test
	void testGetString_DifferentDatabase_SwitchAndRestore() {
		// 模拟当前数据库为0
		when(redisFactoryHelpers.getDefaultConfiguration().getDatabase()).thenReturn(0);
		when(redisTemplate.opsForValue()).thenReturn(mock(valueOperations));
		when(redisTemplate.opsForValue().get("test")).thenReturn("value");

		// 调用dbIndex=2的操作
		Object result = redisManageService.getString("test", 2);

		// 验证结果
		assertNotNull(result);
		assertEquals("value", result);

		// 验证selectDatabase被调用2次（切换到2，恢复到0）
		verify(redisConnection, times(1)).select(2);
		verify(redisConnection, times(1)).select(0);

		// 验证执行了get操作
		verify(redisTemplate.opsForValue(), times(1)).get("test");
	}

	/**
	 * 测试场景3：dbIndex为null，不执行任何切换
	 */
	@Test
	void testGetString_NullDbIndex_NoSwitch() {
		// 模拟当前数据库为0
		when(redisFactoryHelpers.getDefaultConfiguration().getDatabase()).thenReturn(0);
		when(redisTemplate.opsForValue()).thenReturn(mock(valueOperations));
		when(redisTemplate.opsForValue().get("test")).thenReturn("value");

		// 调用dbIndex=null的操作
		Object result = redisManageService.getString("test", null);

		// 验证结果
		assertNotNull(result);
		assertEquals("value", result);

		// 验证selectDatabase未被调用
		verify(redisConnection, never()).select(anyInt());

		// 验证执行了get操作
		verify(redisTemplate.opsForValue(), times(1)).get("test");
	}

	/**
	 * 测试场景4：设置String值，相同数据库不切换
	 */
	@Test
	void testSetString_SameDatabase_NoSwitch() {
		// 模拟当前数据库为1
		when(redisFactoryHelpers.getDefaultConfiguration().getDatabase()).thenReturn(1);
		when(redisTemplate.opsForValue()).thenReturn(mock(valueOperations));

		// 调用dbIndex=1的操作
		Boolean result = redisManageService.setString("test", "value", 300L, 1);

		// 验证结果
		assertTrue(result);

		// 验证selectDatabase未被调用
		verify(redisConnection, never()).select(anyInt());

		// 验证执行了set操作
		verify(redisTemplate.opsForValue(), times(1)).set(eq("test"), eq("value"), any());
	}

	/**
	 * 测试场景5：删除Key，不同数据库需要切换
	 */
	@Test
	void testDelete_DifferentDatabase_SwitchAndRestore() {
		// 模拟当前数据库为0
		when(redisFactoryHelpers.getDefaultConfiguration().getDatabase()).thenReturn(0);
		when(redisTemplate.delete("test")).thenReturn(true);

		// 调用dbIndex=3的操作
		Boolean result = redisManageService.delete("test", 3);

		// 验证结果
		assertTrue(result);

		// 验证selectDatabase被调用2次
		verify(redisConnection, times(1)).select(3);
		verify(redisConnection, times(1)).select(0);

		// 验证执行了delete操作
		verify(redisTemplate, times(1)).delete("test");
	}

	/**
	 * 测试场景6：判断Key存在性，相同数据库不切换
	 */
	@Test
	void testExists_SameDatabase_NoSwitch() {
		// 模拟当前数据库为2
		when(redisFactoryHelpers.getDefaultConfiguration().getDatabase()).thenReturn(2);
		when(redisTemplate.hasKey("test")).thenReturn(true);

		// 调用dbIndex=2的操作
		Boolean result = redisManageService.exists("test", 2);

		// 验证结果
		assertTrue(result);

		// 验证selectDatabase未被调用
		verify(redisConnection, never()).select(anyInt());

		// 验证执行了hasKey操作
		verify(redisTemplate, times(1)).hasKey("test");
	}

	/**
	 * 测试场景7：批量删除，不同数据库需要切换
	 */
	@Test
	void testDeleteBatch_DifferentDatabase_SwitchAndRestore() {
		// 模拟当前数据库为0
		when(redisFactoryHelpers.getDefaultConfiguration().getDatabase()).thenReturn(0);
		List<String> keys = new ArrayList<>();
		keys.add("key1");
		keys.add("key2");
		when(redisTemplate.delete(keys)).thenReturn(2L);

		// 调用dbIndex=5的操作
		Long result = redisManageService.deleteBatch(keys, 5);

		// 验证结果
		assertEquals(2L, result.longValue());

		// 验证selectDatabase被调用2次
		verify(redisConnection, times(1)).select(5);
		verify(redisConnection, times(1)).select(0);

		// 验证执行了delete操作
		verify(redisTemplate, times(1)).delete(keys);
	}

	/**
	 * 测试场景8：异常情况下仍能正确恢复数据库
	 */
	@Test
	void testGetString_Exception_StillRestoreDatabase() {
		// 模拟当前数据库为0
		when(redisFactoryHelpers.getDefaultConfiguration().getDatabase()).thenReturn(0);
		when(redisTemplate.opsForValue()).thenReturn(mock(valueOperations));
		when(redisTemplate.opsForValue().get("test")).thenThrow(new RuntimeException("Redis error"));

		// 调用dbIndex=2的操作，会抛出异常
		Object result = redisManageService.getString("test", 2);

		// 验证结果为null（异常被捕获）
		assertNull(result);

		// 验证即使发生异常，仍然恢复了数据库
		verify(redisConnection, times(1)).select(2);
		verify(redisConnection, times(1)).select(0);
	}

	/**
	 * 测试场景9：连续多次相同数据库操作，每次都正确判断
	 */
	@Test
	void testMultipleCalls_SameDatabase_EachCallOptimized() {
		// 模拟当前数据库为0
		when(redisFactoryHelpers.getDefaultConfiguration().getDatabase()).thenReturn(0);
		when(redisTemplate.opsForValue()).thenReturn(mock(valueOperations));
		when(redisTemplate.opsForValue().get(any())).thenReturn("value");

		// 连续3次调用dbIndex=0的操作
		redisManageService.getString("key1", 0);
		redisManageService.getString("key2", 0);
		redisManageService.getString("key3", 0);

		// 验证selectDatabase从未被调用
		verify(redisConnection, never()).select(anyInt());

		// 验证执行了3次get操作
		verify(redisTemplate.opsForValue(), times(3)).get(any());
	}

	/**
	 * 测试场景10：交替访问不同数据库
	 */
	@Test
	void testAlternatingDatabases_CorrectSwitching() {
		// 模拟当前数据库为0
		when(redisFactoryHelpers.getDefaultConfiguration().getDatabase()).thenReturn(0);
		when(redisTemplate.opsForValue()).thenReturn(mock(valueOperations));
		when(redisTemplate.opsForValue().get(any())).thenReturn("value");

		// 访问数据库0（不切换）
		redisManageService.getString("key1", 0);
		verify(redisConnection, never()).select(anyInt());

		// 访问数据库1（切换1→0）
		redisManageService.getString("key2", 1);
		verify(redisConnection, times(1)).select(1);
		verify(redisConnection, times(1)).select(0);

		// 访问数据库2（切换2→0）
		redisManageService.getString("key3", 2);
		verify(redisConnection, times(1)).select(2);
		verify(redisConnection, times(2)).select(0);

		// 再次访问数据库0（不切换）
		redisManageService.getString("key4", 0);
		// selectDatabase调用次数不变
		verify(redisConnection, times(2)).select(0);
	}
}