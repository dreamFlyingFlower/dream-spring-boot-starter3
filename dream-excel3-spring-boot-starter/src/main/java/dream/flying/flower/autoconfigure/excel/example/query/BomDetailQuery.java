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
public class BomDetailQuery extends AbstractQuery {

	private static final long serialVersionUID = -2689912713480785557L;

	@Schema(description = "bom明细id")
	private Long bomId;

	@Schema(description = "父节点id")
	private String parentId;

	@Schema(description = "bom编号")
	private String bomCode;

	@Schema(description = "bom层级")
	private String bomLevel;

	@Schema(description = "bom编号")
	private String productCode;

	@Schema(description = "物料编码")
	private String materielCode;

	@Schema(description = "装配路线")
	private String assemble;

	@Schema(description = "物料类型 1:自制件 2：采购件 3：客供件")
	private String materielType;

	@Schema(description = "工艺级别")
	private String routingLevel;

	@Schema(description = "产品/物料区别")
	private Integer bomType;

	@Schema(description = "随工单code")
	private String orderNo;

	@Schema(description = "上料验证标识")
	private String feedingCheckFlag;

	@Schema(description = "计划编码")
	private String planNo;

	@Schema(description = "上料工序名称")
	private String feedingProcessName;

	@Schema(description = "工作中心id")
	private Integer workCenterId;

	@Schema(description = "上料工序id")
	private Integer feedingProcessId;

	@Schema(description = "班组id")
	private Integer workGroupId;

	@Schema(description = "导入编码，导入excel使用")
	private String excelCode;

	@Schema(description = "时间戳，请求的时间戳")
	private String timestamp;
}