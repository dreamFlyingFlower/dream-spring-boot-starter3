package dream.flying.flower.autoconfigure.localize.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

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