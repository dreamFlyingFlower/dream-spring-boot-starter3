package dream.flying.flower.autoconfigure.logger.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import dream.flying.flower.framework.mybatis.plus.entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 操作日志实体类,用于存储系统操作日志信息的数据库实体
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
@TableName("sys_operation_log")
public class OperationLogEntity extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	private String traceId;

	@TableField(condition = SqlCondition.LIKE)
	private String appName;

	private String module;

	private String operationType;

	@TableField(condition = SqlCondition.LIKE)
	private String operationDesc;

	@TableField(condition = SqlCondition.LIKE)
	private String methodName;

	@TableField(condition = SqlCondition.LIKE)
	private String className;

	@TableField(condition = SqlCondition.LIKE)
	private String clientIp;

	@TableField(condition = SqlCondition.LIKE)
	private String requestUrl;

	private String requestMethod;

	@TableField(condition = SqlCondition.LIKE)
	private String requestParams;

	@TableField(condition = SqlCondition.LIKE)
	private String requestBody;

	@TableField(condition = SqlCondition.LIKE)
	private String requestHeaders;

	private LocalDateTime requestTime;

	@TableField(condition = SqlCondition.LIKE)
	private Integer responseStatus;

	@TableField(condition = SqlCondition.LIKE)
	private String responseBody;

	@TableField(condition = SqlCondition.LIKE)
	private String responseHeaders;

	private LocalDateTime responseTime;

	private Long costTime;

	private Integer success;

	@TableField(condition = SqlCondition.LIKE)
	private String errorMsg;

	private Long userId;

	@TableField(condition = SqlCondition.LIKE)
	private String username;
}