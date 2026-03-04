package dream.flying.flower.autoconfigure.sms;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import dream.flying.flower.framework.constant.ConstConfig;
import dream.flying.flower.framework.sms.manager.AliyunSmsProvider;
import dream.flying.flower.framework.sms.manager.HuaweiSmsProvider;
import dream.flying.flower.framework.sms.manager.TencentSmsProvider;
import dream.flying.flower.framework.sms.model.SmsSender;
import dream.flying.flower.framework.sms.model.SmsTemplate;
import dream.flying.flower.framework.sms.properties.SmsProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * 短信服务自动配置类
 * <p>
 * 参考 Spring Cache 的自动配置方式，根据条件自动注册相应的短信服务商。
 * </p>
 * <p>
 * 设计模式：参考
 * {@code org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration}
 * </p>
 *
 * @author Dream Flying Flower
 * @version 1.0
 * @since 2026-03-04
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(SmsSender.class)
@EnableConfigurationProperties(SmsProperties.class)
@ConditionalOnProperty(prefix = ConstConfig.Sms.SMS, name = ConstConfig.ENABLED, havingValue = "true",
		matchIfMissing = true)
public class SmsAutoConfiguration {

	private final SmsProperties properties;

	public SmsAutoConfiguration(SmsProperties properties) {
		this.properties = properties;
		log.info("短信服务自动配置初始化，默认服务商：{}", properties.getDefaultProvider());
	}

	/**
	 * 创建 SmsTemplate Bean
	 * <p>
	 * 类似 Spring Cache 的 {@code CacheManager} Bean 创建
	 *
	 * @param senders 所有已注册的短信发送器
	 * @return SmsTemplate 实例
	 */
	@Bean
	@ConditionalOnMissingBean
	SmsTemplate smsTemplate(ObjectProvider<List<SmsSender>> senders) {
		SmsTemplate template = new SmsTemplate(properties.getDefaultProvider(), properties);

		// 注册所有可用的发送器
		senders.stream().forEach(list -> list.forEach(template::registerSender));

		log.info("SmsTemplate Bean 创建成功，已注册的发送器数量：{}", template.getRegisteredProviders().size());
		return template;
	}

	/**
	 * 自动配置阿里云短信发送器
	 * <p>
	 * 当配置中存在阿里云相关配置时自动注册
	 */
	@Bean
	@ConditionalOnClass(name = "com.aliyun.dysmsapi20170525.Client")
	@ConditionalOnProperty(prefix = "spring.sms.providers.aliyun", name = "access-key-id")
	@ConditionalOnMissingBean(name = "aliyunSmsSender")
	SmsSender aliyunSmsSender() {
		SmsProperties.ProviderConfig config = properties.getProviderConfig("aliyun");
		if (config != null && config.getAccessKeyId() != null) {
			log.info("自动配置阿里云短信发送器");
			return new AliyunSmsProvider(config);
		}
		throw new IllegalStateException("阿里云短信配置不完整");
	}

	/**
	 * 自动配置腾讯云短信发送器
	 * <p>
	 * 当配置中存在腾讯云相关配置时自动注册
	 */
	@Bean
	@ConditionalOnClass(name = "com.tencentcloudapi.sms.v20210111.SmsClient")
	@ConditionalOnProperty(prefix = "spring.sms.providers.tencent", name = "access-key-id")
	@ConditionalOnMissingBean(name = "tencentSmsSender")
	SmsSender tencentSmsSender() {
		SmsProperties.ProviderConfig config = properties.getProviderConfig("tencent");
		if (config != null && config.getAccessKeyId() != null) {
			log.info("自动配置腾讯云短信发送器");
			return new TencentSmsProvider(config);
		}
		throw new IllegalStateException("腾讯云短信配置不完整");
	}

	/**
	 * 自动配置华为云短信发送器
	 * <p>
	 * 当配置中存在华为云相关配置时自动注册
	 */
	@Bean
	@ConditionalOnClass(name = "com.huaweicloud.sdk.sms.v3.SmsClient")
	@ConditionalOnProperty(prefix = "spring.sms.providers.huawei", name = "access-key-id")
	@ConditionalOnMissingBean(name = "huaweiSmsSender")
	SmsSender huaweiSmsSender() {
		SmsProperties.ProviderConfig config = properties.getProviderConfig("huawei");
		if (config != null && config.getAccessKeyId() != null) {
			log.info("自动配置华为云短信发送器");
			return new HuaweiSmsProvider(config);
		}
		throw new IllegalStateException("华为云短信配置不完整");
	}

	/**
	 * 获取所有已配置的短信发送器
	 * <p>
	 * 类似 Spring Cache 的配置收集方式
	 *
	 * @param aliyunSender 阿里云发送器（可选）
	 * @param tencentSender 腾讯云发送器（可选）
	 * @param huaweiSender 华为云发送器（可选）
	 * @return 发送器列表
	 */
	@Bean
	@ConditionalOnMissingBean
	List<SmsSender> smsSenders(ObjectProvider<AliyunSmsProvider> aliyunSender,
			ObjectProvider<TencentSmsProvider> tencentSender, ObjectProvider<HuaweiSmsProvider> huaweiSender) {

		List<SmsSender> senders = new ArrayList<>();

		aliyunSender.ifAvailable(senders::add);
		tencentSender.ifAvailable(senders::add);
		huaweiSender.ifAvailable(senders::add);

		log.debug("收集到 {} 个短信发送器", senders.size());
		return senders;
	}
}