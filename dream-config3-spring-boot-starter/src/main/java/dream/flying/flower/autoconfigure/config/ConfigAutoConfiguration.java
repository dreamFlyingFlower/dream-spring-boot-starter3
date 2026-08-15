package dream.flying.flower.autoconfigure.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import dream.flying.flower.autoconfigure.config.cache.ConfigCacheManager;
import dream.flying.flower.autoconfigure.config.cache.ConfigCacheWarmupRunner;
import dream.flying.flower.autoconfigure.config.convert.ConfigConvert;
import dream.flying.flower.autoconfigure.config.endpoint.ConfigCacheEndpoint;
import dream.flying.flower.autoconfigure.config.endpoint.ConfigEndpoint;
import dream.flying.flower.autoconfigure.config.mapper.ConfigMapper;
import dream.flying.flower.autoconfigure.config.properties.DreamConfigProperties;
import dream.flying.flower.autoconfigure.config.service.ConfigService;
import dream.flying.flower.autoconfigure.config.service.impl.ConfigServiceImpl;
import dream.flying.flower.framework.constant.ConstConfig;

/**
 * Config auto configuration class
 *
 * @author 飞花梦影
 * @date 2026-08-13
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@AutoConfiguration(after = { FlywayAutoConfiguration.class })
@EnableConfigurationProperties({ DreamConfigProperties.class })
@MapperScan("dream.flying.flower.autoconfigure.config.mapper")
@ConditionalOnProperty(prefix = ConstConfig.Auto.CONFIG, name = ConstConfig.ENABLED, havingValue = "true",
		matchIfMissing = true)
public class ConfigAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(ConfigEndpoint.class)
	ConfigEndpoint configEndpoint() {
		return new ConfigEndpoint();
	}

	@Bean
	@ConditionalOnMissingBean(ConfigEndpoint.class)
	ConfigCacheEndpoint configCacheEndpoint(ConfigCacheWarmupRunner configCacheWarmupRunner,
			ConfigCacheManager configCacheManager) {
		return new ConfigCacheEndpoint(configCacheWarmupRunner, configCacheManager);
	}

	@Bean
	@ConditionalOnMissingBean(ConfigConvert.class)
	ConfigConvert configConvert() {
		return ConfigConvert.INSTANCE;
	}

	@Bean
	@ConditionalOnMissingBean(ConfigService.class)
	ConfigService configService() {
		return new ConfigServiceImpl();
	}

	@Bean
	@ConditionalOnMissingBean(ConfigCacheManager.class)
	ConfigCacheManager configCacheManager(RedisTemplate<String, Object> redisTemplate, ConfigMapper configMapper,
			DreamConfigProperties dreamConfigProperties) {
		return new ConfigCacheManager(redisTemplate, configMapper, dreamConfigProperties);
	}

	@Bean
	@ConditionalOnMissingBean(ConfigCacheWarmupRunner.class)
	@ConditionalOnProperty(prefix = ConstConfig.Auto.CONFIG, name = ConstConfig.ENABLED_WARMUP, havingValue = "true",
			matchIfMissing = true)
	ConfigCacheWarmupRunner configCacheWarmupRunner(ConfigCacheManager configCacheManager, ConfigMapper configMapper,
			DreamConfigProperties dreamConfigProperties) {
		return new ConfigCacheWarmupRunner(configCacheManager, configMapper, dreamConfigProperties);
	}

	@Bean
	@ConditionalOnProperty(prefix = ConstConfig.Auto.CONFIG, name = ConstConfig.ENABLED_API, havingValue = "true",
			matchIfMissing = true)
	GroupedOpenApi configApi(DreamConfigProperties dreamConfigProperties) {
		return GroupedOpenApi.builder()
				// 分组标识,最好不要有中文,可能出错
				.group(dreamConfigProperties.getApiGroup())
				// 分组展示名称
				.displayName(dreamConfigProperties.getApiGroupName())
				// 扫描包路径
				.packagesToScan(dreamConfigProperties.getApiPackageScan())
				.build();
	}
}