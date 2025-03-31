package dream.flying.flower.autoconfigure.logger.support;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import dream.flying.flower.autoconfigure.logger.entity.OperationLogEntity;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import dream.flying.flower.digest.DigestHelper;
import dream.flying.flower.framework.json.JsonHelpers;
import dream.flying.flower.logger.Logger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 日志处理
 *
 * @author 飞花梦影
 * @date 2025-03-26 21:56:44
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface LogAspectHandler extends ApplicationContextAware {

	/** MDC链路追踪id */
	String MDC_TRACT_ID_KEY = "mdc_trace_id";

	/** 排除敏感属性字段 */
	List<String> EXCLUDE_PARAM_NAMES =
			new ArrayList<>(Arrays.asList("password", "oldPassword", "newPassword", "confirmPassword"));

	/** 排除不需要记录的参数类型 */
	List<Class<?>> EXCLUDE_PARAM_TYPES = new ArrayList<>(Arrays.asList(MultipartFile.class, HttpServletRequest.class,
			HttpServletResponse.class, BindingResult.class));

	/**
	 * 处理切面
	 * 
	 * @param point 切入点
	 * @param logger 日志注解
	 * @return 方法执行结果
	 * @throws Throwable 异常
	 */
	default Object doLogAspect(ProceedingJoinPoint point, Logger logger) throws Throwable {
		return point.proceed();
	}

	/**
	 * 是否进行日志记录
	 * 
	 * @param point 切入点
	 * @return true->记录日志;false->不记录日志
	 */
	default boolean shouldLog(ProceedingJoinPoint point) {
		return true;
	}

	/**
	 * 构建生成traceId
	 * 
	 * @return traceId
	 */
	static String getTraceId() {
		String traceId = MDC.get(MDC_TRACT_ID_KEY);
		return StringUtils.defaultIfBlank(traceId, DigestHelper.uuid());
	}

	/**
	 * 创建日志对象
	 * 
	 * @param point 切入点
	 * @param success 是否成功
	 * @param errorMsg 错误信息
	 * @param requestTime 请求时间
	 * @param responseTime 响应时间
	 * @return OperationLogEntity
	 */
	OperationLogEntity createLog(ProceedingJoinPoint point, boolean success, String errorMsg, LocalDateTime requestTime,
			LocalDateTime responseTime);

	/**
	 * 获取当前登录用户ID
	 * 
	 * @return 用户ID
	 */
	default Long getCurrentUserId() {
		return 1L;
	}

	/**
	 * 获取当前用户用户名
	 * 
	 * @return 用户名
	 */
	default String getCurrentUsername() {
		return "system";
	}

	/**
	 * 保存控制层日志
	 * 
	 * @param point 切入点
	 * @param operationLogEntity 日志对象
	 * @param params 请求参数
	 * @param result 结果
	 */
	default void saveController(ProceedingJoinPoint point, OperationLogEntity operationLogEntity,
			Map<String, String[]> params, Object result) {
	}

	/**
	 * 保存注解日志
	 * 
	 * @param point 切入点
	 * @param logger 日志注解
	 * @param operationLogEntity 日志对象
	 * @param params 请求参数
	 * @param result 结果
	 */
	default void saveLogger(ProceedingJoinPoint point, Logger logger, OperationLogEntity operationLogEntity,
			Map<String, String[]> params, Object result) {
	}

	/**
	 * 保存日志
	 * 
	 * @param operationLogService 日志业务服务
	 * @param operationLogEntity 日志对象
	 */
	default void saveLog(OperationLogService operationLogService, OperationLogEntity operationLogEntity) {
		operationLogService.save(operationLogEntity);
	}

	/**
	 * 补全日志对象
	 * 
	 * @param point 切入点
	 * @param operationLogEntity 日志对象
	 * @param params 查询参数
	 * @param result 结果
	 * @param saveRequest 是否存储请求
	 * @param saveResponse 是否存储响应
	 */
	default void completeLog(ProceedingJoinPoint point, OperationLogEntity operationLogEntity,
			Map<String, String[]> params, Object result, boolean saveRequest, boolean saveResponse) {
	}

	/**
	 * 获取参数名和参数值,WebHelpers里有相同方法,看看各种方法的优劣
	 *
	 * @param point 切入点
	 * @param method 当前执行方法
	 * @return 参数
	 */
	default String extractBody(ProceedingJoinPoint joinPoint, Method method) {
		Object[] args = joinPoint.getArgs();
		if (ObjectUtils.isEmpty(args)) {
			return null;
		}
		Map<String, Object> params = new HashMap<>();
		Parameter[] parameters = method.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			// 跳过文件类型参数
			if (filterParamType(parameter.getType())) {
				continue;
			}
			// 形参名
			if (filterParamName(parameter.getName())) {
				continue;
			}
			params.put(parameter.getName(), args[i]);
		}
		return JsonHelpers.toString(params);
	}

	/**
	 * 需要忽略的参数类型
	 * 
	 * @return 参数类型字节码列表
	 */
	default List<Class<?>> excludeParamTypes() {
		return EXCLUDE_PARAM_TYPES;
	}

	/**
	 * 是否忽略参数类型
	 * 
	 * @param paramType 参数字节码
	 * @return 是否忽略:true->忽略;false->不忽略
	 */
	default boolean filterParamType(Class<?> paramType) {
		return Optional.ofNullable(excludeParamTypes())
				.orElse(Collections.emptyList())
				.stream()
				.filter(t -> paramType.isArray() ? t.isAssignableFrom(paramType.getComponentType())
						: t.isAssignableFrom(paramType))
				.findFirst()
				.isPresent();
	}

	/**
	 * 需要忽略敏感属性
	 * 
	 * @return 属性名列表
	 */
	default List<String> excludeParamNames() {
		return EXCLUDE_PARAM_NAMES;
	}

	/**
	 * 是否忽略参数名
	 * 
	 * @param paramName 参数名
	 * @return 是否忽略:true->忽略;false->不忽略
	 */
	default boolean filterParamName(String paramName) {
		return Optional.ofNullable(excludeParamNames())
				.orElse(Collections.emptyList())
				.stream()
				.filter(paramName::equalsIgnoreCase)
				.findFirst()
				.isPresent();
	}
}