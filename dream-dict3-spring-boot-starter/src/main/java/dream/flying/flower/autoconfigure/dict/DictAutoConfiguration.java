package dream.flying.flower.autoconfigure.dict;

import org.mybatis.spring.annotation.MapperScan;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import dream.flying.flower.autoconfigure.dict.cache.DictCacheManager;
import dream.flying.flower.autoconfigure.dict.cache.DictCacheWarmupRunner;
import dream.flying.flower.autoconfigure.dict.mapper.DictItemMapper;
import dream.flying.flower.autoconfigure.dict.mapper.DictMapper;
import dream.flying.flower.autoconfigure.dict.properties.DreamDictProperties;
import dream.flying.flower.autoconfigure.dict.service.DictItemService;
import dream.flying.flower.autoconfigure.dict.service.DictService;
import dream.flying.flower.autoconfigure.dict.service.impl.DictItemServiceImpl;
import dream.flying.flower.autoconfigure.dict.service.impl.DictServiceImpl;
import dream.flying.flower.framework.constant.ConstConfig;

/**
 * Dict auto configuration class
 *
 * @author 飞花梦影
 * @date 2026-08-15 09:35:23
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@EnableConfigurationProperties({ DreamDictProperties.class })
@AutoConfiguration(after = { FlywayAutoConfiguration.class })
@MapperScan("dream.flying.flower.autoconfigure.dict.mapper")
@ConditionalOnProperty(prefix = ConstConfig.Auto.DICT, name = ConstConfig.ENABLED, havingValue = "true",
		matchIfMissing = true)
public class DictAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(DictService.class)
	DictService dictService() {
		return new DictServiceImpl();
	}

	@Bean
	@ConditionalOnMissingBean(DictItemService.class)
	DictItemService dictItemService() {
		return new DictItemServiceImpl();
	}

	@Bean
	@ConditionalOnMissingBean(DictCacheManager.class)
	DictCacheManager dictCacheManager(RedisTemplate<String, Object> redisTemplate, DictMapper dictMapper,
			DictItemMapper dictItemMapper, DreamDictProperties dreamDictProperties) {
		return new DictCacheManager(redisTemplate, dictMapper, dictItemMapper, dreamDictProperties);
	}

	@Bean
	@ConditionalOnMissingBean(DictCacheWarmupRunner.class)
	@ConditionalOnProperty(prefix = ConstConfig.Auto.DICT, name = ConstConfig.ENABLED_WARMUP, havingValue = "true",
			matchIfMissing = true)
	DictCacheWarmupRunner dictCacheWarmupRunner(DictCacheManager dictCacheManager, DictMapper dictMapper,
			DreamDictProperties dreamDictProperties) {
		return new DictCacheWarmupRunner(dictCacheManager, dictMapper, dreamDictProperties);
	}

	@Bean
	@ConditionalOnProperty(prefix = ConstConfig.Auto.DICT, name = ConstConfig.ENABLED_API, havingValue = "true",
			matchIfMissing = true)
	GroupedOpenApi configApi(DreamDictProperties dreamDictProperties) {
		return GroupedOpenApi.builder()
				// 分组标识,最好不要有中文,可能出错
				.group(dreamDictProperties.getApiGroup())
				// 分组展示名称
				.displayName(dreamDictProperties.getApiGroupName())
				// 扫描包路径
				.packagesToScan(dreamDictProperties.getApiPackageScan())
				.build();
	}
}