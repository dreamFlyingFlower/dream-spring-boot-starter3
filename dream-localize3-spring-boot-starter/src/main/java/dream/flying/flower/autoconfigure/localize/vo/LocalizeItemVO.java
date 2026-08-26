package dream.flying.flower.autoconfigure.localize.vo;

import com.fhs.core.trans.vo.TransPojo;

import dream.flying.flower.framework.core.valid.ValidAdds;
import dream.flying.flower.framework.web.model.AbstractTenantVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 国际化资源明细
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
@Schema(description = "国际化资源明细")
public class LocalizeItemVO extends AbstractTenantVO implements TransPojo {

	private static final long serialVersionUID = 1L;

	@Schema(description = "语言ID")
	@NotNull(message = "语言ID不能为空", groups = ValidAdds.class)
	private Long languageId;

	@Schema(description = "国际化资源ID")
	@NotNull(message = "国际化资源ID不能为空", groups = ValidAdds.class)
	private Long localizeId;

	@Schema(description = "内容")
	@NotBlank(message = "内容不能为空", groups = ValidAdds.class)
	private String content;
}