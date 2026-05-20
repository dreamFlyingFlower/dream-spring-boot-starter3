package dream.flying.flower.autoconfigure.i18n;

import java.util.Locale;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import dream.flying.flower.autoconfigure.i18n.properties.LocalizationProperties;
import dream.flying.flower.autoconfigure.i18n.service.LocalizationService;
import dream.flying.flower.autoconfigure.i18n.service.impl.LocalizationServiceImpl;
import dream.flying.flower.framework.constant.ConstConfig;

/**
 * I18n auto configuration class
 *
 * @author 飞花梦影
 * @date 2026-05-20 10:43:03
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@AutoConfiguration(after = { FlywayAutoConfiguration.class })
@EnableConfigurationProperties({ LocalizationProperties.class })
@MapperScan("dream.flying.flower.autoconfigure.i18n.mapper")
@ConditionalOnProperty(prefix = ConstConfig.Auto.LOCALIZATION, name = ConstConfig.ENABLED, havingValue = "true",
		matchIfMissing = true)
public class LocalizationAutoConfiguration implements WebMvcConfigurer {

	@Bean
	@ConditionalOnMissingBean(LocalizationService.class)
	LocalizationService localizationService(RedisTemplate<String, String> redisTemplate,
			LocalizationProperties localizationProperties) {
		return new LocalizationServiceImpl(redisTemplate, localizationProperties);
	}

	@Bean
	MessageSource messageSource(LocalizationService localizationService) {
		return new CustomMessageSource(localizationService);
	}

	@Bean
	LocaleResolver localeResolver(LocalizationProperties properties) {
		SessionLocaleResolver resolver = new SessionLocaleResolver();
		String[] parts = properties.getDefaultLocale().split("_");
		if (parts.length == 2) {
			resolver.setDefaultLocale(new Locale(parts[0], parts[1]));
		} else {
			resolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
		}
		return resolver;
	}

	@Bean
	LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName("lang");
		return interceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

	static class CustomMessageSource implements MessageSource {

		private final LocalizationService localizationService;

		CustomMessageSource(LocalizationService localizationService) {
			this.localizationService = localizationService;
		}

		@Override
		public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
			String langCode = locale.getLanguage() + "_" + locale.getCountry();
			String message = localizationService.getMessage(langCode, code);
			return message != null ? message : (defaultMessage != null ? defaultMessage : code);
		}

		@Override
		public String getMessage(String code, Object[] args, Locale locale) {
			return getMessage(code, args, null, locale);
		}

		@Override
		public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
			String[] codes = resolvable.getCodes();
			if (codes != null) {
				for (String code : codes) {
					String message = getMessage(code, null, locale);
					if (message != null && !code.equals(message)) {
						return message;
					}
				}
			}
			return resolvable.getDefaultMessage();
		}
	}
}