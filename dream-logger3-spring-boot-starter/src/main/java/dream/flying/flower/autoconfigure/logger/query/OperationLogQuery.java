package dream.flying.flower.autoconfigure.logger.query;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import dream.flying.flower.ConstDate;
import dream.flying.flower.db.annotation.Query;
import dream.flying.flower.db.enums.QueryType;
import dream.flying.flower.framework.web.query.AbstractTenantQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 日志查询参数
 *
 * @author 飞花梦影
 * @date 2025-03-19 16:35:05
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogQuery extends AbstractTenantQuery {

	private static final long serialVersionUID = 1L;

	@Schema(description = "链路追踪ID")
	private String traceId;

	@Schema(description = "应用名称")
	private String appName;

	@Schema(description = "模块名称")
	private String moduleName;

	@Schema(description = "操作类型")
	private String operationType;

	@Schema(description = "操作概述")
	private String summary;

	@Schema(description = "类名")
	private String className;

	@Schema(description = "方法名")
	private String methodName;

	@Schema(description = "请求IP")
	private String requestIp;

	@Schema(description = "请求URL")
	private String requestUrl;

	@Schema(description = "请求方式")
	private String requestMethod;

	@Schema(description = "请求参数")
	private String requestParam;

	@Schema(description = "请求体")
	private String requestBody;

	@Schema(description = "请求头")
	private String requestHeader;

	@Schema(description = "请求时间")
	@Query(type = QueryType.BETWEEN)
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
	@Query(type = QueryType.BETWEEN)
	@JsonFormat(pattern = ConstDate.DATETIME)
	@DateTimeFormat(pattern = ConstDate.DATETIME)
	private LocalDateTime responseTime;

	@Schema(description = "耗时(毫秒)")
	@Query(type = QueryType.BETWEEN)
	private Long costTime;

	@Schema(description = "是否成功:0->失败;1->成功")
	private Integer success;

	@Schema(description = "错误信息")
	private String errorMsg;

	@Schema(description = "用户ID")
	private Long userId;

	@Schema(description = "用户名")
	private String username;

	@Schema(description = "用户昵称")
	private String nickName;
}