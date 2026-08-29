package dream.flying.flower.autoconfigure.localize.enums;

/**
 * Locale resolver strategy type
 *
 * <p>
 * Single source of truth for the {@code dream.localize.locale-resolver}
 * configuration property. Spring Boot 3.x configuration-processor + IDE will
 * hint the available enum values directly inside YAML / properties files.
 * Callers can use {@link #name()} for a standard uppercase string form, or
 * {@link #toString()} which returns the same value as {@code name()}.
 * </p>
 *
 * @author 飞花梦影
 * @date 2026-08-29 10:00:00
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public enum LocaleResolverType {

	/**
	 * Session based locale resolver. Stateful. Language can be switched via
	 * locale change request parameter and persisted in HttpSession.
	 */
	SESSION,

	/**
	 * Cookie based locale resolver, suitable for stateless token apis. Language
	 * can be switched via locale change request parameter and persisted in a
	 * cookie for subsequent requests.
	 */
	COOKIE,

	/**
	 * Custom named HTTP header based locale resolver. Reads the single BCP-47
	 * language tag from the header key configured via
	 * {@code dream.localize.header-name} (default {@code X-App-Language}). This
	 * is <b>not</b> the standard Accept-Language weighted header; for that use
	 * {@link #ACCEPT_HEADER}.
	 */
	HEADER,

	/**
	 * Standard HTTP Accept-Language header based locale resolver. Uses Spring's
	 * built-in AcceptHeaderLocaleResolver. Read only, honors the supported
	 * locales list filter for exact matches against the browser's weighted list.
	 */
	ACCEPT_HEADER,

	/**
	 * Fixed locale resolver, always uses defaultLocale regardless of any
	 * request values. Useful for testing or single-locale deployments.
	 */
	FIXED
}
