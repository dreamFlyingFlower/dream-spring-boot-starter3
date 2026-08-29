package dream.flying.flower.autoconfigure.localize;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.mybatis.spring.annotation.MapperScan;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import dream.flying.flower.autoconfigure.localize.cache.LocalizeCache;
import dream.flying.flower.autoconfigure.localize.cache.RedisLocalizeCache;
import dream.flying.flower.autoconfigure.localize.constant.ConstLocalize;
import dream.flying.flower.autoconfigure.localize.convert.LocalizeConvert;
import dream.flying.flower.autoconfigure.localize.endpoint.LanguageEndpoint;
import dream.flying.flower.autoconfigure.localize.endpoint.LocalizeCacheEndpoint;
import dream.flying.flower.autoconfigure.localize.endpoint.LocalizeEndpoint;
import dream.flying.flower.autoconfigure.localize.enums.LocaleResolverType;
import dream.flying.flower.autoconfigure.localize.handler.LocalizeMessageSource;
import dream.flying.flower.autoconfigure.localize.helpers.LocalizeHelpers;
import dream.flying.flower.autoconfigure.localize.mapper.LanguageMapper;
import dream.flying.flower.autoconfigure.localize.properties.DreamLocalizeProperties;
import dream.flying.flower.autoconfigure.localize.resolver.HeaderLocaleResolver;
import dream.flying.flower.autoconfigure.localize.service.LanguageService;
import dream.flying.flower.autoconfigure.localize.service.LocalizeService;
import dream.flying.flower.autoconfigure.localize.service.impl.LanguageServiceImpl;
import dream.flying.flower.autoconfigure.localize.service.impl.LocalizeServiceImpl;
import dream.flying.flower.framework.constant.ConstConfig;

