package dream.flying.flower.autoconfigure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import dream.flying.flower.framework.constant.ConstConfig;
import lombok.Data;

/**
 * Config properties configuration
 *
 * @author 飞花梦影
 * @date 2026-08-13
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Data
@ConfigurationProperties(prefix = ConstConfig.Auto.CONFIG)
public class DreamConfigProperties {

	/**
	 * Enable config feature
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
	private boolean warmupEnabled = true;

	/**
	 * Document Api group
	 */
	private String apiGroup = "系统";

	/**
	 * Document Api scan package
	 */
	private String apiPackageScan = "dream.flying.flower.autoconfigure.config";
}