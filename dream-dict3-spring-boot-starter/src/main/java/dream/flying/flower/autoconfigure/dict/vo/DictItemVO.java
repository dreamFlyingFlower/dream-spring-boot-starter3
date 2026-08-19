package dream.flying.flower.autoconfigure.dict.vo;

import dream.flying.flower.framework.core.valid.ValidAdds;
import dream.flying.flower.framework.core.valid.ValidEdits;
import dream.flying.flower.framework.web.model.AbstractTenantVO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
	@NotNull(message = "字典ID不能为空", groups = ValidAdds.class)
	private Long dictId;

	@Schema(description = "字典编码")
	@NotBlank(message = "字典编码不能为空", groups = ValidAdds.class)
	@Size(max = 32, message = "字典编码长度不能超过 32 个字符", groups = { ValidAdds.class, ValidEdits.class })
	private String dictCode;

	@Schema(description = "字典项编码")
	@NotBlank(message = "字典项编码不能为空", groups = ValidAdds.class)
	@Size(max = 32, message = "字典项编码长度不能超过 32 个字符", groups = { ValidAdds.class, ValidEdits.class })
	private String itemCode;

	@Schema(description = "字典项名称")
	@NotBlank(message = "字典项名称不能为空", groups = ValidAdds.class)
	@Size(max = 64, message = "字典项名称长度不能超过 64 个字符", groups = { ValidAdds.class, ValidEdits.class })
	private String itemName;

	@Schema(description = "字典项值")
	@NotNull(message = "字典项值不能为空", groups = ValidAdds.class)
	private Integer itemValue;

	@Schema(description = "国际化消息编码")
	@Size(max = 32, message = "国际化消息编码不能超过 32 个字符", groups = { ValidAdds.class, ValidEdits.class })
	private String localizeCode;

	@Schema(description = "排序", requiredMode = RequiredMode.REQUIRED)
	@Min(value = 0, message = "排序值不能小于0")
	private Integer sortIndex;

	@Schema(description = "备注")
	@Size(max = 256, message = "备注长度不能超过 256 个字符", groups = { ValidAdds.class, ValidEdits.class })
	private String remark;
}