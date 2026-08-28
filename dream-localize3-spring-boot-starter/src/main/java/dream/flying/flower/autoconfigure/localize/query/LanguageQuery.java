package dream.flying.flower.autoconfigure.localize.query;

import dream.flying.flower.db.annotation.Query;
import dream.flying.flower.db.enums.QueryType;
import dream.flying.flower.framework.web.query.AbstractTenantQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 语言查询
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
@Schema(description = "语言查询")
public class LanguageQuery extends AbstractTenantQuery {

	private static final long serialVersionUID = 1L;

	@Schema(description = "显示名称")
	@Query(type = QueryType.LIKE)
	private String displayName;

	@Schema(description = "语言")
	@Query(type = QueryType.LIKE)
	private String lang;

	@Schema(description = "区域脚本代码")
	private String script;

	@Schema(description = "国家/地区")
	private String country;

	@Schema(description = "区域变体代码")
	private String variant;

	@Schema(description = "标准语言代码")
	@Query(type = QueryType.LIKE)
	private String fullLang;

	@Schema(description = "启用标志:0-未启用;1-启用")
	private Integer enabled;

	@Schema(description = "备注")
	private String remark;
}