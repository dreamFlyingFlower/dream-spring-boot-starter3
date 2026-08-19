package dream.flying.flower.autoconfigure.dict.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import dream.flying.flower.framework.constant.ConstConfig;
import dream.flying.flower.framework.constant.ConstStarter;
import lombok.Data;

/**
 * Dict properties configuration
 *
 * @author 飞花梦影
 * @date 2026-08-15 09:34:11
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Data
@ConfigurationProperties(prefix = ConstConfig.Auto.DICT)
public class DreamDictProperties {

	/**
	 * Enable dict feature
	 */
	private boolean enabled = true;

	/**
	 * Enable endpoint
	 */
	private boolean enabledEndpoint = true;

	/**
	 * Cache expire time in hours
	 */
	private long cacheExpireHours = 24;

	/**
	 * Enable cache warmup
	 */
	private boolean enabledWarmup = true;

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
	private String apiPackageScan = "dream.flying.flower.autoconfigure";
}