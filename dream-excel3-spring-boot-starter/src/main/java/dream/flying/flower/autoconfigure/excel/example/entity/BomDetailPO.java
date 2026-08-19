package dream.flying.flower.autoconfigure.excel.example.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import dream.flying.flower.framework.mybatis.plus.entity.AbstractEntity;
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
@TableName("emes_basic_bom_item")
public class BomDetailPO extends AbstractEntity {

	private static final long serialVersionUID = 5309000039454555124L;

	@Schema(description = "bom编号")
	private String bomCode;

	@Schema(description = "产品编号")
	private String productCode;

	@Schema(description = "bom层级")
	private Integer bomLevel;

	@Schema(description = "bom行号")
	private Integer bomLineCode;

	@Schema(description = "父节点id")
	private Integer parentId;

	@Schema(description = "设备扫描模式，字典值emes_scan_model")
	private String scanModel;

	@Schema(description = "物料编码")
	private String materielCode;

	@Schema(description = "物料名称")
	@TableField(exist = false)
	private String materielName;

	@Schema(description = "规格")
	@TableField(exist = false)
	private String specification;

	@Schema(description = "计量单位")
	@TableField(exist = false)
	private String measuringUnit;

	@Schema(description = "物料使用数量")
	private BigDecimal materielNum;

	@Schema(description = "基本数量")
	private BigDecimal basicNum;

	@Schema(description = "基础数量")
	private BigDecimal baseNum;

	@Schema(description = "物料位置")
	private String materielPosition;

	@Schema(description = "物料类型")
	private String materielType;

	@Schema(description = "物料说明")
	private String materielRemark;

	@Schema(description = "物料产地/品牌")
	private String materielSource;

	@Schema(description = "加工路线")
	private Integer madeId;

	@Schema(description = "工艺路线级别")
	private String madeLevel;

	@TableField(exist = false)
	@Schema(description = "工艺/工艺路线编码")
	private String madeCode;

	@TableField(exist = false)
	@Schema(description = "工艺/工艺路线名称")
	private String madeName;

	@Schema(description = "工作中心id")
	private Integer workCenterId;

	@TableField(exist = false)
	@Schema(description = "工作中心", hidden = true)
	private String workCenterName;

	@Schema(description = "加工类型 2:工序组 3：工艺路线")
	private String madeType;

	@Schema(description = "装配路线(字典值)")
	private String assemble;

	@Schema(description = "上料工序id")
	private Integer feedingProcessId;

	@Schema(description = "上料工序编码")
	@TableField(exist = false)
	private String feedingProcessCode;

	@Schema(description = "上料工序名称")
	@TableField(exist = false)
	private String feedingProcessName;

	@Schema(description = "上料验证标识,字典值emes_check_flag")
	private String feedingCheckFlag;

	@TableField(exist = false)
	private LocalDateTime planStartTime;

	@TableField(exist = false)
	private LocalDateTime planEndTime;

	@TableField(exist = false)
	private List<BomDetailPO> children;
}