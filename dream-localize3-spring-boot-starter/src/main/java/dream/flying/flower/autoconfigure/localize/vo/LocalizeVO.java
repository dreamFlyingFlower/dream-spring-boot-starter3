package dream.flying.flower.autoconfigure.localize.vo;

import com.fhs.core.trans.vo.TransPojo;

import dream.flying.flower.framework.core.valid.ValidAdd;
import dream.flying.flower.framework.core.valid.ValidAdds;
import dream.flying.flower.framework.core.valid.ValidEdit;
import dream.flying.flower.framework.web.model.AbstractTenantVO;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class LocalizeVO extends AbstractTenantVO implements TransPojo {

	private static final long serialVersionUID = 1L;

	@Schema(description = "国际化编码")
	@NotBlank(message = "国际化编码不能为空", groups = ValidAdds.class)
	@Size(max = 32, message = "国际化编码最大长度不能超过32", groups = { ValidAdd.class, ValidEdit.class })
	private String localizeCode;

	@Schema(description = "语言ID")
	@NotNull(message = "语言ID不能为空", groups = ValidAdds.class)
	private Long languageId;

	@Schema(description = "标准语言代码")
	@NotBlank(message = "标准语言代码不能为空", groups = ValidAdds.class)
	@Size(max = 32, message = "标准语言代码最大长度不能超过32", groups = { ValidAdd.class, ValidEdit.class })
	private String fullLang;

	@Schema(description = "国际化信息")
	@NotBlank(message = "国际化信息不能为空", groups = ValidAdds.class)
	private String content;

	@Schema(description = "数据类型")
	private Integer dataType;

	@Schema(description = "备注")
	@Size(max = 256, message = "备注最大长度不能超过256", groups = { ValidAdd.class, ValidEdit.class })
	private String remark;
}