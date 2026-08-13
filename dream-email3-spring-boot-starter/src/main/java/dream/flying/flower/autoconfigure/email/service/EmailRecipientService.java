package dream.flying.flower.autoconfigure.email.service;

import java.util.List;

import dream.flying.flower.autoconfigure.email.entity.EmailRecipientEntity;
import dream.flying.flower.autoconfigure.email.query.EmailRecipientQuery;
import dream.flying.flower.autoconfigure.email.vo.EmailRecipientVO;
import dream.flying.flower.framework.mybatis.plus.service.BaseServices;

/**
 * 邮件发送收件人服务接口
 *
 * @author 飞花梦影
 * @date 2026-05-25 13:25:57
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface EmailRecipientService
		extends BaseServices<EmailRecipientEntity, EmailRecipientVO, EmailRecipientQuery> {

	/**
	 * 根据发送日志ID查询收件人
	 *
	 * @param sendLogId 发送日志ID
	 * @return 收件人列表
	 */
	List<EmailRecipientEntity> findBySendLogId(Long sendLogId);
}