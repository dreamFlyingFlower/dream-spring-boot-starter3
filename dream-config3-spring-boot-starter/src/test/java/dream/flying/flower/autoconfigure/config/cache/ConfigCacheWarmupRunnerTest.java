package dream.flying.flower.autoconfigure.config.cache;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
 * Config cache warmup service test
 *
 * <p>
 * Test scenarios:
 * <ol>
 * <li>Startup warmup with multiple configs</li>
 * <li>Warmup disabled</li>
 * <li>Empty config list</li>
 * <li>Exception during warmup</li>
 * <li>Refresh single config cache</li>
 * <li>Evict config cache</li>
 * <li>Scheduled warmup</li>
 * </ol>
 *
 * @author 飞花梦影
 * @date 2026-08-13
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Config cache warmup service test")
class ConfigCacheWarmupRunnerTest {

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ConfigMapper configMapper;

	@Mock
	private ValueOperations<String, Object> valueOperations;

	private DreamConfigProperties dreamConfigProperties;

	@InjectMocks
	private ConfigCacheWarmupRunner configCacheWarmupRunner;

	@BeforeEach
	void setUp() {
		dreamConfigProperties = new DreamConfigProperties();
		dreamConfigProperties.setEnabled(true);
		dreamConfigProperties.setWarmupEnabled(true);
		dreamConfigProperties.setCacheExpireHours(24);

		// Rebuild service with real properties object
		configCacheWarmupRunner = new ConfigCacheWarmupRunner(redisTemplate, configMapper, dreamConfigProperties);

		lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
	}

	/**
	 * Build mock config entity
	 */
	private ConfigEntity buildConfig(String configKey, String configValue, String dataType, String category,
			Integer status) {
		ConfigEntity config = new ConfigEntity();
		config.setId(1L);
		config.setConfigKey(configKey);
		config.setConfigValue(configValue);
		config.setDataType(dataType);
		config.setCategory(category);
		config.setStatus(status);
		config.setDeleted(0);
		config.setSortIndex(0);
		return config;
	}

