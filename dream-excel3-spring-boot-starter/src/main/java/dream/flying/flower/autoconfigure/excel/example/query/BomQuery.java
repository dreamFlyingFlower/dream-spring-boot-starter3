package dream.flying.flower.autoconfigure.excel.example.query;

import dream.flying.flower.framework.web.query.AbstractQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BomQuery extends AbstractQuery {

	private static final long serialVersionUID = 2529364438634102497L;

	@Schema(description = "自增id")
	private Long id;

	@Schema(description = "产品编号")
	private String productCode;

	@Schema(description = "产品名称")
	private String productName;

	@Schema(description = "有效标识，0：无效；1：有效 2：待维护")
	private String isEnable;

	@Schema(description = "有效标识，0：手动新增；1：外部导入")
	private String isFromImport;

	@Schema(description = "bom状态")
	private String state;
}