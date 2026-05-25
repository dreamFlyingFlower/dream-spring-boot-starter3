package dream.flying.flower.autoconfigure.email.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

import dream.flying.flower.db.annotation.Unique;
import dream.flying.flower.framework.mybatis.plus.entity.AbstractTenantEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Email send log entity class
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_email_send_log")
public class EmailSendLogEntity extends AbstractTenantEntity {

	private static final long serialVersionUID = 1L;

	@Unique
	private String templateCode;

	/**
	 * Subject
	 */
	private String subject;

	/**
	 * From email address
	 */
	private String fromEmail;

	/**
	 * From name
	 */
	private String fromName;

	/**
	 * Send status: 1-pending, 2-success, 3-failed
	 */
	private Integer sendStatus;

	/**
	 * Error message if failed
	 */
	private String errorMessage;

	/**
	 * Send time
	 */
	private LocalDateTime sendTime;

	/**
	 * Attachment count
	 */
	private Integer attachmentCount;

	/**
	 * Remark
	 */
	private String remark;
}