package dream.flying.flower.autoconfigure.localize;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import dream.flying.flower.autoconfigure.localize.cache.LocalizeCache;
import dream.flying.flower.autoconfigure.localize.cache.RedisLocalizeCache;
import dream.flying.flower.autoconfigure.localize.constant.ConstLocalize;
import dream.flying.flower.autoconfigure.localize.convert.LanguageConvert;
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
import dream.flying.flower.collection.ListHelper;
import dream.flying.flower.framework.constant.ConstConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@RequiredArgsConstructor
@AutoConfiguration(after = { FlywayAutoConfiguration.class }, before = { MessageSourceAutoConfiguration.class,
		WebMvcAutoConfiguration.class })
@EnableConfigurationProperties({ DreamLocalizeProperties.class })
@MapperScan("dream.flying.flower.autoconfigure.localize.mapper")
@ConditionalOnProperty(prefix = ConstConfig.Auto.LOCALIZE, name = ConstConfig.ENABLED, havingValue = "true",
		matchIfMissing = true)
public class LocalizeAutoConfiguration implements WebMvcConfigurer {

	private final DreamLocalizeProperties dreamLocalizeProperties;

	@Bean
	@ConditionalOnMissingBean(LanguageEndpoint.class)
	@ConditionalOnProperty(prefix = ConstConfig.Auto.LOCALIZE, name = ConstConfig.ENABLED_ENDPOINT,
			havingValue = "true")
	LanguageEndpoint languageEndpoint() {
		return new LanguageEndpoint();
	}

	@Bean
	@ConditionalOnMissingBean(LocalizeEndpoint.class)
	@ConditionalOnProperty(prefix = ConstConfig.Auto.LOCALIZE, name = ConstConfig.ENABLED_ENDPOINT,
			havingValue = "true")
	LocalizeEndpoint localizeEndpoint() {
		return new LocalizeEndpoint();
	}

	@Bean
	@ConditionalOnMissingBean(LocalizeCacheEndpoint.class)
	@ConditionalOnProperty(prefix = ConstConfig.Auto.LOCALIZE, name = "enabled-cache-endpoint", havingValue = "true")
	LocalizeCacheEndpoint localizeCacheEndpoint(LocalizeService localizeService) {
		return new LocalizeCacheEndpoint(localizeService);
	}

	@Bean
	@ConditionalOnMissingBean(LanguageConvert.class)
	LanguageConvert languageConvert() {
		return LanguageConvert.INSTANCE;
	}

	@Bean
	@ConditionalOnMissingBean(LocalizeConvert.class)
	LocalizeConvert localizeConvert() {
		return LocalizeConvert.INSTANCE;
	}

	@Bean
	@ConditionalOnMissingBean(LanguageService.class)
	LanguageService languageService() {
		return new LanguageServiceImpl();
	}

