package dream.flying.flower.autoconfigure.config.vo;

import dream.flying.flower.framework.core.valid.ValidAdds;
import dream.flying.flower.framework.core.valid.ValidEdits;
import dream.flying.flower.framework.web.model.AbstractTenantVO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 配置VO
 *
 * @author 飞花梦影
 * @date 2026-08-13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "配置VO")
public class ConfigVO extends AbstractTenantVO {

	private static final long serialVersionUID = 1L;

	@Schema(description = "配置键", example = "system.name", requiredMode = RequiredMode.REQUIRED)
	@NotBlank(message = "Config key cannot be empty", groups = ValidAdds.class)
	@Size(max = 128, message = "Config key length cannot exceed 128 characters",
			groups = { ValidAdds.class, ValidEdits.class })
	private String configKey;

	@Schema(description = "配置值", example = "My System Name")
	private String configValue;

	@Schema(description = "数据类型: string/number/boolean/json", example = "string")
	@Size(max = 32, message = "Data type length cannot exceed 32 characters",
			groups = { ValidAdds.class, ValidEdits.class })
	private String dataType;

	@Schema(description = "配置分类", example = "system")
	@Size(max = 64, message = "Category length cannot exceed 64 characters",
			groups = { ValidAdds.class, ValidEdits.class })
	private String category;

	@Schema(description = "排序", requiredMode = RequiredMode.REQUIRED)
	@Min(value = 0, message = "Sort index cannot be less than 0")
	private Integer sortIndex;

	@Schema(description = "状态:0-禁用;1-可用")
	private Integer status;

	@Schema(description = "备注")
	@Size(max = 256, message = "Remark length cannot exceed 256 characters",
			groups = { ValidAdds.class, ValidEdits.class })
	private String remark;
}