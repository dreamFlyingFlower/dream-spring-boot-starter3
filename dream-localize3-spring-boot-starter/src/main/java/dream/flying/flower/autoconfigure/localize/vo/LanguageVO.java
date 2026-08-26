package dream.flying.flower.autoconfigure.localize.vo;

import org.hibernate.validator.constraints.Range;

import com.fhs.core.trans.vo.TransPojo;

import dream.flying.flower.framework.core.valid.ValidAdd;
import dream.flying.flower.framework.core.valid.ValidAdds;
import dream.flying.flower.framework.core.valid.ValidEdit;
import dream.flying.flower.framework.core.valid.ValidEdits;
import dream.flying.flower.framework.web.model.AbstractTenantVO;
import io.swagger.v3.oas.annotations.media.Schema;
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
 * 语言
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
@Schema(description = "语言")
public class LanguageVO extends AbstractTenantVO implements TransPojo {

	private static final long serialVersionUID = 1L;

	@Schema(description = "显示名称")
	@NotBlank(message = "显示名称不能为空", groups = ValidAdds.class)
	@Size(max = 32, message = "显示名称最大长度不能超过 32 个字符", groups = { ValidAdd.class, ValidEdit.class })
	private String displayName;

	@Schema(description = "语言")
	@NotBlank(message = "语言不能为空", groups = ValidAdds.class)
	@Size(max = 8, message = "语言最大长度不能超过 8 个字符", groups = { ValidAdd.class, ValidEdit.class })
	private String lang;

	@Schema(description = "区域脚本代码")
	@Size(max = 8, message = "区域脚本代码长度不能超过 8 个字符", groups = { ValidAdds.class, ValidEdits.class })
	private String script;

	@Schema(description = "国家/地区")
	@Size(max = 8, message = "国家/地区长度不能超过 8 个字符", groups = { ValidAdds.class, ValidEdits.class })
	private String country;

	@Schema(description = "区域变体代码")
	@Size(max = 8, message = "区域变体代码长度不能超过 8 个字符", groups = { ValidAdds.class, ValidEdits.class })
	private String variant;

	@Schema(description = "启用标志:0-未启用;1-启用")
	@Range(min = 0, max = 1)
	private Integer enabled;

	@Schema(description = "排序")
	@Min(1)
	private Integer sortIndex;

	@Schema(description = "备注")
	@Size(max = 256, message = "备注最大长度不能超过 256 个字符", groups = { ValidAdd.class, ValidEdit.class })
	private String remark;
}