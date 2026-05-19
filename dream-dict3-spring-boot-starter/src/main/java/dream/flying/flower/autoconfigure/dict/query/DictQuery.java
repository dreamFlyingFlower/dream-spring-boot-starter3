package dream.flying.flower.autoconfigure.dict.query;

import dream.flying.flower.framework.web.query.AbstractQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 字典查询参数
 * 
 * @author 飞花梦影
 * @date 2022-09-01 16:09:57
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "字典查询参数")
public class DictQuery extends AbstractQuery {

	private static final long serialVersionUID = 1L;

	/**
	 * 字典编码
	 */
	@Schema(description = "字典编码")
	private String dictCode;

	/**
	 * 字典名称
	 */
	@Schema(description = "字典名称")
	private String dictName;

	/**
	 * 来源
	 */
	@Schema(description = "来源:1-字典数据;2-动态SQL")
	private Integer dictSource;

	/**
	 * 动态sql
	 */
	@Schema(description = "动态sql")
	private String dictSql;
}