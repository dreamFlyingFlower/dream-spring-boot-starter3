package dream.flying.flower.autoconfigure.logger.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import dream.flying.flower.autoconfigure.logger.entity.OperationLogEntity;
import dream.flying.flower.autoconfigure.logger.properties.DreamLoggerProperties;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import dream.flying.flower.framework.core.json.JsonHelpers;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 日志处理
 *
 * @author 飞花梦影
 * @date 2025-03-26 21:56:44
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface LoggerHandler {

	/**
	 * 是否进行日志记录
	 * 
	 * @param point 切入点
	 * @param applicationContext spring上下文
	 * @param dreamLoggerProperties 自定义配置
	 * @return true->记录日志;false->不记录日志
	 */
	default boolean shouldLog(ProceedingJoinPoint point, ApplicationContext applicationContext,
			DreamLoggerProperties dreamLoggerProperties) {
		List<String> scanPackages = dreamLoggerProperties.getScanPackages();
		if (CollectionUtils.isEmpty(scanPackages)) {
			scanPackages = AutoConfigurationPackages.get(applicationContext);
		}
		String packageName = point.getTarget().getClass().getPackage().getName();
		return scanPackages.stream().anyMatch(packageName::startsWith);
	}

	/**
	 * 保存日志
	 * 
	 * @param operationLogService 日志服务
	 * @param operationLogEntity 日志对象
	 */
	default void saveLog(OperationLogService operationLogService, OperationLogEntity operationLogEntity) {
		operationLogService.save(operationLogEntity);
	}

	/**
	 * 获取参数名和参数值,WebHelpers里有相同方法,看看各种方法的优劣
	 *
	 * @param point 切入点
	 * @return 参数
	 */
	default String extractParams(ProceedingJoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (ObjectUtils.isEmpty(args)) {
			return null;
		}
		Map<String, Object> params = new HashMap<>();
		String[] names = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
		for (int i = 0; i < names.length; i++) {
			if (filterParamName(names[i])) {
				continue;
			}
			if (filterParamValue(args[i])) {
				continue;
			}
			params.put(names[i], args[i]);
		}
		return JsonHelpers.toString(params);
	}

	/**
	 * 需要忽略敏感属性
	 * 
	 * @return 属性名列表
	 */
	List<String> excludeProperties();

	/**
	 * 是否拦截参数名
	 * 
	 * @param paramName 参数名
	 * @return true->拦截;false->不拦截
	 */
	default boolean filterParamName(String paramName) {
		if (CollectionUtils.isNotEmpty(excludeProperties())) {
			return excludeProperties().contains(paramName);
		}
		return false;
	}

	/**
	 * 是否拦截参数值
	 * 
	 * @param paramValue 参数值
	 * @return true->拦截;false->不拦截
	 */
	default boolean filterParamValue(Object paramValue) {
		if (paramValue instanceof MultipartFile || paramValue instanceof HttpServletRequest
				|| paramValue instanceof HttpServletResponse || paramValue instanceof BindingResult) {
			return true;
		}

		Class<?> clazz = paramValue.getClass();
		if (clazz.isArray()) {
			return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
		} else if (Collection.class.isAssignableFrom(clazz)) {
			Collection<?> collection = (Collection<?>) paramValue;
			for (Object value : collection) {
				return value instanceof MultipartFile;
			}
		} else if (Map.class.isAssignableFrom(clazz)) {
			Map<?, ?> map = (Map<?, ?>) paramValue;
			for (Object value : map.entrySet()) {
				Map.Entry<?, ?> entry = (Map.Entry<?, ?>) value;
				return entry.getValue() instanceof MultipartFile;
			}
		}
		return false;
	}

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
}