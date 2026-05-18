package dream.flying.flower.autoconfigure.i18n.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * I18n properties configuration
 *
 * @author 飞花梦影
 * @date 2026-05-18
 */
@Data
@ConfigurationProperties(prefix = "dream.i18n")
public class I18nProperties {

	/**
	 * Enable i18n feature
	 */
	private boolean enabled = true;

	/**
	 * Default locale
	 */
	private String defaultLocale = "zh_CN";

	/**
	 * Cache expire time in hours
	 */
	private long cacheExpireHours = 24;
}
