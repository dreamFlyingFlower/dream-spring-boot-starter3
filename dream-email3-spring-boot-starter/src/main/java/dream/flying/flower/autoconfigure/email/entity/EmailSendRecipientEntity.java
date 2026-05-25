package dream.flying.flower.autoconfigure.email.entity;

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
 * Email send recipient entity class
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
@TableName("sys_email_send_recipient")
public class EmailSendRecipientEntity extends AbstractTenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * Send log ID (unique with email and recipient_type when not deleted)
	 */
	@Unique
	private Long sendLogId;

	/**
	 * Email address (unique with send_log_id and recipient_type when not deleted)
	 */
	@Unique
	private String email;

	/**
	 * Recipient type: 1-to, 2-cc, 3-bcc (unique with send_log_id and email when not deleted)
	 */
	@Unique
	private Integer recipientType;
}
