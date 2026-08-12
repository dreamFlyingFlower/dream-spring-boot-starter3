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
 * 邮件发送日志实体类
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
@TableName("sys_email_send_log")
public class EmailSendLogEntity extends AbstractTenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 模板编码
	 */
	@Unique
	private String templateCode;

	/**
	 * 主题
	 */
	private String subject;

	/**
	 * 发件人邮箱
	 */
	private String fromEmail;

	/**
	 * 发件人名称
	 */
	private String fromName;

	/**
	 * 发送状态: 1-待发送, 2-成功, 3-失败
	 */
	private Integer sendStatus;

	/**
	 * 失败错误信息
	 */
	private String errorMessage;

	/**
	 * 发送时间
	 */
	private LocalDateTime sendTime;

	/**
	 * 附件数量
	 */
	private Integer attachmentCount;

	/**
	 * 备注
	 */
	private String remark;
}