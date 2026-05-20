package dream.flying.flower.autoconfigure.i18n.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import dream.flying.flower.framework.constant.ConstConfig;
import lombok.Data;

/**
 * I18n properties configuration
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
@ConfigurationProperties(prefix = ConstConfig.Auto.LOCALIZATION)
public class LocalizationProperties {

	/**
	 * Enable i18n feature
	 */
	private boolean enabled = true;

	/**
	 * Enable endpoint
	 */
	private boolean enabledEndpoint = true;

	/**
	 * Default locale
	 */
	private String defaultLocale = "zh_CN";

	/**
	 * Cache expire time in hours
	 */
	private long cacheExpireHours = 24;
}