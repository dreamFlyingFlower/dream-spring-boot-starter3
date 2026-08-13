package dream.flying.flower.autoconfigure.email.service;

import dream.flying.flower.autoconfigure.email.entity.EmailTemplateEntity;
import dream.flying.flower.autoconfigure.email.query.EmailTemplateQuery;
import dream.flying.flower.autoconfigure.email.vo.EmailTemplateVO;
import dream.flying.flower.framework.mybatis.plus.service.BaseServices;

/**
 * Email template service interface
 *
 * @author 飞花梦影
 * @date 2026-05-25 13:25:57
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface EmailTemplateService extends BaseServices<EmailTemplateEntity, EmailTemplateVO, EmailTemplateQuery> {

	/**
	 * Get email template by template code
	 *
	 * @param templateCode template code
	 * @return email template vo
	 */
	EmailTemplateVO getByCode(String templateCode);

	/**
	 * Enable / Disable email template
	 *
	 * @param id template ID
	 * @param status status
	 */
	boolean toggleEnable(Long id, Integer status);
}