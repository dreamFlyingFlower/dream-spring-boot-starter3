package dream.flying.flower.autoconfigure.oss;

import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import dream.flying.flower.autoconfigure.oss.DreamOssAutoConfiguration.OssConfigurationImportSelector;
import dream.flying.flower.framework.oss.OssManager;
import dream.flying.flower.framework.oss.OssManagerCustomizer;
import dream.flying.flower.framework.oss.OssManagerCustomizers;
import dream.flying.flower.framework.oss.config.OssConfigurations;
import dream.flying.flower.framework.oss.enums.OssType;
import dream.flying.flower.framework.oss.properties.OssProperties;

/**
 * 存储自动配置,参照{@link CacheAutoConfiguration},默认使用本地存储.注意,自动配置不能被直接扫描,否则报错
 * 
 * @author 飞花梦影
 * @date 2023-08-11 14:41:38
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@AutoConfiguration
@Import({ OssConfigurationImportSelector.class })
public class DreamOssAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	OssManagerCustomizers ossManagerCustomizers(ObjectProvider<OssManagerCustomizer<?>> customizers) {
		return new OssManagerCustomizers(customizers.orderedStream().collect(Collectors.toList()));
	}

	@Bean
	OssManagerValidator ossAutoConfigurationValidator(OssProperties ossProperties,
			ObjectProvider<OssManager> ossManager) {
		return new OssManagerValidator(ossProperties, ossManager);
	}

	/**
	 * Bean used to validate that a OssManager exists and provide a more meaningful
	 * exception.
	 */
	static class OssManagerValidator implements InitializingBean {

		private final OssProperties ossProperties;

		private final ObjectProvider<OssManager> ossManager;

		OssManagerValidator(OssProperties ossProperties, ObjectProvider<OssManager> ossManager) {
			this.ossProperties = ossProperties;
			this.ossManager = ossManager;
		}

		@Override
		public void afterPropertiesSet() {
			Assert.notNull(this.ossManager.getIfAvailable(),
					() -> "No oss manager could be auto-configured, check your configuration (oss type is '"
							+ this.ossProperties.getType() + "')");
		}
	}

	/**
	 * {@link ImportSelector} to add {@link OssType} configuration classes.
	 */
	static class OssConfigurationImportSelector implements ImportSelector {

		@Override
		public String[] selectImports(AnnotationMetadata importingClassMetadata) {
			OssType[] types = OssType.values();
			String[] imports = new String[types.length];
			for (int i = 0; i < types.length; i++) {
				imports[i] = OssConfigurations.getConfigurationClass(types[i]);
			}
			return imports;
		}
	}
}