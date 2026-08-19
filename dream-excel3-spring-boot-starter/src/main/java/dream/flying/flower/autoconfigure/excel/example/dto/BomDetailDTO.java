package dream.flying.flower.autoconfigure.excel.example.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;

import dream.flying.flower.ConstDate;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
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
@Schema(name = "Bom详情")
public class BomDetailDTO implements Serializable {

	private static final long serialVersionUID = 592778813131560413L;

	@Schema(description = "自增id")
	private Long id;

	@Schema(description = "bom编号", requiredMode = RequiredMode.REQUIRED)
	private String bomCode;

	@Schema(description = "产品编号", requiredMode = RequiredMode.REQUIRED)
	private String productCode;

	@Schema(description = "bom版本,2021/11/12，U8系统对应新增字段")
	private String version;

	@Schema(description = "bom层级", requiredMode = RequiredMode.REQUIRED)
	private Integer bomLevel;

	@Schema(description = "bom行号", requiredMode = RequiredMode.REQUIRED)
	private Integer bomLineCode;

	@Schema(description = "上级物料编码", requiredMode = RequiredMode.REQUIRED)
	private String parentMaterielCode;

	@Schema(description = "父节点id", requiredMode = RequiredMode.REQUIRED)
	private Integer parentId;

	@Schema(description = "设备扫描模式,字典值emes_scan_model")
	private String scanModel;

	@Schema(description = "物料编码", requiredMode = RequiredMode.REQUIRED)
	private String materielCode;

	@Schema(description = "物料名称", hidden = true)
	private String materielName;

	@Schema(description = "物料规格", hidden = true)
	private String specification;

	@Schema(description = "物料单位", hidden = true)
	private String measuringUnit;

	@Schema(description = "物料使用数量", requiredMode = RequiredMode.REQUIRED)
	private BigDecimal materielNum;

	@Schema(description = "基本数量")
	private BigDecimal basicNum;

	@Schema(description = "基础数量")
	private BigDecimal baseNum;

	@Schema(description = "物料位置", requiredMode = RequiredMode.REQUIRED)
	private String materielPosition;

	@Schema(description = "物料类型 1:自制件 2：采购件 3：客供件", requiredMode = RequiredMode.REQUIRED)
	private String materielType;

	@Schema(description = "物料说明")
	private String materielRemark;

	@Schema(description = "物料产地/品牌")
	private String materielSource;

	@Schema(description = "加工路线")
	private Integer madeId;

	@Schema(description = "加工类型")
	private String madeType;

	@Schema(description = "加工路线编码", hidden = true)
	private String madeCode;

	@Schema(description = "工艺/工艺路线名称", hidden = true)
	private String madeName;

	@Schema(description = "工艺路线级别", hidden = true)
	private String madeLevel;

	@Schema(description = "工作中心id", requiredMode = RequiredMode.REQUIRED)
	private Integer workCenterId;

	@Schema(description = "工作中心", hidden = true)
	private String workCenterName;

	@Schema(description = "装配路线", requiredMode = RequiredMode.REQUIRED)
	private String assemble;

	@Schema(description = "上料工序id", requiredMode = RequiredMode.REQUIRED)
	private Integer feedingProcessId;

	@Schema(description = "上料工序编码", hidden = true)
	private String feedingProcessCode;

	@Schema(description = "上料工序名称", hidden = true)
	private String feedingProcessName;

	@Schema(description = "子节点列表", hidden = true)
	private List<BomDetailDTO> childrenList;

	@Schema(description = "入库编码")
	private String halfStockCode;

	@Schema(description = "入库数量")
	private BigDecimal inQuantity;

	@Schema(description = "入库时间")
	private LocalDateTime putLibTime;

	@Schema(description = "锁定数量")
	private BigDecimal lockQuantity;

	@Schema(description = "上料验证标识,字典值emes_check_flag", requiredMode = RequiredMode.REQUIRED)
	private String feedingCheckFlag;

	@Schema(description = "导入错误信息，后台返回", hidden = true)
	private String errorMsg;

	@Schema(description = "替代料标识")
	private String newFlag;

	@JsonFormat(pattern = ConstDate.DATETIME)
	@TableField(fill = FieldFill.INSERT)
	private Date createTime;

	@JsonFormat(pattern = ConstDate.DATETIME)
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Date updateTime;
}