/**
 * I18n auto configuration class
 * 
 * <pre>
 * Locale.getDefault().toString(): 历史本地化,返回的是zh_CN
 * Locale.getDefault().toLanguageTag(): 国际化标准,返回的是zh-CN
 * 在处理时,需要2种情况都考虑,进行兼容.
 * 
 * Locale resolver strategy can be switched via properties.
 * Default is still {@link SessionLocaleResolver} for backward compatibility.
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

	/**
	 * Resolved properties instance. Held as a field so addInterceptors can use the
	 * runtime values without declaring an extra method arg. This avoids a second
	 * local-change-interceptor instance.
	 */
	private final DreamLocalizeProperties properties;

	public LocalizeAutoConfiguration(DreamLocalizeProperties properties) {
		this.properties = properties;
	}

	@Bean
	@ConditionalOnMissingBean(LocalizeConvert.class)
	LocalizeConvert localizeConvert() {
		return LocalizeConvert.INSTANCE;
	}

	@Bean
	@ConditionalOnMissingBean(LocalizeEndpoint.class)
	LocalizeEndpoint localizeEndpoint() {
		return new LocalizeEndpoint();
	}

	@Bean
	@ConditionalOnMissingBean(LanguageService.class)
	LanguageService languageService(LanguageMapper languageMapper) {
		return new LanguageServiceImpl(languageMapper);
	}

	@Bean
	@ConditionalOnMissingBean(LanguageEndpoint.class)
	LanguageEndpoint languageEndpoint() {
		return new LanguageEndpoint();
	}

	@Bean
	@ConditionalOnMissingBean(LocalizeCacheEndpoint.class)
	@ConditionalOnProperty(prefix = ConstConfig.Auto.LOCALIZE, name = "enabled-cache-endpoint", havingValue = "true")
	LocalizeCacheEndpoint localizeCacheEndpoint(LocalizeService localizeService) {
		return new LocalizeCacheEndpoint(localizeService);
	}

	@Bean
	@ConditionalOnMissingBean(LocalizeService.class)
	LocalizeService localizeService(LocalizeCache localizeCache, LanguageMapper languageMapper,
			DreamLocalizeProperties dreamLocalizeProperties) {
		return new LocalizeServiceImpl(localizeCache, languageMapper, dreamLocalizeProperties);
	}

	@Bean
	MessageSource messageSource(LocalizeService localizeService) {
		return new LocalizeMessageSource(localizeService);
	}

	@Bean
	LocaleResolver localeResolver(DreamLocalizeProperties properties) {
		Locale defaultLocale = parseLocaleOrDefault(properties.getDefaultLocale());
		List<Locale> supportedLocales = buildSupportedLocales(properties.getSupportedLocales());
		LocaleResolverType strategy = properties.getLocaleResolver();
		strategy = strategy == null ? LocaleResolverType.HEADER : strategy;

		LocaleResolver resolver;
		switch (strategy) {
		case COOKIE: {
			CookieLocaleResolver cookie = new CookieLocaleResolver(properties.getCookieName());
			cookie.setDefaultLocale(defaultLocale);
			if (properties.getCookieMaxAge() != null) {
				int maxAgeSeconds = (int) properties.getCookieMaxAge().getSeconds();
				cookie.setCookieMaxAge(maxAgeSeconds);
			}
			if (StringUtils.hasText(properties.getCookiePath())) {
				cookie.setCookiePath(properties.getCookiePath());
			}
			cookie.setCookieHttpOnly(properties.isCookieHttpOnly());
			resolver = cookie;
			break;
		}
		case SESSION: {
			SessionLocaleResolver session = new SessionLocaleResolver();
			session.setDefaultLocale(defaultLocale);
			resolver = session;
			break;
		}
		case FIXED: {
			resolver = new FixedLocaleResolver(defaultLocale);
			break;
		}
		case HEADER: {
			String headerName = StringUtils.hasText(properties.getHeaderName())
					? properties.getHeaderName().trim()
					: ConstLocalize.DEFAULT_HEADER_NAME;
			resolver = new HeaderLocaleResolver(headerName, defaultLocale);
			break;
		}
		case ACCEPT_HEADER:
		default: {
			AcceptHeaderLocaleResolver acceptHeader = new AcceptHeaderLocaleResolver();
			acceptHeader.setDefaultLocale(defaultLocale);
			if (!CollectionUtils.isEmpty(supportedLocales)) {
				acceptHeader.setSupportedLocales(supportedLocales);
			}
			resolver = acceptHeader;
			break;
		}
		}
		return resolver;
	}

	@Bean
	LocaleChangeInterceptor localeChangeInterceptor(DreamLocalizeProperties properties) {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName(properties.getLocaleChangeParamName());
		interceptor.setIgnoreInvalidLocale(properties.isIgnoreInvalidLocale());
		return interceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(buildLocaleChangeInterceptor());
	}

	private LocaleChangeInterceptor buildLocaleChangeInterceptor() {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName(properties.getLocaleChangeParamName());
		interceptor.setIgnoreInvalidLocale(properties.isIgnoreInvalidLocale());
		return interceptor;
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

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(RedisTemplate.class)
	LocalizeCache redisLocalizeCache(RedisTemplate<String, String> redisTemplate) {
		return new RedisLocalizeCache(redisTemplate);
	}

	/**
	 * Parse default-locale property using BCP-47 first, then fall back to legacy
	 * underscore split. Returns Locale.SIMPLIFIED_CHINESE as the last fallback.
	 *
	 * @param defaultLocale raw property value (may be zh-CN or zh_CN)
	 * @return parsed Locale, never null
	 */
	private static Locale parseLocaleOrDefault(String defaultLocale) {
		if (!StringUtils.hasText(defaultLocale)) {
			return Locale.SIMPLIFIED_CHINESE;
		}
		try {
			Locale parsed = LocalizeHelpers.parse(defaultLocale);
			if (parsed != null && StringUtils.hasText(parsed.getLanguage())) {
				return parsed;
			}
		} catch (Exception ignored) {
			// fall through to legacy split
		}
		String normalized = defaultLocale.replace('_', '-');
		String[] parts = normalized.split("-");
		if (parts.length >= 2) {
			return new Locale(parts[0], parts[1]);
		}
		if (parts.length == 1 && StringUtils.hasText(parts[0])) {
			return new Locale(parts[0]);
		}
		return Locale.SIMPLIFIED_CHINESE;
	}

	/**
	 * Convert supported locale tag list to Locale instances. Empty/null list is
	 * accepted and produces empty list.
	 *
	 * @param supportedLocales raw BCP-47 tag list (can be null/empty)
	 * @return parsed Locale list, never null
	 */
	private static List<Locale> buildSupportedLocales(List<String> supportedLocales) {
		if (CollectionUtils.isEmpty(supportedLocales)) {
			return new ArrayList<>();
		}
		return supportedLocales.stream().map(LocalizeHelpers::parse).collect(Collectors.toList());
	}
}
