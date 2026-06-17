package dream.flying.flower.autoconfigure.email.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Azure应用相关信息
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
@Data
@ConfigurationProperties(prefix = "dream.azure")
public class AzureProperties {

	/**
	 * 是否为国内应用
	 */
	private boolean chainRegion = true;

	/**
	 * 租户ID或目录ID
	 */
	private String tenantId;

	/**
	 * 客户端ID
	 */
	private String clientId;

	/**
	 * 客户端密钥
	 */
	private String clientSecret;

	/**
	 * 国外scope
	 */
	private String scope = "https://graph.microsoft.com/.default";

	/**
	 * 国内scope
	 */
	private String chinaScope = "https://microsoftgraph.chinacloudapi.cn/.default";
}