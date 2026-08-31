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
 * {@link LocaleResolverType} enum takes their place as the single source of
 * truth for both IDE hints and configuration binding.</li>
 * <li>All defaults previously declared in {@code Defaults} are now top-level
 * constants on {@code ConstLocalize} for shorter import paths and easier code
 * reading.</li>
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
	 * Default locale resolver strategy enum type. ACCEPT_HEADER is the default
	 * strategy for stateless REST / JWT architectures
	 */
	LocaleResolverType DEFAULT_LOCALE_RESOLVER_TYPE = LocaleResolverType.ACCEPT_HEADER;

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
	 * Default list of supported locale tags. The ACCEPT_HEADER resolver will
	 * strictly match incoming Accept-Language values against this list.
	 */
	List<String> DEFAULT_SUPPORTED_LOCALES = List.of("zh-CN", "en-US");

	/**
	 * Flyway classpath location where localize starter SQL migrations live. Each
	 * starter MUST use its own migration sub-directory under classpath:db/migration
	 * (e.g. localize, quartz, job etc.) so SQLs from different starters are not
	 * merged into the same default locations bucket and cannot collide on
	 * duplicated version numbers.
	 *
	 * Rule: {@code db/migration/<MODULE_NAME>} where MODULE_NAME is the starter
	 * identifier (localize for this starter).
	 */
	String FLYWAY_LOCATION_PATH = "db/migration/" + MODULE_NAME;

	/**
	 * Full classpath: prefix form of the migration directory for use in
	 * FlywayConfigurationCustomizer locations arrays.
	 */
	String FLYWAY_LOCATION_CLASSPATH = "classpath:" + FLYWAY_LOCATION_PATH;

	/**
	 * Independent schema history table name used by the isolated per-starter
	 * Flyway instance. Each starter MUST reserve a UNIQUE, non-colliding table
	 * name using the pattern {@code flyway_<MODULE_NAME>_history} so two starters
	 * can use exactly the same migration version prefix (e.g. both V1.0.0) and
	 * NEVER collide. Rule: table name length <= MySQL 64 char identifier limit,
	 * all lowercase, underscore separated.
	 */
	String FLYWAY_HISTORY_TABLE = "flyway_" + MODULE_NAME + "_history";
}