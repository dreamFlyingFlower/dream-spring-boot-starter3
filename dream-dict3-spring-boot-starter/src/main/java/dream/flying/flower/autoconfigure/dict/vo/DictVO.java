package dream.flying.flower.autoconfigure.dict.vo;

import java.util.List;

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
 * 字典DTO
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
@Schema(description = "字典")
public class DictVO extends AbstractTenantVO {

	private static final long serialVersionUID = 1L;

	@Schema(description = "字典编码", example = "gender", requiredMode = RequiredMode.REQUIRED)
	@NotBlank(message = "字典编码不能为空", groups = ValidAdds.class)
	@Size(max = 32, message = "字典编码长度不能超过 32 个字符", groups = { ValidAdds.class, ValidEdits.class })
	private String dictCode;

	@Schema(description = "字典名称", example = "性别", requiredMode = RequiredMode.REQUIRED)
	@NotBlank(message = "字典名称不能为空", groups = ValidAdds.class)
	@Size(max = 32, message = "字典名称长度不能超过 32 个字符", groups = { ValidAdds.class, ValidEdits.class })
	private String dictName;

	@Schema(description = "国际化消息编码", example = "gender")
	@Size(max = 32, message = "动态sql长度不能超过 32 个字符", groups = { ValidAdds.class, ValidEdits.class })
	private String messageCode;

	@Schema(description = "来源:1-字典数据;2-动态SQL")
	private Integer dictSource;

	@Schema(description = "动态sql", example = "select gender from table")
	@Size(max = 512, message = "动态sql长度不能超过 512 个字符", groups = { ValidAdds.class, ValidEdits.class })
	private String dictSql;

	@Schema(description = "排序", requiredMode = RequiredMode.REQUIRED)
	@Min(value = 0, message = "排序值不能小于0")
	private Integer sortIndex;

	@Schema(description = "备注", example = "性别字典")
	@Size(max = 256, message = "备注长度不能超过 256 个字符", groups = { ValidAdds.class, ValidEdits.class })
	private String remark;

	@Schema(description = "字典项列表")
	private List<DictItemVO> dictItemVos;
}