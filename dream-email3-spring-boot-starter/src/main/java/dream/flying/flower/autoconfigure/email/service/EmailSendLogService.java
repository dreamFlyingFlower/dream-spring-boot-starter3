package dream.flying.flower.autoconfigure.email.service;

import dream.flying.flower.autoconfigure.email.entity.EmailSendLogEntity;

/**
 * 邮件发送日志服务接口
 *
 * @author 飞花梦影
 * @date 2026-05-25 13:25:57
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface EmailSendLogService {

	/**
	 * 保存邮件发送日志
	 *
	 * @param sendLog 邮件发送日志实体
	 */
	void saveLog(EmailSendLogEntity sendLog);

	/**
	 * 更新邮件发送日志状态
	 *
	 * @param id 日志ID
	 * @param sendStatus 发送状态
	 * @param errorMessage 失败错误信息
	 */
	void updateLogStatus(Long id, Integer sendStatus, String errorMessage);
}
