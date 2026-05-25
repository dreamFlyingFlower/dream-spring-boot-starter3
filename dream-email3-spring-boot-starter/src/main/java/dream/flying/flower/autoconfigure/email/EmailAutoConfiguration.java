package dream.flying.flower.autoconfigure.email;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import dream.flying.flower.autoconfigure.email.properties.EmailProperties;
import dream.flying.flower.autoconfigure.email.service.EmailSendRecipientService;
import dream.flying.flower.autoconfigure.email.service.EmailService;
import dream.flying.flower.autoconfigure.email.service.EmailTemplateService;
import dream.flying.flower.autoconfigure.email.service.impl.EmailSendRecipientServiceImpl;
import dream.flying.flower.autoconfigure.email.service.impl.EmailServiceImpl;
import dream.flying.flower.autoconfigure.email.service.impl.EmailTemplateServiceImpl;
import dream.flying.flower.framework.constant.ConstConfig;

/**
 * Email auto configuration class
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
@AutoConfiguration(after = { FlywayAutoConfiguration.class })
@MapperScan("dream.flying.flower.autoconfigure.email.mapper")
@EnableConfigurationProperties({ EmailProperties.class })
@ConditionalOnProperty(prefix = ConstConfig.PREFIX + ".email", name = ConstConfig.ENABLED, havingValue = "true",
		matchIfMissing = true)
public class EmailAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(EmailService.class)
	EmailService emailService() {
		return new EmailServiceImpl();
	}

	@Bean
	@ConditionalOnMissingBean(EmailSendRecipientService.class)
	EmailSendRecipientService emailSendRecipientService() {
		return new EmailSendRecipientServiceImpl();
	}

	@Bean
	@ConditionalOnMissingBean(EmailTemplateService.class)
	EmailTemplateService emailTemplateService() {
		return new EmailTemplateServiceImpl();
	}
}
