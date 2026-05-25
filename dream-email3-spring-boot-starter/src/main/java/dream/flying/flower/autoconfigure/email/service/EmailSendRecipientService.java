package dream.flying.flower.autoconfigure.email.service;

import java.util.List;

import dream.flying.flower.autoconfigure.email.entity.EmailSendRecipientEntity;

/**
 * Email send recipient service interface
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
public interface EmailSendRecipientService {

	/**
	 * Batch save recipients
	 *
	 * @param recipients recipient list
	 */
	void batchSave(List<EmailSendRecipientEntity> recipients);

	/**
	 * Find recipients by send log ID
	 *
	 * @param sendLogId send log ID
	 * @return recipient list
	 */
	List<EmailSendRecipientEntity> findBySendLogId(Long sendLogId);
}
