package dream.flying.flower.autoconfigure.localize.resolver;

import java.util.Locale;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

import dream.flying.flower.autoconfigure.localize.helpers.LocalizeHelpers;
import dream.flying.flower.lang.StrHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom named-header based LocaleResolver
 *
 * <p>
 * Unlike Spring's built-in {@code AcceptHeaderLocaleResolver} which only reads
 * the standard HTTP {@code Accept-Language} weighted quality list, this resolver
 * reads a single plain BCP-47 style language tag from a configurable custom
 * header name (e.g. {@code X-App-Language} or {@code lang}).
 * </p>
 *
 * <p>
 * Resolution rules (performance-first short-circuit):
 * <ol>
 * <li>If the configured custom header is present and non-blank, parse it with
 * {@link LocalizeHelpers#parse(String)} (BCP-47 first, then legacy underscore
 * fallback). Successful parse returns the resolved Locale immediately.</li>
 * <li>If header is missing / empty / unparsable, fall back to
 * {@link HttpServletRequest#getLocale()} which is the container's
 * interpretation of the standard Accept-Language header (or the server
 * default).</li>
 * <li>If the request-level Locale is also not available, use the configured
 * default Locale supplied at construction time.</li>
 * </ol>
 * </p>
 *
 * <p>
 * {@link #setLocale} is a no-op by design: the header is a client-provided
 * value; there is no server-side location to write a locale change back to for
 * a stateless custom-header architecture.
 * </p>
 *
 * @author 飞花梦影
 * @date 2026-08-29 10:00:00
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
public class HeaderLocaleResolver implements LocaleResolver {

	/**
	 * Name of the custom header that carries the BCP-47 language tag. Never null
	 * after construction.
	 */
	private final String headerName;

	/**
	 * Fallback default Locale used when neither the custom header nor the
	 * container Accept-Language default resolves to a usable value. Never null.
	 */
	private final Locale defaultLocale;

	/**
	 * Construct a new HeaderLocaleResolver with the given custom header name and
	 * default fallback Locale.
	 *
	 * @param headerName non-null non-blank HTTP header key
	 * @param defaultLocale non-null fallback Locale
	 */
	public HeaderLocaleResolver(String headerName, Locale defaultLocale) {
		if (StrHelper.isBlank(headerName)) {
			throw new IllegalArgumentException("headerName must not be blank");
		}
		if (defaultLocale == null) {
			throw new IllegalArgumentException("defaultLocale must not be null");
		}
		this.headerName = headerName;
		this.defaultLocale = defaultLocale;
	}

	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		// 1. Fast path: read custom header value and try BCP-47 parse
		String raw = request.getHeader(headerName);
		if (StrHelper.isNotBlank(raw)) {
			try {
				Locale parsed = LocalizeHelpers.parse(raw);
				if (parsed != null && StringUtils.hasText(parsed.getLanguage())) {
					return parsed;
				}
			} catch (Exception e) {
				log.warn("HeaderLocaleResolver failed to parse locale value '{}' from header '{}': {}",
						raw, headerName, e.getMessage());
			}
		}
		// 2. Fallback to servlet container interpretation of Accept-Language header
		try {
			Locale requestLocale = request.getLocale();
			if (requestLocale != null && StringUtils.hasText(requestLocale.getLanguage())) {
				return requestLocale;
			}
		} catch (Exception ignored) {
			// Should not happen in compliant containers, ignore safely
		}
		// 3. Final fallback to configured default locale (never null by constructor)
		return defaultLocale;
	}

	@Override
	public void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response,
			@Nullable Locale locale) {
		// No-op by design: stateless header-based resolver cannot persist a locale
		// change. The LocaleChangeInterceptor in HEADER mode should be disabled or
		// ignored by the caller to avoid misleading warning logs.
	}

	/**
	 * @return the configured custom header name, never null
	 */
	public String getHeaderName() {
		return headerName;
	}

	/**
	 * @return the configured default fallback Locale, never null
	 */
	public Locale getDefaultLocale() {
		return defaultLocale;
	}
}
