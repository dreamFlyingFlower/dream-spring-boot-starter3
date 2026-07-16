package dream.flying.flower.autoconfigure.logger.support;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import dream.flying.flower.autoconfigure.logger.entity.OperationLogEntity;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import dream.flying.flower.digest.DigestHelper;
import dream.flying.flower.framework.json.JsonHelpers;
import dream.flying.flower.lang.StrHelper;
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
	List<String> EXCLUDE_PARAM_NAMES = new ArrayList<>(Arrays.asList("pwd", "password"));

	/** 排除不需要记录的参数类型 */
	List<Class<?>> EXCLUDE_PARAM_TYPES = new ArrayList<>(Arrays.asList(MultipartFile.class, HttpServletRequest.class,
			HttpServletResponse.class, BindingResult.class));

	/** 排除不需要记录的响应类型 */
	List<Class<?>> EXCLUDE_RESPONSE_TYPES =
			new ArrayList<>(Arrays.asList(Byte.class, byte.class, HttpServletRequest.class, HttpServletResponse.class,
					BindingResult.class, InputStream.class, Resource.class));

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
	 * @param logger 日志注解,可能为null
	 * @return true->记录日志;false->不记录日志
	 */
	default boolean shouldLog(ProceedingJoinPoint point, Logger logger) {
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
		Map<String, Object> params = new LinkedHashMap<>();
		Parameter[] parameters = method.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			// 处理相关类型
			if (filterParamType(parameter.getType())) {
				continue;
			}

			String paramName = handlerParamName(parameter);

			Object arg = args[i];
			// 处理数据
			arg = handlerData(paramName, arg);

			params.put(paramName, arg);
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
				.anyMatch(t -> paramType.isArray()
						? t.isAssignableFrom(paramType.getComponentType()) : t.isAssignableFrom(paramType));
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
	 * 获取形参名
	 * 
	 * @param parameter 参数
	 * @return 是否忽略:true->忽略;false->不忽略
	 */
	default String handlerParamName(Parameter parameter) {
		// 默认使用参数名,如果编译时保留了参数名
		String paramName = parameter.getName();
		// 检查 @RequestParam
		RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
		if (null != requestParam && StrHelper.isNotBlank(requestParam.value())) {
			paramName = requestParam.value();
		}

		PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
		if (null != pathVariable && StrHelper.isNotBlank(pathVariable.value())) {
			paramName = pathVariable.value();
		}

		return paramName.toLowerCase();
	}

	/**
	 * 处理数据,主要是敏感字段
	 * 
	 * @param paramName
	 * @param arg
	 * @return
	 */
	default Object handlerData(String paramName, Object arg) {
		if (arg == null) {
			return null;
		}

		boolean match = Optional.ofNullable(excludeParamNames())
				.orElse(Collections.emptyList())
				.stream()
				.anyMatch(t -> paramName.contains(t.toLowerCase()));

		if (match) {
			return handlerSentitiveData(paramName, arg);
		}

		// 复杂对象交给序列化处理,序列化时ObjectMapper会递归处理
		return arg;
	}

	/**
	 * 处理敏感字段
	 * 
	 * @param paramName 字段名
	 * @param arg 数据
	 * @return 处理后数据
	 */
	default Object handlerSentitiveData(String paramName, Object arg) {
		// 如果是字符串,检查是否是敏感字段
		if (arg instanceof String) {
			String str = (String) arg;
			// 对密码、手机号等进行脱敏
			if (str.length() > 4 && (paramName.contains("password") || paramName.contains("pwd"))) {
				return "******";
			}
			if (str.matches("\\d{11}") && (paramName.contains("phone") || paramName.contains("mobile"))) {
				return str.substring(0, 3) + "****" + str.substring(7);
			}
		}

		return arg;
	}

	/**
	 * 需要忽略的响应类型
	 * 
	 * @return 响应类型字节码列表
	 */
	default List<Class<?>> excludeResponseTypes() {
		return EXCLUDE_RESPONSE_TYPES;
	}

	/**
	 * 忽略响应类型
	 * 
	 * @param responseType 响应字节码
	 * @return 是否忽略:true->忽略;false->不忽略
	 */
	default boolean filterResponseType(Class<?> responseType) {
		return Optional.ofNullable(excludeResponseTypes())
				.orElse(Collections.emptyList())
				.stream()
				.anyMatch(t -> responseType.isArray()
						? t.isAssignableFrom(responseType.getComponentType()) : t.isAssignableFrom(responseType));
	}
}