	@Bean
	@ConditionalOnMissingBean(LocalizeService.class)
	LocalizeService localizeService(LocalizeCache localizeCache, LanguageMapper languageMapper) {
		return new LocalizeServiceImpl(localizeCache, languageMapper, dreamLocalizeProperties);
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(RedisTemplate.class)
	LocalizeCache redisLocalizeCache(RedisTemplate<String, String> redisTemplate) {
		return new RedisLocalizeCache(redisTemplate);
	}

	@Bean
	@Primary
	@ConditionalOnMissingBean(name = "messageSource")
	MessageSource messageSource(LocalizeService localizeService) {
		return new LocalizeMessageSource(localizeService);
	}

	@Bean
	@ConditionalOnMissingBean(name = "localeResolver")
	LocaleResolver localeResolver() {
		Locale defaultLocale = parseLocale(dreamLocalizeProperties.getDefaultLocale());
		List<Locale> supportedLocales = buildSupportedLocales(dreamLocalizeProperties.getSupportedLocales());
		LocaleResolverType strategy = dreamLocalizeProperties.getLocaleResolver();
		strategy = strategy == null ? LocaleResolverType.ACCEPT_HEADER : strategy;

		LocaleResolver resolver;
		switch (strategy) {
		case COOKIE: {
			CookieLocaleResolver cookie = new CookieLocaleResolver(dreamLocalizeProperties.getCookieName());
			cookie.setDefaultLocale(defaultLocale);
			if (dreamLocalizeProperties.getCookieMaxAge() != null) {
				int maxAgeSeconds = (int) dreamLocalizeProperties.getCookieMaxAge().getSeconds();
				cookie.setCookieMaxAge(Duration.ofSeconds(maxAgeSeconds));
			}
			if (StringUtils.isNotBlank(dreamLocalizeProperties.getCookiePath())) {
				cookie.setCookiePath(dreamLocalizeProperties.getCookiePath());
			}
			cookie.setCookieHttpOnly(dreamLocalizeProperties.isCookieHttpOnly());
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
			String headerName = StringUtils.isNotBlank(dreamLocalizeProperties.getHeaderName())
					? dreamLocalizeProperties.getHeaderName().trim() : ConstLocalize.DEFAULT_HEADER_NAME;
			resolver = new HeaderLocaleResolver(headerName, defaultLocale);
			break;
		}
		case ACCEPT_HEADER:
		default: {
			AcceptHeaderLocaleResolver acceptHeader = new AcceptHeaderLocaleResolver();
			acceptHeader.setDefaultLocale(defaultLocale);
			if (ListHelper.isNotEmpty(supportedLocales)) {
				acceptHeader.setSupportedLocales(supportedLocales);
			}
			resolver = acceptHeader;
			break;
		}
		}
		return resolver;
	}

	/**
	 * Parse default-locale property using BCP-47 first, then fall back to legacy
	 * underscore split. Returns Locale.SIMPLIFIED_CHINESE as the last fallback.
	 *
	 * @param defaultLocale raw property value (may be zh-CN or zh_CN)
	 * @return parsed Locale, never null
	 */
	private static Locale parseLocale(String defaultLocale) {
		if (StringUtils.isBlank(defaultLocale)) {
			return Locale.SIMPLIFIED_CHINESE;
		}
		try {
			Locale parsed = LocalizeHelpers.parse(defaultLocale);
			if (parsed != null && StringUtils.isNotBlank(parsed.getLanguage())) {
				return parsed;
			}
		} catch (Exception e) {
			// fall through to legacy split
			log.error("Parse default locale {} failed : {}", defaultLocale, e.getMessage());
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
		if (ListHelper.isEmpty(supportedLocales)) {
			return new ArrayList<>();
		}
		return supportedLocales.stream().map(LocalizeHelpers::parse).collect(Collectors.toList());
	}

	@Bean
	@ConditionalOnMissingBean(name = "localeChangeInterceptor")
	LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName(dreamLocalizeProperties.getLocaleChangeParamName());
		interceptor.setIgnoreInvalidLocale(dreamLocalizeProperties.isIgnoreInvalidLocale());
		return interceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

	@Bean
	@ConditionalOnProperty(prefix = ConstConfig.Auto.CONFIG, name = ConstConfig.ENABLED_API, havingValue = "true",
			matchIfMissing = true)
	GroupedOpenApi configApi() {
		return GroupedOpenApi.builder()
				// 分组标识,最好不要有中文,可能出错
				.group(dreamLocalizeProperties.getApiGroup())
				// 分组展示名称
				.displayName(dreamLocalizeProperties.getApiGroupName())
				// 扫描包路径
				.packagesToScan(dreamLocalizeProperties.getApiPackageScan())
				.build();
	}

	// =========================================================================
	// Isolated per-starter Flyway instance. This is the OFFICIAL ZERO-CONFLICT
	// pattern when packaging SQL migrations inside reusable Spring Boot
	// starters. Host project + any number of sibling starters can run with
	// THEIR OWN fully independent Flyway instances. Each instance:
	//   (1) Scans ONLY its own sub-directory under classpath:db/migration/<mod>
	//       (no overlap with host db/migration or other starter subdirectories)
	//   (2) Writes applied-migration records to ITS OWN history table named
	//       flyway_<module>_history. History tables are fully isolated, so two
	//       different starters can both ship a V1.0.0 migration and there is
	//       ZERO version-number collision. No one needs to coordinate versions.
	//   (3) Runs AFTER the host project's main flywayInitializer bean, so host
	//       baseline / shared schemas are already applied before starter tables.
	//   (4) Uses baselineOnMigrate=true so existing databases are not blocked.
	// =========================================================================

	/**
	 * Construct the isolated Flyway instance for the localize module. Only the
	 * locations for THIS starter are registered, so the scanner cannot accidentally
	 * pick up migrations from host projects or other starters. The history table
	 * name uses a dedicated constant derived from module name. Construction-only;
	 * migrate() is driven separately by the runner bean below.
	 *
	 * @param dataSource the auto-configured primary DataSource
	 * @return Flyway instance for this starter (never null)
	 */
	@Bean(name = "localizeFlyway")
	@ConditionalOnClass(name = "org.flywaydb.core.Flyway")
	@ConditionalOnMissingBean(name = "localizeFlyway")
	@ConditionalOnProperty(prefix = "spring.flyway", name = "enabled", havingValue = "true",
			matchIfMissing = true)
	org.flywaydb.core.Flyway localizeFlyway(DataSource dataSource) {
		return org.flywaydb.core.Flyway.configure()
				.dataSource(dataSource)
				.locations(ConstLocalize.FLYWAY_LOCATION_CLASSPATH)
				.table(ConstLocalize.FLYWAY_HISTORY_TABLE)
				.baselineOnMigrate(true)
				.baselineVersion("0")
				.validateOnMigrate(true)
				.outOfOrder(false)
				.ignoreMigrationPatterns("*:missing")
				.load();
	}

	/**
	 * Runner that triggers migration for the isolated localize Flyway instance.
	 * Runs with highest precedence so starter SQLs are applied BEFORE any
	 * business-level ApplicationRunner/CommandLineRunner and BEFORE the first
	 * HTTP request that would hit a sys_language/sys_localize table. The runner
	 * depends on the host project's default flywayInitializer bean being fully
	 * applied first; Spring Boot always completes context refresh (including the
	 * host flywayInitializer) before any ApplicationRunner is invoked, so the
	 * execution order is naturally correct without manual @DependsOn.
	 *
	 * @param localizeFlyway the isolated Flyway instance injected by qualifier
	 * @return ApplicationRunner executed once on startup
	 */
	@Bean
	@ConditionalOnClass(name = "org.flywaydb.core.Flyway")
	@ConditionalOnMissingBean(name = "localizeFlywayMigrateRunner")
	@ConditionalOnProperty(prefix = "spring.flyway", name = "enabled", havingValue = "true",
			matchIfMissing = true)
	@Order(Ordered.HIGHEST_PRECEDENCE)
	ApplicationRunner localizeFlywayMigrateRunner(
			@org.springframework.beans.factory.annotation.Qualifier("localizeFlyway") org.flywaydb.core.Flyway localizeFlyway) {
		return new ApplicationRunner() {

			@Override
			public void run(ApplicationArguments args) throws Exception {
				try {
					org.flywaydb.core.api.MigrationResult result = localizeFlyway.migrate();
					if (ListHelper.isNotEmpty(result.warnings)) {
						log.warn("[localize] Flyway isolated migration finished with {} warnings.",
								result.warnings.size());
					}
					log.info("[localize] Flyway isolated migration applied {} script(s), history table {}.",
							result.migrationsExecuted, ConstLocalize.FLYWAY_HISTORY_TABLE);
				} catch (Exception e) {
					log.error(
							"[localize] Isolated Flyway migration FAILED on locations={}, table={}. Aborting startup to protect data consistency.",
							ConstLocalize.FLYWAY_LOCATION_CLASSPATH, ConstLocalize.FLYWAY_HISTORY_TABLE, e);
					throw e;
				}
			}
		};
	}
}