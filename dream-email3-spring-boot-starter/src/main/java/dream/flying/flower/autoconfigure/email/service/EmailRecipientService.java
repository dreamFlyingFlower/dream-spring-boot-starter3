package dream.flying.flower.autoconfigure.email.service;

import java.util.List;

import dream.flying.flower.autoconfigure.email.entity.EmailRecipientEntity;

/**
 * 邮件发送收件人服务接口
 *
 * @author 飞花梦影
 * @date 2026-05-25 13:25:57
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface EmailRecipientService {

	/**
	 * 批量保存收件人
	 *
	 * @param recipients 收件人列表
	 */
	void batchSave(List<EmailRecipientEntity> recipients);

	/**
	 * 根据发送日志ID查询收件人
	 *
	 * @param sendLogId 发送日志ID
	 * @return 收件人列表
	 */
	List<EmailRecipientEntity> findBySendLogId(Long sendLogId);
}
