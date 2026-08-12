package dream.flying.flower.autoconfigure.email.query;

import dream.flying.flower.framework.web.query.AbstractTenantQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 邮件发送收件人查询
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
@Schema(description = "邮件发送收件人查询")
public class EmailRecipientQuery extends AbstractTenantQuery {

	private static final long serialVersionUID = 1L;

	@Schema(description = "发送日志ID")
	private Long sendLogId;

	@Schema(description = "邮箱地址")
	private String email;

	@Schema(description = "收件人类型: 1-收件人, 2-抄送, 3-密送")
	private Integer recipientType;
}
