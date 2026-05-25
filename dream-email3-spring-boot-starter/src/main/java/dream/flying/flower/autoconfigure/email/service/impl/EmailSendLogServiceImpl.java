package dream.flying.flower.autoconfigure.email.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import dream.flying.flower.autoconfigure.email.entity.EmailSendLogEntity;
import dream.flying.flower.autoconfigure.email.mapper.EmailSendLogMapper;
import dream.flying.flower.autoconfigure.email.service.EmailSendLogService;
import lombok.extern.slf4j.Slf4j;

/**
 * Email send log service implementation
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
@Slf4j
@Service
public class EmailSendLogServiceImpl extends ServiceImpl<EmailSendLogMapper, EmailSendLogEntity>
		implements EmailSendLogService {

	@Autowired
	private EmailSendLogMapper emailSendLogMapper;

	@Override
	public void saveLog(EmailSendLogEntity sendLog) {
		emailSendLogMapper.insert(sendLog);
		log.debug("Email send log saved: id={}", sendLog.getId());
	}

	@Override
	public void updateLogStatus(Long id, Integer sendStatus, String errorMessage) {
		EmailSendLogEntity sendLog = emailSendLogMapper.selectById(id);
		if (sendLog != null) {
			sendLog.setSendStatus(sendStatus);
			sendLog.setErrorMessage(errorMessage);
			emailSendLogMapper.updateById(sendLog);
			log.debug("Email send log updated: id={}, status={}", id, sendStatus);
		}
	}
}
