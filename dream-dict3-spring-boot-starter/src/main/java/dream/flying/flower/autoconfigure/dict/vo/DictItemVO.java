package dream.flying.flower.autoconfigure.dict.vo;

import dream.flying.flower.framework.web.model.AbstractTenantVO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 字典项
 * 
 * @author 飞花梦影
 * @date 2022-09-01 16:09:10
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "字典项")
public class DictItemVO extends AbstractTenantVO {

	private static final long serialVersionUID = 1L;

	@Schema(description = "字典ID")
	@NotNull
	private Long dictId;

	@Schema(description = "字典编码")
	@NotBlank
	private String dictCode;

	@Schema(description = "字典项值")
	@NotNull
	private Integer dictValue;

	@Schema(description = "字典标签")
	private String dictLabel;

	@Schema(description = "国际化")
	private String localization;

	@Schema(description = "标签样式")
	private String labelClass;

	@Schema(description = "排序", requiredMode = RequiredMode.REQUIRED)
	@Min(value = 0, message = "排序值不能小于0")
	private Integer sortIndex;

	@Schema(description = "备注")
	private String remark;
}