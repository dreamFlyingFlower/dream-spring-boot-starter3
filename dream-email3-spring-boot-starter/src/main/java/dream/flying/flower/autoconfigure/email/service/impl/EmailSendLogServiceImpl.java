package dream.flying.flower.autoconfigure.email.service.impl;

import org.springframework.stereotype.Service;

import dream.flying.flower.autoconfigure.email.convert.EmailSendLogConvert;
import dream.flying.flower.autoconfigure.email.entity.EmailSendLogEntity;
import dream.flying.flower.autoconfigure.email.mapper.EmailSendLogMapper;
import dream.flying.flower.autoconfigure.email.query.EmailSendLogQuery;
import dream.flying.flower.autoconfigure.email.service.EmailSendLogService;
import dream.flying.flower.autoconfigure.email.vo.EmailSendLogVO;
import dream.flying.flower.framework.mybatis.plus.service.impl.AbstractServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 邮件发送日志服务实现
 *
 * @author 飞花梦影
 * @date 2026-05-25 13:25:57
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSendLogServiceImpl extends AbstractServiceImpl<EmailSendLogEntity, EmailSendLogVO, EmailSendLogQuery,
		EmailSendLogConvert, EmailSendLogMapper> implements EmailSendLogService {

	@Override
	public void updateLogStatus(Long id, Integer sendStatus, String errorMessage) {
		EmailSendLogEntity sendLog = baseMapper.selectById(id);
		if (sendLog != null) {
			sendLog.setSendStatus(sendStatus);
			sendLog.setErrorMessage(errorMessage);
			baseMapper.updateById(sendLog);
			log.debug("Email send log updated: id={}, status={}", id, sendStatus);
		}
	}
}