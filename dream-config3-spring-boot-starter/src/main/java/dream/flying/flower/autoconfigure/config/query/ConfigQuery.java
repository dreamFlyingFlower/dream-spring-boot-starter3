package dream.flying.flower.autoconfigure.config.query;

import dream.flying.flower.framework.web.query.AbstractQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Config query parameter
 *
 * @author 飞花梦影
 * @date 2026-08-13
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Config query parameter")
public class ConfigQuery extends AbstractQuery {

	private static final long serialVersionUID = 1L;

	@Schema(description = "配置键")
	private String configKey;

	@Schema(description = "配置值")
	private String configValue;

	@Schema(description = "数据类型: string/number/boolean/json")
	private String dataType;

	@Schema(description = "配置分类")
	private String category;

	@Schema(description = "状态:0-禁用;1-可用")
	private Integer status;
}