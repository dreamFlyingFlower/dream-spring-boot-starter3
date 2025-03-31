package dream.flying.flower.autoconfigure.logger.vo;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import dream.flying.flower.ConstDate;
import dream.flying.flower.framework.web.model.AbstractTenantVO;
import dream.flying.flower.framework.web.valid.ValidAdd;
import dream.flying.flower.framework.web.valid.ValidEdit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 日志业务视图类
 * 
 * @author 飞花梦影
 * @date 2025-03-18 22:41:03
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogVO extends AbstractTenantVO {

	private static final long serialVersionUID = 1L;

	@Schema(description = "链路追踪ID")
	@Size(max = 64, message = "链路追踪ID最大长度不能超过64", groups = { ValidAdd.class, ValidEdit.class })
	private String traceId;

	@Schema(description = "应用名称")
	@Size(max = 64, message = "应用名称最大长度不能超过64", groups = { ValidAdd.class, ValidEdit.class })
	private String appName;

	@Schema(description = "模块名称")
	@Size(max = 64, message = "模块名称最大长度不能超过64", groups = { ValidAdd.class, ValidEdit.class })
	private String moduleName;

	@Schema(description = "操作类型")
	@Size(max = 32, message = "操作类型最大长度不能超过32", groups = { ValidAdd.class, ValidEdit.class })
	private String operationType;

	@Schema(description = "操作概述")
	@Size(max = 512, message = "操作概述最大长度不能超过512", groups = { ValidAdd.class, ValidEdit.class })
	private String summary;

	@Schema(description = "类名")
	@Size(max = 256, message = "类名最大长度不能超过256", groups = { ValidAdd.class, ValidEdit.class })
	private String className;

	@Schema(description = "方法名")
	@Size(max = 256, message = "方法名最大长度不能超过256", groups = { ValidAdd.class, ValidEdit.class })
	private String methodName;

	@Schema(description = "请求IP")
	@Size(max = 64, message = "请求IP最大长度不能超过64", groups = { ValidAdd.class, ValidEdit.class })
	private String requestIp;

	@Schema(description = "请求URL")
	@Size(max = 256, message = "请求URL最大长度不能超过256", groups = { ValidAdd.class, ValidEdit.class })
	private String requestUrl;

	@Schema(description = "请求方式")
	@Size(max = 16, message = "请求方式最大长度不能超过16", groups = { ValidAdd.class, ValidEdit.class })
	private String requestMethod;

	@Schema(description = "请求参数")
	private String requestParam;

	@Schema(description = "请求体")
	private String requestBody;

	@Schema(description = "请求头")
	private String requestHeader;

	@Schema(description = "请求时间")
	@JsonFormat(pattern = ConstDate.DATETIME)
	@DateTimeFormat(pattern = ConstDate.DATETIME)
	private LocalDateTime requestTime;

	@Schema(description = "响应状态码")
	private Integer responseStatus;

	@Schema(description = "响应内容")
	private String responseBody;

	@Schema(description = "响应头")
	private String responseHeader;

	@Schema(description = "响应时间")
	@JsonFormat(pattern = ConstDate.DATETIME)
	@DateTimeFormat(pattern = ConstDate.DATETIME)
	private LocalDateTime responseTime;

	@Schema(description = "耗时(毫秒)")
	private Long costTime;

	@Schema(description = "是否成功:0->失败;1->成功")
	private Integer success;

	@Schema(description = "错误信息")
	private String errorMsg;

	@Schema(description = "用户ID")
	private Long userId;

	@Schema(description = "用户名")
	@Size(max = 32, message = "用户名最大长度不能超过32", groups = { ValidAdd.class, ValidEdit.class })
	private String username;

	@Schema(description = "用户昵称")
	@Size(max = 32, message = "用户昵称最大长度不能超过32", groups = { ValidAdd.class, ValidEdit.class })
	private String nickName;
}