	/**
	 * Build expected cache key
	 */
	private String buildExpectedCacheKey(String configKey) {
		return "dream-config" + ":" + ConstConfig.MODULE_NAME + ":" + ConstConfig.CONFIG_CACHE_PREFIX + ":" + configKey;
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("1. Startup warmup - multiple configs cached successfully")
	void testWarmup_Success() {
		// Given: mock 3 enabled configs
		ConfigEntity config1 = buildConfig("system.name", "My System", "string", "system", 1);
		ConfigEntity config2 = buildConfig("system.port", "8080", "number", "system", 1);
		ConfigEntity config3 = buildConfig("system.debug", "true", "boolean", "system", 1);
		List<ConfigEntity> configs = Arrays.asList(config1, config2, config3);

		when(configMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(configs);

		// When: trigger startup warmup
		configCacheWarmupRunner.run();

		// Then: verify all configs cached to redis
		verify(valueOperations, times(3)).set(anyString(), any(ConfigEntity.class), anyLong(), eq(TimeUnit.HOURS));

		// Verify cache key and value for config1
		String expectedKey1 = buildExpectedCacheKey("system.name");
		verify(valueOperations, times(1)).set(eq(expectedKey1), eq(config1), eq(24L), eq(TimeUnit.HOURS));

		// Verify cache key and value for config2
		String expectedKey2 = buildExpectedCacheKey("system.port");
		verify(valueOperations, times(1)).set(eq(expectedKey2), eq(config2), eq(24L), eq(TimeUnit.HOURS));

		// Verify cache key and value for config3
		String expectedKey3 = buildExpectedCacheKey("system.debug");
		verify(valueOperations, times(1)).set(eq(expectedKey3), eq(config3), eq(24L), eq(TimeUnit.HOURS));
	}

	@Test
	@DisplayName("2. Warmup disabled - no cache operations")
	void testWarmup_Disabled() {
		// Given: warmup disabled
		dreamConfigProperties.setWarmupEnabled(false);

		// When: trigger startup warmup
		configCacheWarmupRunner.run();

		// Then: no database query and no redis operations
		verify(configMapper, never()).selectList(any());
		verify(valueOperations, never()).set(anyString(), any(), anyLong(), any(TimeUnit.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("3. Empty config list - no cache operations")
	void testWarmup_EmptyList() {
		// Given: empty config list
		when(configMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

		// When: trigger startup warmup
		configCacheWarmupRunner.run();

		// Then: database queried but no redis cache operations
		verify(configMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
		verify(valueOperations, never()).set(anyString(), any(), anyLong(), any(TimeUnit.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("4. Exception during warmup - exception caught, no crash")
	void testWarmup_Exception() {
		// Given: database query throws exception
		when(configMapper.selectList(any(LambdaQueryWrapper.class)))
				.thenThrow(new RuntimeException("Database connection failed"));

		// When: trigger startup warmup, should not throw exception
		configCacheWarmupRunner.run();

		// Then: exception caught, no redis operations
		verify(valueOperations, never()).set(anyString(), any(), anyLong(), any(TimeUnit.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("5. Refresh config cache - config exists, cache updated")
	void testRefreshConfigCache_Exists() {
		// Given: config exists in database
		ConfigEntity config = buildConfig("system.name", "Updated Name", "string", "system", 1);
		when(configMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(config);

		// When: refresh config cache
		configCacheWarmupRunner.refresh("system.name");

		// Then: cache updated with new value
		String expectedKey = buildExpectedCacheKey("system.name");
		verify(valueOperations, times(1)).set(eq(expectedKey), eq(config), eq(24L), eq(TimeUnit.HOURS));
		// Should not delete cache
		verify(redisTemplate, never()).delete(anyString());
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("6. Refresh config cache - config not exists, cache evicted")
	void testRefreshConfigCache_NotExists() {
		// Given: config does not exist in database
		when(configMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

		// When: refresh config cache
		configCacheWarmupRunner.refresh("system.deleted");

		// Then: cache evicted
		String expectedKey = buildExpectedCacheKey("system.deleted");
		verify(redisTemplate, times(1)).delete(eq(expectedKey));
		// Should not set cache
		verify(valueOperations, never()).set(anyString(), any(), anyLong(), any(TimeUnit.class));
	}

	@Test
	@DisplayName("7. Evict config cache - cache deleted")
	void testEvictConfigCache() {
		// When: evict config cache
		configCacheWarmupRunner.evict("system.name");

		// Then: cache deleted by key
		String expectedKey = buildExpectedCacheKey("system.name");
		verify(redisTemplate, times(1)).delete(eq(expectedKey));
	}

	@Test
	@DisplayName("8. Evict config cache - exception caught, no crash")
	void testEvictConfigCache_Exception() {
		// Given: redis delete throws exception
		when(redisTemplate.delete(anyString())).thenThrow(new RuntimeException("Redis connection failed"));

		// When: evict config cache, should not throw exception
		configCacheWarmupRunner.evict("system.name");

		// Then: delete called, exception caught
		verify(redisTemplate, times(1)).delete(anyString());
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("9. Scheduled warmup - success")
	void testScheduledWarmup_Success() {
		// Given: mock 2 enabled configs
		ConfigEntity config1 = buildConfig("app.title", "Test App", "string", "app", 1);
		ConfigEntity config2 = buildConfig("app.version", "1.0.0", "string", "app", 1);
		List<ConfigEntity> configs = Arrays.asList(config1, config2);

		when(configMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(configs);

		// When: trigger scheduled warmup
		configCacheWarmupRunner.scheduledWarmup();

		// Then: all configs cached
		verify(valueOperations, times(2)).set(anyString(), any(ConfigEntity.class), anyLong(), eq(TimeUnit.HOURS));
	}

	@Test
	@DisplayName("10. Scheduled warmup - disabled, no operations")
	void testScheduledWarmup_Disabled() {
		// Given: warmup disabled
		dreamConfigProperties.setWarmupEnabled(false);

		// When: trigger scheduled warmup
		configCacheWarmupRunner.scheduledWarmup();

		// Then: no operations
		verify(configMapper, never()).selectList(any());
		verify(valueOperations, never()).set(anyString(), any(), anyLong(), any(TimeUnit.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("11. Cache config - redis exception caught, continues with next")
	void testWarmup_RedisException_ContinuesWithNext() {
		// Given: first config cache throws exception, second succeeds
		ConfigEntity config1 = buildConfig("system.name", "My System", "string", "system", 1);
		ConfigEntity config2 = buildConfig("system.port", "8080", "number", "system", 1);
		List<ConfigEntity> configs = new ArrayList<>(Arrays.asList(config1, config2));

		when(configMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(configs);
		// First set throws exception, second succeeds (lenient because order may vary)
		// set() returns void, must use doThrow().when() syntax
		lenient().doThrow(new RuntimeException("Redis timeout"))
				.when(valueOperations)
				.set(anyString(), eq(config1), anyLong(), eq(TimeUnit.HOURS));

		// When: trigger startup warmup, should not throw exception
		configCacheWarmupRunner.run();

		// Then: both configs attempted (first failed, second succeeded)
		verify(valueOperations, times(2)).set(anyString(), any(ConfigEntity.class), anyLong(), eq(TimeUnit.HOURS));
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("12. Refresh config cache - exception caught, no crash")
	void testRefreshConfigCache_Exception() {
		// Given: database query throws exception
		when(configMapper.selectOne(any(LambdaQueryWrapper.class))).thenThrow(new RuntimeException("Database error"));

		// When: refresh config cache, should not throw exception
		configCacheWarmupRunner.refresh("system.name");

		// Then: no cache operations
		verify(valueOperations, never()).set(anyString(), any(), anyLong(), any(TimeUnit.class));
		verify(redisTemplate, never()).delete(anyString());
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("13. Verify cache key format")
	void testCacheKeyFormat() {
		// Given: single config
		ConfigEntity config = buildConfig("test.key", "test-value", "string", "test", 1);
		when(configMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(config));

		// When: trigger startup warmup
		configCacheWarmupRunner.run();

		// Then: verify cache key format is "dream-config:config:config:{configKey}"
		String expectedKey = "dream-config:config:config:test.key";
		verify(valueOperations, times(1)).set(eq(expectedKey), eq(config), eq(24L), eq(TimeUnit.HOURS));
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("14. Verify cache expire hours from properties")
	void testCacheExpireHours() {
		// Given: custom cache expire hours
		dreamConfigProperties.setCacheExpireHours(48);
		ConfigEntity config = buildConfig("test.key", "test-value", "string", "test", 1);
		when(configMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(config));

		// When: trigger startup warmup
		configCacheWarmupRunner.run();

		// Then: verify 48 hours used
		verify(valueOperations, times(1)).set(anyString(), any(ConfigEntity.class), eq(48L), eq(TimeUnit.HOURS));
	}
}