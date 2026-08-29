package dream.flying.flower.autoconfigure.localize.properties;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import dream.flying.flower.autoconfigure.localize.constant.ConstLocalize;
import dream.flying.flower.autoconfigure.localize.enums.LocaleResolverType;
import dream.flying.flower.framework.constant.ConstConfig;
import dream.flying.flower.framework.constant.ConstStarter;
import lombok.Data;

/**
 * I18n properties configuration
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
@ConfigurationProperties(prefix = ConstConfig.Auto.LOCALIZE)
public class DreamLocalizeProperties {

	/**
	 * Enable i18n feature
	 */
	private boolean enabled = true;

	/**
	 * Enable endpoint (LocalizeEndpoint/LanguageEndpoint)
	 */
	private boolean enabledEndpoint = true;

	/**
	 * Enable cache endpoint (cache clear/evict). False by default for security
	 */
	private boolean enabledCacheEndpoint = ConstLocalize.DEFAULT_ENABLED_CACHE_ENDPOINT;

	/**
	 * Default locale tag in BCP-47 format, e.g. zh-CN. Also accepts legacy zh_CN
	 */
	private String defaultLocale = ConstLocalize.DEFAULT_LOCALE;

	/**
	 * Supported locale tag list. Empty means accept all locale tags
	 */
	private List<String> supportedLocales = ConstLocalize.DEFAULT_SUPPORTED_LOCALES;

	/**
	 * Locale resolver strategy. See
	 * {@link dream.flying.flower.autoconfigure.localize.enums.LocaleResolverType}
	 */
	private LocaleResolverType localeResolver = ConstLocalize.DEFAULT_LOCALE_RESOLVER_TYPE;

	/**
	 * Request parameter name for switching locale
	 */
	private String localeChangeParamName = ConstLocalize.DEFAULT_LOCALE_CHANGE_PARAM;

	/**
	 * If true, invalid locale input is ignored and default locale is used instead
	 */
	private boolean ignoreInvalidLocale = ConstLocalize.DEFAULT_IGNORE_INVALID_LOCALE;

	/**
	 * Custom header name used when {@code locale-resolver=HEADER}. Frontend
	 * passes the BCP-47 language tag (e.g. zh-CN) into this named header for
	 * each request in stateless token-based architectures.
	 */
	private String headerName = ConstLocalize.DEFAULT_HEADER_NAME;

	/**
	 * Cookie name used by the cookie-based locale resolver
	 */
	private String cookieName = ConstLocalize.DEFAULT_COOKIE_NAME;

	/**
	 * Cookie path used by the cookie-based locale resolver
	 */
	private String cookiePath = ConstLocalize.DEFAULT_COOKIE_PATH;

	/**
	 * Cookie max age for the cookie-based locale resolver
	 */
	private Duration cookieMaxAge = ConstLocalize.DEFAULT_COOKIE_MAX_AGE;

	/**
	 * Cookie http-only flag for the cookie-based locale resolver
	 */
	private boolean cookieHttpOnly = ConstLocalize.DEFAULT_COOKIE_HTTP_ONLY;

	/**
	 * Cache expire time in hours
	 */
	private Duration expire = Duration.ofHours(24);

	/**
	 * Enable api document
	 */
	private boolean enabledApi = true;

	/**
	 * Document Api group
	 */
	private String apiGroup = ConstStarter.API_GROUP;

	/**
	 * Document Api group name
	 */
	private String apiGroupName = ConstStarter.API_GROUP_NAME;

	/**
	 * Document Api scan package
	 */
	private String apiPackageScan = ConstStarter.API_PACKAGE_SCAN;
}
