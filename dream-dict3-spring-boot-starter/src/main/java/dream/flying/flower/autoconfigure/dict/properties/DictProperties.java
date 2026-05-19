package dream.flying.flower.autoconfigure.dict.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import dream.flying.flower.framework.constant.ConstConfig;
import lombok.Data;

/**
 * Dict properties configuration
 *
 * @author 飞花梦影
 * @date 2026-05-18
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Data
@ConfigurationProperties(prefix = ConstConfig.Sys.DICT)
public class DictProperties {

	/**
	 * Enable dict feature
	 */
	private boolean enabled = true;

	/**
	 * Cache expire time in hours
	 */
	private long cacheExpireHours = 12;

	/**
	 * Enable cache warmup
	 */
	private boolean warmupEnabled = true;
}