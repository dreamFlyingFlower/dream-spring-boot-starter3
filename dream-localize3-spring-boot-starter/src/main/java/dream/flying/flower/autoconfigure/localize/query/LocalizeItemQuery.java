package dream.flying.flower.autoconfigure.localize.query;

import dream.flying.flower.framework.web.query.AbstractTenantQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 国际化资源明细查询
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
@Schema(description = "国际化资源明细查询")
public class LocalizeItemQuery extends AbstractTenantQuery {

	private static final long serialVersionUID = 1L;

	@Schema(description = "语言ID")
	private Long languageId;

	@Schema(description = "国际化资源ID")
	private Long localizeId;

	@Schema(description = "国际化内容")
	private String content;

	@Schema(description = "国际化资源编码")
	private String localizeCode;

	@Schema(description = "语言")
	private String lang;

	@Schema(description = "国家/地区")
	private String country;
}