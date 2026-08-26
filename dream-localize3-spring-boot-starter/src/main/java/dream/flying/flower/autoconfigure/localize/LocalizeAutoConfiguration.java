package dream.flying.flower.autoconfigure.localize;

import java.util.Locale;

import org.mybatis.spring.annotation.MapperScan;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import dream.flying.flower.autoconfigure.localize.cache.CaffeineLocalizeCache;
import dream.flying.flower.autoconfigure.localize.cache.LocalizeCache;
import dream.flying.flower.autoconfigure.localize.cache.RedisLocalizeCache;
import dream.flying.flower.autoconfigure.localize.convert.LocalizeConvert;
import dream.flying.flower.autoconfigure.localize.endpoint.LocalizeEndpoint;
import dream.flying.flower.autoconfigure.localize.properties.DreamLocalizeProperties;
import dream.flying.flower.autoconfigure.localize.service.LocalizeService;
import dream.flying.flower.autoconfigure.localize.service.impl.LocalizeServiceImpl;
import dream.flying.flower.framework.constant.ConstConfig;

/**
 * I18n auto configuration class
 * 
 * <pre>
 * Locale.getDefault().toString(): 历史本地化,返回的是zh_CN
 * Locale.getDefault().toLanguageTag(): 国际化标准,返回的是zh-CN
 * 在处理时,需要2种情况都考虑,进行兼容
 * </pre>
 *
 * @author 飞花梦影
 * @date 2026-05-20 10:43:03
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@AutoConfiguration(after = { FlywayAutoConfiguration.class })
@EnableConfigurationProperties({ DreamLocalizeProperties.class })
@MapperScan("dream.flying.flower.autoconfigure.localize.mapper")
@ConditionalOnProperty(prefix = ConstConfig.Auto.LOCALIZE, name = ConstConfig.ENABLED, havingValue = "true",
		matchIfMissing = true)
public class LocalizeAutoConfiguration implements WebMvcConfigurer {

	@Bean
	@ConditionalOnMissingBean(LocalizeConvert.class)
	LocalizeConvert localizeConvert() {
		return LocalizeConvert.INSTANCE;
	}

	@Bean
	@ConditionalOnMissingBean(LocalizeEndpoint.class)
	LocalizeEndpoint LocalizeEndpoint() {
		return new LocalizeEndpoint();
	}

	@Bean
	@ConditionalOnMissingBean(LocalizeService.class)
	LocalizeService localizeService(RedisTemplate<String, String> redisTemplate,
			DreamLocalizeProperties dreamLocalizeProperties) {
		return new LocalizeServiceImpl(redisTemplate, dreamLocalizeProperties);
	}

	@Bean
	MessageSource messageSource(LocalizeService localizeService) {
		return new CustomMessageSource(localizeService);
	}

	@Bean
	LocaleResolver localeResolver(DreamLocalizeProperties properties) {
		SessionLocaleResolver resolver = new SessionLocaleResolver();
		String[] parts = properties.getDefaultLocale().split("_");
		if (parts.length == 2) {
			resolver.setDefaultLocale(new Locale(parts[0], parts[1]));
		} else {
			resolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
		}

		// AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
		// resolver.setDefaultLocale(Locale.CHINA);
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

		private final LocalizeService localizeService;

		CustomMessageSource(LocalizeService localizeService) {
			this.localizeService = localizeService;
		}

		@Override
		public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
			String lang = locale.getLanguage() + "_" + locale.getCountry();
			String message = localizeService.getMessage(code, lang);
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

	@Bean
	@ConditionalOnProperty(prefix = ConstConfig.Auto.CONFIG, name = ConstConfig.ENABLED_API, havingValue = "true",
			matchIfMissing = true)
	GroupedOpenApi configApi(DreamLocalizeProperties properties) {
		return GroupedOpenApi.builder()
				// 分组标识,最好不要有中文,可能出错
				.group(properties.getApiGroup())
				// 分组展示名称
				.displayName(properties.getApiGroupName())
				// 扫描包路径
				.packagesToScan(properties.getApiPackageScan())
				.build();
	}

	////////////////////////////////////////////////////////////////

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(Caffeine.class)
	Cache<String, String> caffeineCache(DreamLocalizeProperties properties) {
		return Caffeine.newBuilder()
				.maximumSize(properties.getCaffeine().getMaxSize())
				.expireAfterWrite(properties.getCaffeine().getExpireTime())
				.recordStats()
				.build();
	}

	@Bean
	@ConditionalOnMissingBean
	LocalizeService i18nService(LocalizeCache localizeCache) {
		return new LocalizeServiceImpl(localizeCache);
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(name = "dream.auto.localize.cache-type", havingValue = "caffeine", matchIfMissing = true)
	LocalizeCache caffeineLocalizeCache(Cache<String, String> caffeineCache) {
		return new CaffeineLocalizeCache(caffeineCache);
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(name = "dream.auto.localize.cache-type", havingValue = "redis")
	@ConditionalOnClass(RedisTemplate.class)
	LocalizeCache redisCache(RedisTemplate<String, String> redisTemplate, DreamLocalizeProperties properties) {
		return new RedisLocalizeCache(redisTemplate, properties);
	}
}