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
 * 字典项查询参数
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
@Schema(description = "字典项查询参数")
public class DictItemQuery extends AbstractQuery {

	private static final long serialVersionUID = 1L;

	@Schema(description = "字典ID")
	private Long dictId;

	@Schema(description = "字典编码")
	private String dictCode;

	@Schema(description = "字典项值")
	private Integer dictValue;

	@Schema(description = "字典标签")
	private String dictLabel;
}