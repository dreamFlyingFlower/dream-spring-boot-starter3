package dream.flying.flower.autoconfigure.email.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Email properties configuration
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
@Data
@ConfigurationProperties(prefix = "dream.email")
public class EmailProperties {

	/**
	 * Enable email feature
	 */
	private boolean enabled = true;

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