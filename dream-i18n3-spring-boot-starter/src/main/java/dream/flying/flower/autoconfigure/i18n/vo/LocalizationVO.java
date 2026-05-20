package dream.flying.flower.autoconfigure.i18n.vo;

import com.fhs.core.trans.vo.TransPojo;

import dream.flying.flower.framework.core.valid.ValidAdd;
import dream.flying.flower.framework.core.valid.ValidAdds;
import dream.flying.flower.framework.core.valid.ValidEdit;
import dream.flying.flower.framework.core.valid.ValidEdits;
import dream.flying.flower.framework.web.model.AbstractTenantVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 国际化
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
@Schema(description = "国际化")
public class LocalizationVO extends AbstractTenantVO implements TransPojo {

	private static final long serialVersionUID = 1L;

	@Schema(description = "国际化编码")
	@NotBlank(message = "国际化编码不能为空", groups = ValidAdds.class)
	@Size(max = 32, message = "国际化编码最大长度不能超过32", groups = { ValidAdd.class, ValidEdit.class })
	private String messageCode;

	@Schema(description = "国际化信息内容")
	@NotBlank(message = "国际化信息内容不能为空", groups = ValidAdds.class)
	@Size(max = 256, message = "国际化信息内容最大长度不能超过256", groups = { ValidAdd.class, ValidEdit.class })
	private String messageContent;

	@Schema(description = "语言")
	@NotBlank(message = "语言不能为空", groups = ValidAdds.class)
	@Size(max = 10, message = "语言最大长度不能超过10", groups = { ValidAdd.class, ValidEdit.class })
	private String lang;

	@Schema(description = "国家/地区")
	@Size(max = 32, message = "国家/地区长度不能超过 32 个字符", groups = { ValidAdds.class, ValidEdits.class })
	private String country;

	@Schema(description = "区域脚本")
	@Size(max = 32, message = "区域脚本长度不能超过 32 个字符", groups = { ValidAdds.class, ValidEdits.class })
	private String script;

	@Schema(description = "区域变体代码")
	@Size(max = 32, message = "区域变体代码长度不能超过 32 个字符", groups = { ValidAdds.class, ValidEdits.class })
	private String variant;

	@Schema(description = "备注")
	@Size(max = 256, message = "备注最大长度不能超过256", groups = { ValidAdd.class, ValidEdit.class })
	private String remark;
}