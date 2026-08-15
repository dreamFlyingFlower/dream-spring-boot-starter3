package dream.flying.flower.autoconfigure.config.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import dream.flying.flower.autoconfigure.config.constant.ConstConfig;
import dream.flying.flower.autoconfigure.config.entity.ConfigEntity;
import dream.flying.flower.autoconfigure.config.mapper.ConfigMapper;
import dream.flying.flower.autoconfigure.config.properties.DreamConfigProperties;

/**
 * Config cache service test
 *
 * <p>
 * Tests cache read/write/evict/refresh/get operations.
 *
 * @author 飞花梦影
 * @date 2026-08-15
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Config cache service test")
class ConfigCacheServiceTest {

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ConfigMapper configMapper;

	@Mock
	private ValueOperations<String, Object> valueOperations;

	private DreamConfigProperties dreamConfigProperties;

	private ConfigCacheService configCacheService;

	@BeforeEach
	void setUp() {
		dreamConfigProperties = new DreamConfigProperties();
		dreamConfigProperties.setEnabled(true);
		dreamConfigProperties.setEnabledWarmup(true);
		dreamConfigProperties.setCacheExpireHours(24);

		configCacheService = new ConfigCacheService(redisTemplate, configMapper, dreamConfigProperties);

		lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
	}

	private ConfigEntity buildConfig(String configKey, String configValue, Integer status) {
		ConfigEntity config = new ConfigEntity();
		config.setId(1L);
		config.setConfigKey(configKey);
		config.setConfigValue(configValue);
		config.setDataType("string");
		config.setStatus(status);
		config.setDeleted(0);
		return config;
	}

	private String buildExpectedCacheKey(String configKey) {
		return "dream-config" + ":" + ConstConfig.MODULE_NAME + ":" + ConstConfig.CONFIG_CACHE_PREFIX + ":" + configKey;
	}

	@Test
	@DisplayName("1. Cache config - write to redis with correct key and TTL")
	void testCache_Success() {
		ConfigEntity config = buildConfig("system.name", "My System", 1);

		configCacheService.cache(config);

		String expectedKey = buildExpectedCacheKey("system.name");
		verify(valueOperations, times(1)).set(eq(expectedKey), eq(config), eq(24L), eq(TimeUnit.HOURS));
	}

	@Test
	@DisplayName("2. Cache config - redis exception caught, no crash")
	void testCache_Exception() {
		ConfigEntity config = buildConfig("system.name", "My System", 1);
		lenient().doThrow(new RuntimeException("Redis timeout"))
				.when(valueOperations)
				.set(anyString(), eq(config), anyLong(), eq(TimeUnit.HOURS));

		configCacheService.cache(config);

		verify(valueOperations, times(1)).set(anyString(), eq(config), anyLong(), eq(TimeUnit.HOURS));
	}

	@Test
	@DisplayName("3. Evict config - delete from redis by key")
	void testEvict_Success() {
		configCacheService.evict("system.name");

		String expectedKey = buildExpectedCacheKey("system.name");
		verify(redisTemplate, times(1)).delete(eq(expectedKey));
	}

	@Test
	@DisplayName("4. Evict config - redis exception caught, no crash")
	void testEvict_Exception() {
		when(redisTemplate.delete(anyString())).thenThrow(new RuntimeException("Redis connection failed"));

		configCacheService.evict("system.name");

		verify(redisTemplate, times(1)).delete(anyString());
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("5. Refresh config - config exists, cache updated")
	void testRefresh_Exists() {
		ConfigEntity config = buildConfig("system.name", "Updated Name", 1);
		when(configMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(config);

		configCacheService.refresh("system.name");

		String expectedKey = buildExpectedCacheKey("system.name");
		verify(valueOperations, times(1)).set(eq(expectedKey), eq(config), eq(24L), eq(TimeUnit.HOURS));
		verify(redisTemplate, never()).delete(anyString());
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("6. Refresh config - config not exists, cache evicted")
	void testRefresh_NotExists() {
		when(configMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

		configCacheService.refresh("system.deleted");

		String expectedKey = buildExpectedCacheKey("system.deleted");
		verify(redisTemplate, times(1)).delete(eq(expectedKey));
		verify(valueOperations, never()).set(anyString(), any(), anyLong(), any(TimeUnit.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("7. Refresh config - DB exception caught, no crash")
	void testRefresh_Exception() {
		when(configMapper.selectOne(any(LambdaQueryWrapper.class))).thenThrow(new RuntimeException("Database error"));

		configCacheService.refresh("system.name");

		verify(valueOperations, never()).set(anyString(), any(), anyLong(), any(TimeUnit.class));
		verify(redisTemplate, never()).delete(anyString());
	}

	@Test
	@DisplayName("8. Get config - cache hit, return cached value without DB query")
	void testGet_CacheHit() {
		ConfigEntity cached = buildConfig("system.name", "Cached Value", 1);
		when(redisTemplate.opsForValue().get(anyString())).thenReturn(cached);

		ConfigEntity result = configCacheService.get("system.name");

		verify(configMapper, never()).selectOne(any());
		assertEquals(cached, result);
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("9. Get config - cache miss, load from DB and write-through cache")
	void testGet_CacheMiss_DBHit() {
		when(redisTemplate.opsForValue().get(anyString())).thenReturn(null);
		ConfigEntity config = buildConfig("system.name", "DB Value", 1);
		when(configMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(config);

		ConfigEntity result = configCacheService.get("system.name");

		String expectedKey = buildExpectedCacheKey("system.name");
		verify(valueOperations, times(1)).set(eq(expectedKey), eq(config), eq(24L), eq(TimeUnit.HOURS));
		assertEquals(config, result);
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("10. Get config - cache miss and DB miss, return null")
	void testGet_CacheMiss_DBMiss() {
		when(redisTemplate.opsForValue().get(anyString())).thenReturn(null);
		when(configMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

		ConfigEntity result = configCacheService.get("system.notfound");

		verify(valueOperations, never()).set(anyString(), any(), anyLong(), any(TimeUnit.class));
		assertNull(result);
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("11. Get config - cache exception, fallback to DB")
	void testGet_CacheException_FallbackDB() {
		when(redisTemplate.opsForValue().get(anyString())).thenThrow(new RuntimeException("Redis error"));
		ConfigEntity config = buildConfig("system.name", "DB Value", 1);
		when(configMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(config);

		ConfigEntity result = configCacheService.get("system.name");

		assertEquals(config, result);
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("12. Get config - DB exception, return null")
	void testGet_DBException() {
		when(redisTemplate.opsForValue().get(anyString())).thenReturn(null);
		when(configMapper.selectOne(any(LambdaQueryWrapper.class))).thenThrow(new RuntimeException("DB error"));

		ConfigEntity result = configCacheService.get("system.name");

		assertNull(result);
	}

	@Test
	@DisplayName("13. Verify cache key format")
	void testCacheKeyFormat() {
		ConfigEntity config = buildConfig("test.key", "value", 1);

		configCacheService.cache(config);

		String expectedKey = "dream-config:config:config:test.key";
		verify(valueOperations, times(1)).set(eq(expectedKey), eq(config), eq(24L), eq(TimeUnit.HOURS));
	}

	@Test
	@DisplayName("14. Verify cache expire hours from properties")
	void testCacheExpireHours() {
		dreamConfigProperties.setCacheExpireHours(48);
		ConfigEntity config = buildConfig("test.key", "value", 1);

		configCacheService.cache(config);

		verify(valueOperations, times(1)).set(anyString(), any(ConfigEntity.class), eq(48L), eq(TimeUnit.HOURS));
	}
}