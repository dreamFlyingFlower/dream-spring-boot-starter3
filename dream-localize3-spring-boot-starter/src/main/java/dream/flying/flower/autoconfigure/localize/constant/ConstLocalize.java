package dream.flying.flower.autoconfigure.localize.constant;

import java.time.Duration;
import java.util.List;

import dream.flying.flower.autoconfigure.localize.enums.LocaleResolverType;

/**
 * 国际化常量
 *
 * <p>
 * All localize constants are flattened directly into this interface. Legacy
 * nested interfaces {@code Resolver} and {@code Defaults} no longer exist:
 * <ul>
 * <li>The resolver strategy string constants were removed because
 *     {@link LocaleResolverType} enum takes their place as the single source of
 *     truth for both IDE hints and configuration binding.</li>
 * <li>All defaults previously declared in {@code Defaults} are now top-level
 *     constants on {@code ConstLocalize} for shorter import paths and easier
 *     code reading.</li>
 * </ul>
 * </p>
 *
 * @author 飞花梦影
 * @date 2026-05-26 15:31:55
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface ConstLocalize {

	String MODULE_NAME = "localize";

	/**
	 * Default locale tag (BCP-47 format, preferred over legacy zh_CN underscore)
	 */
	String DEFAULT_LOCALE = "zh-CN";

	/**
	 * Default locale resolver strategy enum type. HEADER is the default strategy
	 * for stateless REST / JWT architectures: resolver reads a custom named HTTP
	 * header ({@link #DEFAULT_HEADER_NAME}) carrying a single BCP-47 language tag.
	 */
	LocaleResolverType DEFAULT_LOCALE_RESOLVER_TYPE = LocaleResolverType.HEADER;

	/**
	 * Default locale change request parameter name. Used by SESSION and COOKIE
	 * resolver strategies to allow language switching via URL query parameter.
	 */
	String DEFAULT_LOCALE_CHANGE_PARAM = "lang";

	/**
	 * Default cookie name used by the COOKIE resolver strategy
	 */
	String DEFAULT_COOKIE_NAME = "dream_lang";

	/**
	 * Default cookie path
	 */
	String DEFAULT_COOKIE_PATH = "/";

	/**
	 * Default cookie max age (7 days). Used as {@code Set-Cookie: Max-Age} value.
	 */
	Duration DEFAULT_COOKIE_MAX_AGE = Duration.ofDays(7);

	/**
	 * Default cookie HttpOnly flag. When true, cookie is not accessible from
	 * browser JS for reduced XSS surface.
	 */
	boolean DEFAULT_COOKIE_HTTP_ONLY = true;

	/**
	 * Default ignore-invalid-locale flag. If true, malformed locale input is
	 * silently ignored and the fallback default locale is used instead of throwing.
	 */
	boolean DEFAULT_IGNORE_INVALID_LOCALE = true;

	/**
	 * Default custom header name used by the HEADER resolver strategy. Frontend
	 * passes the current BCP-47 language tag (e.g. zh-CN) inside this header key
	 * for every request when {@code dream.localize.locale-resolver=HEADER}.
	 */
	String DEFAULT_HEADER_NAME = "X-App-Language";

	/**
	 * Default enabled-cache-endpoint flag. False by default for security because
	 * cache eviction is a potentially dangerous operator-only action.
	 */
	boolean DEFAULT_ENABLED_CACHE_ENDPOINT = false;

	/**
	 * Default list of supported locale tags. The ACCEPT_HEADER resolver will
	 * strictly match incoming Accept-Language values against this list.
	 */
	List<String> DEFAULT_SUPPORTED_LOCALES = List.of("zh-CN", "en-US");
}
