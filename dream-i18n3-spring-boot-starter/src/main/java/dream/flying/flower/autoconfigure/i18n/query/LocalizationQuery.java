package dream.flying.flower.autoconfigure.i18n.query;

import dream.flying.flower.framework.web.query.AbstractTenantQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 国际化查询
 *
 * @author 飞花梦影
 * @date 2026-05-20 10:43:03
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "国际化查询")
public class LocalizationQuery extends AbstractTenantQuery {

	private static final long serialVersionUID = 1L;

	@Schema(description = "消息编码")
	private String messageCode;

	@Schema(description = "消息内容")
	private String messageContent;

	@Schema(description = "语言")
	private String lang;

	@Schema(description = "国家/地区")
	private String country;

	@Schema(description = "区域脚本")
	private String script;

	@Schema(description = "区域变体代码")
	private String variant;

	@Schema(description = "备注")
	private String remark;
}