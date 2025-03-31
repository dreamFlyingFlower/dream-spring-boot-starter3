package dream.flying.flower.autoconfigure.logger.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import dream.flying.flower.framework.mybatis.plus.entity.AbstractTenantEntity;
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
public class OperationLogEntity extends AbstractTenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 链路追踪ID
	 */
	private String traceId;

	/**
	 * 应用名称
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String appName;

	/**
	 * 模块名称
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String moduleName;

	/**
	 * 操作类型
	 */
	private String operationType;

	/**
	 * 操作概述
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String summary;

	/**
	 * 类名
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String className;

	/**
	 * 方法名
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String methodName;

	/**
	 * 请求IP地址
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String requestIp;

	/**
	 * 请求URL
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String requestUrl;

	/**
	 * 请求方式
	 */
	private String requestMethod;

	/**
	 * 请求参数
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String requestParam;

	/**
	 * 请求体
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String requestBody;

	/**
	 * 请求头
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String requestHeader;

	/**
	 * 请求时间
	 */
	private LocalDateTime requestTime;

	/**
	 * 响应状态码
	 */
	@TableField(condition = SqlCondition.LIKE)
	private Integer responseStatus;

	/**
	 * 响应内容
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String responseBody;

	/**
	 * 响应头
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String responseHeader;

	/**
	 * 响应时间
	 */
	private LocalDateTime responseTime;

	/**
	 * 耗时(毫秒)
	 */
	private Long costTime;

	/**
	 * 是否成功:0->失败;1->成功
	 */
	private Integer success;

	/**
	 * 错误信息
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String errorMsg;

	/**
	 * 用户ID
	 */
	private Long userId;

	/**
	 * 用户名
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String username;

	/**
	 * 用户昵称
	 */
	@TableField(condition = SqlCondition.LIKE)
	private String nickName;
}