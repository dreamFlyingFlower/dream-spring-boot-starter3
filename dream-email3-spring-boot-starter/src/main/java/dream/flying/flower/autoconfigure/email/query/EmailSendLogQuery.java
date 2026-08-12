package dream.flying.flower.autoconfigure.email.query;

import java.time.LocalDateTime;

import dream.flying.flower.framework.web.query.AbstractTenantQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 邮件发送日志查询
 *
 * @author 飞花梦影
 * @date 2026-08-12 14:43:04
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "邮件发送日志查询")
public class EmailSendLogQuery extends AbstractTenantQuery {

	private static final long serialVersionUID = 1L;

	@Schema(description = "模板编码")
	private String templateCode;

	@Schema(description = "主题")
	private String subject;

	@Schema(description = "发件人邮箱")
	private String fromEmail;

	@Schema(description = "发件人名称")
	private String fromName;

	@Schema(description = "发送状态: 1-待发送, 2-成功, 3-失败")
	private Integer sendStatus;

	@Schema(description = "失败错误信息")
	private String errorMessage;

	@Schema(description = "发送时间")
	private LocalDateTime sendTime;

	@Schema(description = "附件数量")
	private Integer attachmentCount;

	@Schema(description = "备注")
	private String remark;
}
