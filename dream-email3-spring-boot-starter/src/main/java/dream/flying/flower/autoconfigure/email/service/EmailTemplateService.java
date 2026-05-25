package dream.flying.flower.autoconfigure.email.service;

import java.util.List;

import dream.flying.flower.autoconfigure.email.entity.EmailTemplateEntity;

/**
 * Email template service interface
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
public interface EmailTemplateService {

	/**
	 * Save email template
	 *
	 * @param template email template entity
	 */
	void saveTemplate(EmailTemplateEntity template);

	/**
	 * Update email template
	 *
	 * @param template email template entity
	 */
	void updateTemplate(EmailTemplateEntity template);

	/**
	 * Delete email template by ID
	 *
	 * @param id template ID
	 */
	void deleteTemplate(Long id);

	/**
	 * Get email template by ID
	 *
	 * @param id template ID
	 * @return email template entity
	 */
	EmailTemplateEntity getTemplateById(Long id);

	/**
	 * Get email template by template code
	 *
	 * @param templateCode template code
	 * @return email template entity
	 */
	EmailTemplateEntity getTemplateByCode(String templateCode);

	/**
	 * List all enabled email templates
	 *
	 * @return email template list
	 */
	List<EmailTemplateEntity> listEnabledTemplates();

	/**
	 * List all email templates
	 *
	 * @return email template list
	 */
	List<EmailTemplateEntity> listAllTemplates();

	/**
	 * Enable email template
	 *
	 * @param id template ID
	 */
	void enableTemplate(Long id);

	/**
	 * Disable email template
	 *
	 * @param id template ID
	 */
	void disableTemplate(Long id);
}
