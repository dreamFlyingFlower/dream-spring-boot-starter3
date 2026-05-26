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
 * Email template entity class
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
@TableName("sys_email_template")
public class EmailTemplateEntity extends AbstractTenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * Template code (unique with tenant_id when not deleted)
	 */
	@Unique
	private String templateCode;

	/**
	 * Template name
	 */
	private String templateName;

	/**
	 * Template type: verification_code, notification, marketing, etc.
	 */
	private String templateType;

	/**
	 * Template file path (relative to template directory)
	 */
	private String templatePath;

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
	 * Status: 0-disabled, 1-enabled
	 */
	private Integer status;

	/**
	 * Remark
	 */
	private String remark;
}
