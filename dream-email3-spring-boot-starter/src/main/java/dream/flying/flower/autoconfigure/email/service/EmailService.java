package dream.flying.flower.autoconfigure.email.service;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;

/**
 * Email service interface
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
public interface EmailService {

	/**
	 * Send email by template code
	 *
	 * @param toEmail      recipient email
	 * @param templateCode template code
	 * @param variables    template variables
	 */
	void sendEmail(String toEmail, String templateCode, Map<String, Object> variables);

	/**
	 * Send email with attachment
	 *
	 * @param toEmail     recipient email
	 * @param templateCode template code
	 * @param variables   template variables
	 * @param attachments attachment files
	 */
	void sendEmailWithAttachments(String toEmail, String templateCode, Map<String, Object> variables,
			FileSystemResource... attachments);

	/**
	 * Send email with multiple recipients
	 *
	 * @param toEmails     recipient email list
	 * @param ccEmails     CC email list
	 * @param bccEmails    BCC email list
	 * @param templateCode template code
	 * @param variables    template variables
	 */
	void sendEmail(List<String> toEmails, List<String> ccEmails, List<String> bccEmails, String templateCode,
			Map<String, Object> variables);
}
