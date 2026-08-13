package dream.flying.flower.autoconfigure.email;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;

/**
 * Email service interface
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
public interface EmailManager {

	/**
	 * Send email by template code
	 *
	 * @param to recipient email
	 * @param templateCode template code
	 * @param variables template variables
	 */
	void send(String to, String templateCode, Map<String, Object> variables);

	/**
	 * Send email with multiple recipients
	 *
	 * @param tos recipient email list
	 * @param ccs CC email list
	 * @param bccs BCC email list
	 * @param templateCode template code
	 * @param variables template variables
	 */
	void send(List<String> tos, List<String> ccs, List<String> bccs, String templateCode,
			Map<String, Object> variables);

	/**
	 * Send email with attachment
	 *
	 * @param to recipient email
	 * @param templateCode template code
	 * @param variables template variables
	 * @param attachments attachment files
	 */
	void sendAttachments(String to, String templateCode, Map<String, Object> variables,
			FileSystemResource... attachments);
}