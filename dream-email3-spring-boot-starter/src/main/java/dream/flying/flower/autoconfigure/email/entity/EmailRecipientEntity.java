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
 * 邮件发送收件人实体类
 *
 * @author 飞花梦影
 * @date 2026-05-25 13:25:57
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_email_recipient")
public class EmailRecipientEntity extends AbstractTenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 发送日志ID (未删除时与邮箱和收件人类型唯一)
	 */
	@Unique
	private Long sendLogId;

	/**
	 * 邮箱地址 (未删除时与发送日志ID和收件人类型唯一)
	 */
	@Unique
	private String email;

	/**
	 * 收件人类型: 1-收件人, 2-抄送, 3-密送 (未删除时与发送日志ID和邮箱唯一)
	 */
	@Unique
	private Integer recipientType;
}