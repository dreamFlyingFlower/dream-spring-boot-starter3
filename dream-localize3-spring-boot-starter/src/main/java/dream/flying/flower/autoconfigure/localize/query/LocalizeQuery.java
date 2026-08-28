package dream.flying.flower.autoconfigure.localize.query;

import dream.flying.flower.db.annotation.Query;
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
public class LocalizeQuery extends AbstractTenantQuery {

	private static final long serialVersionUID = 1L;

	@Schema(description = "国际化编码")
	@Query
	private String localizeCode;

	@Schema(description = "语言ID")
	@Query
	private Long languageId;

	@Schema(description = "标准语言代码")
	@Query
	private String fullLang;

	@Schema(description = "国际化信息")
	@Query
	private String content;

	@Schema(description = "数据类型")
	@Query
	private Integer dataType;

	@Schema(description = "备注")
	@Query
	private String remark;
}