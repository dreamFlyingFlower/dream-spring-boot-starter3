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
 * Email template query
 *
 * @author 飞花梦影
 * @date 2024-08-01
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "邮件模板查询")
public class EmailTemplateQuery extends AbstractTenantQuery {

	private static final long serialVersionUID = 1L;

	@Schema(description = "Template code (unique with tenant_id when not deleted)")
	private String templateCode;

	@Schema(description = "Template name")
	private String templateName;

	@Schema(description = "Template file path (relative to template directory)")
	private String templatePath;

	@Schema(description = "Subject")
	private String subject;

	@Schema(description = "From email address")
	private String fromEmail;

	@Schema(description = "From name")
	private String fromName;

	@Schema(description = "Status: 0-disabled, 1-enabled")
	private Integer status;

	@Schema(description = "Remark")
	private String remark;
}