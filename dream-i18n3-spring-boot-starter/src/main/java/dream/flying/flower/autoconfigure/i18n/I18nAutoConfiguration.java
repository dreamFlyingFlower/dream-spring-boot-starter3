package dream.flying.flower.autoconfigure.i18n;

import java.util.Locale;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import dream.flying.flower.autoconfigure.i18n.properties.I18nProperties;
import dream.flying.flower.autoconfigure.i18n.service.I18nService;
import dream.flying.flower.framework.constant.ConstConfig;

/**
 * I18n auto configuration class
 *
 * @author 飞花梦影
 * @date 2026-05-18
 */
@AutoConfiguration(after = { FlywayAutoConfiguration.class })
@MapperScan("dream.flying.flower.autoconfigure.i18n.mapper")
@EnableConfigurationProperties({ I18nProperties.class })
@ConditionalOnProperty(prefix = ConstConfig.PREFIX + ".i18n", name = ConstConfig.ENABLED, havingValue = "true",
		matchIfMissing = true)
public class I18nAutoConfiguration implements WebMvcConfigurer {

	@Bean
	@ConditionalOnMissingBean(I18nService.class)
	I18nService i18nService(I18nProperties properties) {
		I18nService service = new I18nService();
		service.setCacheExpireHours(properties.getCacheExpireHours());
		return service;
	}

	@Bean
	MessageSource messageSource(I18nService i18nService) {
		return new CustomMessageSource(i18nService);
	}

	@Bean
	LocaleResolver localeResolver(I18nProperties properties) {
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

		private final I18nService i18nService;

		CustomMessageSource(I18nService i18nService) {
			this.i18nService = i18nService;
		}

		@Override
		public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
			String langCode = locale.getLanguage() + "_" + locale.getCountry();
			String message = i18nService.getMessage(langCode, code);
			return message != null ? message : (defaultMessage != null ? defaultMessage : code);
		}

		@Override
		public String getMessage(String code, Object[] args, Locale locale) {
			return getMessage(code, args, null, locale);
		}

		@Override
		public String getMessage(org.springframework.context.MessageSourceResolvable resolvable, Locale locale) throws org.springframework.context.NoSuchMessageException {
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
