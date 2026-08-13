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

import dream.flying.flower.autoconfigure.config.cache.ConfigCacheWarmupRunner;
import dream.flying.flower.autoconfigure.config.convert.ConfigConvert;
import dream.flying.flower.autoconfigure.config.convert.ConfigConvertImpl;
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
	ConfigEndpoint configEndpoint(ConfigCacheWarmupRunner configCacheWarmupRunner) {
		return new ConfigEndpoint(configCacheWarmupRunner);
	}

	@Bean
	@ConditionalOnMissingBean(ConfigConvert.class)
	ConfigConvert configConvert() {
		return new ConfigConvertImpl();
	}

	@Bean
	@ConditionalOnMissingBean(ConfigService.class)
	ConfigService configService() {
		return new ConfigServiceImpl();
	}

	@Bean
	@ConditionalOnMissingBean(ConfigCacheWarmupRunner.class)
	@ConditionalOnProperty(prefix = ConstConfig.Auto.CONFIG, name = "warmup-enabled", havingValue = "true",
			matchIfMissing = true)
	ConfigCacheWarmupRunner configCacheWarmupRunner(RedisTemplate<String, Object> redisTemplate,
			ConfigMapper configMapper, DreamConfigProperties dreamConfigProperties) {
		return new ConfigCacheWarmupRunner(redisTemplate, configMapper, dreamConfigProperties);
	}

	@Bean
	GroupedOpenApi configApi(DreamConfigProperties dreamConfigProperties) {
		return GroupedOpenApi.builder()
				.group(dreamConfigProperties.getApiGroup())
				.packagesToScan(dreamConfigProperties.getApiPackageScan())
				.build();
	}
}