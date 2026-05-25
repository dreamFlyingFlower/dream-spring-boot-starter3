package dream.flying.flower.autoconfigure.email.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import dream.flying.flower.autoconfigure.email.entity.EmailSendRecipientEntity;
import dream.flying.flower.autoconfigure.email.mapper.EmailSendRecipientMapper;
import dream.flying.flower.autoconfigure.email.service.EmailSendRecipientService;
import lombok.extern.slf4j.Slf4j;

/**
 * Email send recipient service implementation
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
@Slf4j
@Service
public class EmailSendRecipientServiceImpl extends ServiceImpl<EmailSendRecipientMapper, EmailSendRecipientEntity>
		implements EmailSendRecipientService {

	@Autowired
	private EmailSendRecipientMapper emailSendRecipientMapper;

	@Override
	public void batchSave(List<EmailSendRecipientEntity> recipients) {
		if (recipients == null || recipients.isEmpty()) {
			return;
		}
		for (EmailSendRecipientEntity recipient : recipients) {
			emailSendRecipientMapper.insert(recipient);
		}
		log.debug("Batch saved {} email send recipients", recipients.size());
	}

	@Override
	public List<EmailSendRecipientEntity> findBySendLogId(Long sendLogId) {
		return emailSendRecipientMapper.selectList(new LambdaQueryWrapper<EmailSendRecipientEntity>()
				.eq(EmailSendRecipientEntity::getSendLogId, sendLogId)
				.eq(EmailSendRecipientEntity::getDeleted, 0));
	}
}
