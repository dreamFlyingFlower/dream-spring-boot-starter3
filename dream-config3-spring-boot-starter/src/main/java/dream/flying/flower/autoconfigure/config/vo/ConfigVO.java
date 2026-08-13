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
 * Config VO
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
@Schema(description = "Config")
public class ConfigVO extends AbstractTenantVO {

	private static final long serialVersionUID = 1L;

	@Schema(description = "Config key", example = "system.name", requiredMode = RequiredMode.REQUIRED)
	@NotBlank(message = "Config key cannot be empty", groups = ValidAdds.class)
	@Size(max = 128, message = "Config key length cannot exceed 128 characters", groups = { ValidAdds.class,
			ValidEdits.class })
	private String configKey;

	@Schema(description = "Config value", example = "My System Name")
	private String configValue;

	@Schema(description = "Data type: string/number/boolean/json", example = "string")
	@Size(max = 32, message = "Data type length cannot exceed 32 characters", groups = { ValidAdds.class,
			ValidEdits.class })
	private String dataType;

	@Schema(description = "Config category", example = "system")
	@Size(max = 64, message = "Category length cannot exceed 64 characters", groups = { ValidAdds.class,
			ValidEdits.class })
	private String category;

	@Schema(description = "Config description", example = "System name configuration")
	@Size(max = 512, message = "Description length cannot exceed 512 characters", groups = { ValidAdds.class,
			ValidEdits.class })
	private String description;

	@Schema(description = "Sort index", requiredMode = RequiredMode.REQUIRED)
	@Min(value = 0, message = "Sort index cannot be less than 0")
	private Integer sortIndex;

	@Schema(description = "Status: 0-disabled, 1-enabled")
	private Integer status;

	@Schema(description = "Remark")
	@Size(max = 256, message = "Remark length cannot exceed 256 characters", groups = { ValidAdds.class,
			ValidEdits.class })
	private String remark;
}