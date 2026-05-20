package dream.flying.flower.autoconfigure.dict;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import dream.flying.flower.autoconfigure.dict.cache.DictCacheWarmupService;
import dream.flying.flower.autoconfigure.dict.properties.DictProperties;
import dream.flying.flower.autoconfigure.dict.service.DictItemService;
import dream.flying.flower.autoconfigure.dict.service.DictService;
import dream.flying.flower.autoconfigure.dict.service.impl.DictItemServiceImpl;
import dream.flying.flower.autoconfigure.dict.service.impl.DictServiceImpl;
import dream.flying.flower.framework.constant.ConstConfig;

/**
 * Dict auto configuration class
 *
 * @author 飞花梦影
 * @date 2026-05-18
 */
@EnableConfigurationProperties({ DictProperties.class })
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
	@ConditionalOnMissingBean(DictCacheWarmupService.class)
	@ConditionalOnProperty(prefix = ConstConfig.Auto.DICT, name = "warmup-enabled", havingValue = "true",
			matchIfMissing = true)
	DictCacheWarmupService dictCacheWarmupService() {
		return new DictCacheWarmupService();
	}
}