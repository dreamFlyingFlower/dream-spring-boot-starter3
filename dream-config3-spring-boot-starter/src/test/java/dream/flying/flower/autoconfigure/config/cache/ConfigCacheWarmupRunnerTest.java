package dream.flying.flower.autoconfigure.config.cache;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import dream.flying.flower.autoconfigure.config.entity.ConfigEntity;
import dream.flying.flower.autoconfigure.config.mapper.ConfigMapper;
import dream.flying.flower.autoconfigure.config.properties.DreamConfigProperties;

/**
 * Config cache warmup runner test
 *
 * <p>Tests only warmup logic. Cache read/write operations are delegated to
 * {@link ConfigCacheService} and tested separately.
 *
 * @author 飞花梦影
 * @date 2026-08-15
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Config cache warmup runner test")
class ConfigCacheWarmupRunnerTest {

	@Mock
	private ConfigCacheService configCacheService;

	@Mock
	private ConfigMapper configMapper;

	private DreamConfigProperties dreamConfigProperties;

	private ConfigCacheWarmupRunner configCacheWarmupRunner;

	@BeforeEach
	void setUp() {
		dreamConfigProperties = new DreamConfigProperties();
		dreamConfigProperties.setEnabled(true);
		dreamConfigProperties.setEnabledWarmup(true);
		dreamConfigProperties.setCacheExpireHours(24);

		configCacheWarmupRunner = new ConfigCacheWarmupRunner(configCacheService, configMapper, dreamConfigProperties);
	}

	private ConfigEntity buildConfig(String configKey, Integer status) {
		ConfigEntity config = new ConfigEntity();
		config.setId(1L);
		config.setConfigKey(configKey);
		config.setConfigValue("test-value");
		config.setDataType("string");
		config.setStatus(status);
		config.setDeleted(0);
		return config;
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("1. Startup warmup - all configs delegated to cache service")
	void testWarmup_Success() {
		ConfigEntity config1 = buildConfig("system.name", 1);
		ConfigEntity config2 = buildConfig("system.port", 1);
		List<ConfigEntity> configs = Arrays.asList(config1, config2);

		when(configMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(configs);

		configCacheWarmupRunner.run();

		verify(configCacheService, times(1)).cache(config1);
		verify(configCacheService, times(1)).cache(config2);
	}

	@Test
	@DisplayName("2. Warmup disabled - no database query, no cache operations")
	void testWarmup_Disabled() {
		dreamConfigProperties.setEnabledWarmup(false);

		configCacheWarmupRunner.run();

		verify(configMapper, never()).selectList(any());
		verify(configCacheService, never()).cache(any());
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("3. Empty config list - database queried, no cache operations")
	void testWarmup_EmptyList() {
		when(configMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

		configCacheWarmupRunner.run();

		verify(configMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
		verify(configCacheService, never()).cache(any());
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("4. Exception during warmup - caught, no crash")
	void testWarmup_Exception() {
		when(configMapper.selectList(any(LambdaQueryWrapper.class)))
				.thenThrow(new RuntimeException("Database connection failed"));

		configCacheWarmupRunner.run();

		verify(configCacheService, never()).cache(any());
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("5. Scheduled warmup - all configs delegated to cache service")
	void testScheduledWarmup_Success() {
		ConfigEntity config1 = buildConfig("app.title", 1);
		List<ConfigEntity> configs = Collections.singletonList(config1);

		when(configMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(configs);

		configCacheWarmupRunner.scheduledWarmup();

		verify(configCacheService, times(1)).cache(config1);
	}

	@Test
	@DisplayName("6. Scheduled warmup - disabled, no operations")
	void testScheduledWarmup_Disabled() {
		dreamConfigProperties.setEnabledWarmup(false);

		configCacheWarmupRunner.scheduledWarmup();

		verify(configMapper, never()).selectList(any());
		verify(configCacheService, never()).cache(any());
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("7. Manual warmup - all configs delegated to cache service")
	void testManualWarmup() {
		ConfigEntity config1 = buildConfig("a.key", 1);
		ConfigEntity config2 = buildConfig("b.key", 1);
		ConfigEntity config3 = buildConfig("c.key", 1);
		List<ConfigEntity> configs = Arrays.asList(config1, config2, config3);

		when(configMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(configs);

		configCacheWarmupRunner.warmup();

		verify(configCacheService, times(3)).cache(any(ConfigEntity.class));
	}
}