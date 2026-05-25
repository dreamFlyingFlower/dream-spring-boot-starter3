package dream.flying.flower.autoconfigure.email.service;

import java.util.Map;

import org.springframework.core.io.FileSystemResource;

/**
 * Email send log service interface
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
public interface EmailSendLogService {

	/**
	 * Save email send log
	 *
	 * @param sendLog email send log entity
	 */
	void saveLog(dream.flying.flower.autoconfigure.email.entity.EmailSendLogEntity sendLog);

	/**
	 * Update email send log status
	 *
	 * @param id           log ID
	 * @param sendStatus   send status
	 * @param errorMessage error message if failed
	 */
	void updateLogStatus(Long id, Integer sendStatus, String errorMessage);
}
