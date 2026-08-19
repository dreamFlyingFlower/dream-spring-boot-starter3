package dream.flying.flower.autoconfigure.excel.example.dto;

import java.io.Serializable;
import java.time.LocalDate;
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
public class BomDTO implements Serializable {

	private static final long serialVersionUID = 6641576013891708019L;

	@Schema(description = "自增id")
	private Long id;

	@Schema(description = "bom编号", hidden = true)
	private String bomCode;

	@Schema(description = "产品编码", requiredMode = RequiredMode.REQUIRED)
	private String productCode;

	@Schema(description = "产品名称", hidden = true)
	private String productName;

	@Schema(description = "产品规格型号", hidden = true)
	private String productModel;

	@Schema(description = "计量单位", requiredMode = RequiredMode.REQUIRED)
	private String measuremenUnit;

	@Schema(description = "设备扫描模式 字典值emes_scan_model")
	private String scanModel;

	@Schema(description = "bom状态", hidden = true)
	private String state;

	@Schema(description = "版本代号", requiredMode = RequiredMode.REQUIRED)
	private String versionCode;

	@Schema(description = "版本日期", requiredMode = RequiredMode.REQUIRED)
	private LocalDate versionDate;

	@Schema(description = "客户编码")
	private String customerCode;

	@TableField(exist = false)
	@Schema(description = "客户名称", hidden = true)
	private String customerName;

	@Schema(description = "客户零件编码")
	private String customerPartCode;

	@Schema(description = "项目编码")
	private String projectCode;

	@Schema(description = "工艺路线编码", requiredMode = RequiredMode.REQUIRED)
	private Integer routingId;

	@Schema(description = "工艺路线编码", hidden = true)
	private String routingCode;

	@Schema(description = "工艺路线名称", hidden = true)
	private String routingName;

	@Schema(description = "工艺路线级别", hidden = true)
	private String routingLevel;

	@Schema(description = "工作中心id", hidden = true)
	private Integer workCenterId;

	@Schema(description = "工作中心名称", hidden = true)
	private String workCenterName;

	@Schema(description = "有效标识，0：无效；1：有效", hidden = true)
	private String isEnable;

	@Schema(description = "是否来源外部导入，0：手动新增；1：外部U8系统导入", hidden = true)
	private String isFromImport;

	@Schema(description = "bom明细列表", hidden = true)
	private List<BomDetailDTO> detailDTOList;

	@Schema(description = "拼版数量")
	private String arrayNum;

	@JsonFormat(pattern = ConstDate.DATETIME)
	@TableField(fill = FieldFill.INSERT)
	private Date createTime;

	@JsonFormat(pattern = ConstDate.DATETIME)
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Date updateTime;
}