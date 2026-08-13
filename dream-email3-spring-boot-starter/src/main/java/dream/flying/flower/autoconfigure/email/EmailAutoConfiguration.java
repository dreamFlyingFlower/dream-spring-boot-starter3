package dream.flying.flower.autoconfigure.email;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import dream.flying.flower.autoconfigure.email.manager.DefaultEmailMananger;
import dream.flying.flower.autoconfigure.email.mapper.EmailTemplateMapper;
import dream.flying.flower.autoconfigure.email.properties.DreamEmailProperties;
import dream.flying.flower.autoconfigure.email.service.EmailRecipientService;
import dream.flying.flower.autoconfigure.email.service.EmailSendLogService;
import dream.flying.flower.autoconfigure.email.service.EmailTemplateService;
import dream.flying.flower.autoconfigure.email.service.impl.EmailRecipientServiceImpl;
import dream.flying.flower.autoconfigure.email.service.impl.EmailSendLogServiceImpl;
import dream.flying.flower.autoconfigure.email.service.impl.EmailTemplateServiceImpl;
import dream.flying.flower.framework.constant.ConstConfig;

/**
 * 邮件自动配置类
 *
 * @author 飞花梦影
 * @date 2026-05-25 13:25:57
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@AutoConfiguration(after = { FlywayAutoConfiguration.class })
@MapperScan("dream.flying.flower.autoconfigure.email.mapper")
@EnableConfigurationProperties({ DreamEmailProperties.class })
@ConditionalOnProperty(prefix = ConstConfig.PREFIX + ".email", name = ConstConfig.ENABLED, havingValue = "true",
		matchIfMissing = true)
public class EmailAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(EmailManager.class)
	EmailManager emailManager(JavaMailSender mailSender, TemplateEngine templateEngine, DreamEmailProperties dreamEmailProperties,
			EmailTemplateService emailTemplateService, EmailSendLogService emailSendLogService,
			EmailRecipientService emailRecipientService) {
		return new DefaultEmailMananger(mailSender, templateEngine, dreamEmailProperties, emailTemplateService,
				emailSendLogService, emailRecipientService);
	}

	@Bean
	@ConditionalOnMissingBean(EmailSendLogService.class)
	EmailSendLogService emailSendLogService() {
		return new EmailSendLogServiceImpl();
	}

	@Bean
	@ConditionalOnMissingBean(EmailRecipientService.class)
	EmailRecipientService emailRecipientService() {
		return new EmailRecipientServiceImpl();
	}

	@Bean
	@ConditionalOnMissingBean(EmailTemplateService.class)
	EmailTemplateService emailTemplateService(EmailTemplateMapper emailTemplateMapper) {
		return new EmailTemplateServiceImpl();
	}
}