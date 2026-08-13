package dream.flying.flower.autoconfigure.email.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import dream.flying.flower.framework.constant.ConstConfig;
import lombok.Data;

/**
 * Email properties configuration
 *
 * @author 飞花梦影
 * @date 2026-05-25 13:25:57
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
@ConfigurationProperties(prefix = ConstConfig.Auto.EMAIL)
public class DreamEmailProperties {

	/**
	 * Enable email feature
	 */
	private boolean enabled = true;

	/**
	 * Enable email endpoint
	 */
	private boolean enabledEndpoint = true;

	/**
	 * Template directory path
	 */
	private String templateDir;

	/**
	 * Default from email
	 */
	private String defaultFromEmail;

	/**
	 * Default from name
	 */
	private String defaultFromName;
}