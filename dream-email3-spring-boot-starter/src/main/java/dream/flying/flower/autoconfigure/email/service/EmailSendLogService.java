package dream.flying.flower.autoconfigure.email.service;

import dream.flying.flower.autoconfigure.email.entity.EmailSendLogEntity;
import dream.flying.flower.autoconfigure.email.query.EmailSendLogQuery;
import dream.flying.flower.autoconfigure.email.vo.EmailSendLogVO;
import dream.flying.flower.framework.mybatis.plus.service.BaseServices;

/**
 * 邮件发送日志服务接口
 *
 * @author 飞花梦影
 * @date 2026-05-25 13:25:57
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface EmailSendLogService extends BaseServices<EmailSendLogEntity, EmailSendLogVO, EmailSendLogQuery> {

	/**
	 * 更新邮件发送日志状态
	 *
	 * @param id 日志ID
	 * @param sendStatus 发送状态
	 * @param errorMessage 失败错误信息
	 */
	void updateLogStatus(Long id, Integer sendStatus, String errorMessage);
}