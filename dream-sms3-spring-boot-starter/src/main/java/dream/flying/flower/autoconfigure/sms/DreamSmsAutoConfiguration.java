package dream.flying.flower.autoconfigure.sms;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import dream.flying.flower.autoconfigure.sms.DreamSmsAutoConfiguration.SmsConfigurationImportSelector;
import dream.flying.flower.framework.sms.SmsManager;
import dream.flying.flower.framework.sms.SmsManagerCustomizer;
import dream.flying.flower.framework.sms.SmsManagerCustomizers;
import dream.flying.flower.framework.sms.SmsTemplate;
import dream.flying.flower.framework.sms.config.SmsConfigurations;
import dream.flying.flower.framework.sms.enums.SmsType;
import dream.flying.flower.framework.sms.properties.SmsProperties;

/**
 * 存储自动配置,参照{@link CacheAutoConfiguration},默认使用本地存储.注意,自动配置不能被直接扫描,否则报错
 * 
 * @author 飞花梦影
 * @date 2023-08-11 14:41:38
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@AutoConfiguration
@Import({ SmsConfigurationImportSelector.class })
@EnableConfigurationProperties(SmsProperties.class)
public class DreamSmsAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	SmsManagerCustomizers smsManagerCustomizers(ObjectProvider<SmsManagerCustomizer<?>> customizers) {
		return new SmsManagerCustomizers(customizers.orderedStream().collect(Collectors.toList()));
	}

	/**
	 * 注入所有SmsManager,该方法可能有错误,可能需要进行删除
	 * 
	 * @param smsProperties 短信配置
	 * @param smsManagers 短信管理者列表
	 * @return 短信管理模板
	 */
	@Bean
	@ConditionalOnMissingBean
	SmsTemplate smsTemplate(SmsProperties smsProperties, ObjectProvider<List<SmsManager>> smsManagers) {
		SmsTemplate smsTemplate = new SmsTemplate(smsProperties.getDefaultSmsType(), smsProperties);

		// 延迟获取所有 SmsManager
		smsManagers.stream().forEach(list -> list.forEach(smsTemplate::register));

		return smsTemplate;
	}

	@Bean
	SmsManagerValidator smsManagerValidator(SmsProperties smsProperties, ObjectProvider<SmsManager> smsManager) {
		return new SmsManagerValidator(smsProperties, smsManager);
	}

	/**
	 * Bean used to validate that a SmsManager exists and provide a more meaningful
	 * exception.
	 */
	static class SmsManagerValidator implements InitializingBean {

		private final SmsProperties smsProperties;

		private final ObjectProvider<SmsManager> smsManager;

		SmsManagerValidator(SmsProperties smsProperties, ObjectProvider<SmsManager> smsManager) {
			this.smsProperties = smsProperties;
			this.smsManager = smsManager;
		}

		@Override
		public void afterPropertiesSet() {
			Assert.notNull(this.smsManager.getIfAvailable(),
					() -> "No sms manager could be auto-configured, check your configuration (sms type is '"
							+ this.smsProperties.getType() + "')");
		}
	}

	/**
	 * {@link ImportSelector} to add {@link SmsType} configuration classes.
	 */
	static class SmsConfigurationImportSelector implements ImportSelector {

		@Override
		public String[] selectImports(AnnotationMetadata importingClassMetadata) {
			SmsType[] types = SmsType.values();
			String[] imports = new String[types.length];
			for (int i = 0; i < types.length; i++) {
				imports[i] = SmsConfigurations.getConfigurationClass(types[i]);
			}
			return imports;
		}
	}
}