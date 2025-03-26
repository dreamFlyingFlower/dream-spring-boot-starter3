package dream.flying.flower.autoconfigure.logger.support;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;

import dream.flying.flower.autoconfigure.logger.entity.OperationLogEntity;
import dream.flying.flower.autoconfigure.logger.properties.DreamLoggerProperties;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import dream.flying.flower.framework.core.helper.IpHelpers;
import dream.flying.flower.framework.core.json.JsonHelpers;
import dream.flying.flower.framework.web.helper.WebHelpers;
import dream.flying.flower.lang.StrHelper;
import dream.flying.flower.logger.BusinessType;
import dream.flying.flower.logger.Logger;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 日志处理
 *
 * @author 飞花梦影
 * @date 2025-03-26 21:56:44
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractAspectLoggerHandler implements LoggerHandler {

	/** 排除敏感属性字段 */
	public static final List<String> EXCLUDE_PROPERTIES =
			new ArrayList<>(Arrays.asList("password", "oldPassword", "newPassword", "confirmPassword"));

	protected final ApplicationContext applicationContext;

	protected final OperationLogService operationLogService;

	protected final DreamLoggerProperties dreamLoggerProperties;

	protected Object doLogger(ProceedingJoinPoint point, Logger logger) throws Throwable {
		LocalDateTime requestTime = LocalDateTime.now();
		Object result = null;
		boolean success = false;
		String errorMsg = null;

		try {
			result = point.proceed();
			success = true;
			return result;
		} catch (Exception e) {
			errorMsg = e.getMessage();
			throw e;
		} finally {
			if (shouldLog(point, applicationContext, dreamLoggerProperties)) {
				if (null == logger) {
					saveLogger(point, logger, dreamLoggerProperties.getAppName(), result, success, errorMsg,
							requestTime, LocalDateTime.now());
				} else {
					saveController(point, dreamLoggerProperties.getAppName(), result, success, errorMsg, requestTime,
							LocalDateTime.now());
				}
			}
		}
	}

	@Async("operationLogExecutor")
	protected void saveLogger(ProceedingJoinPoint point, Logger logger, String appName, Object result, boolean success,
			String errorMsg, LocalDateTime requestTime, LocalDateTime responseTime) {
		OperationLogEntity operationLogEntity = buildLogger(point, appName, logger.value(),
				logger.businessType().name(), logger.description(), logger.saveRequest(), logger.saveResponse(), result,
				success, errorMsg, requestTime, responseTime);

		try {
			saveLog(operationLogService, operationLogEntity);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Failed to save operation log:{}", e.getMessage());
		}
	}

	@Async("operationLogExecutor")
	protected void saveController(ProceedingJoinPoint point, String appName, Object result, boolean success,
			String errorMsg, LocalDateTime requestTime, LocalDateTime responseTime) {
		String operationDesc = null;
		MethodSignature methodSignature = (MethodSignature) point.getSignature();
		Method method = methodSignature.getMethod();
		if (method.isAnnotationPresent(Operation.class)) {
			Operation apiOperation = method.getAnnotation(Operation.class);
			operationDesc = StrHelper.getDefault(apiOperation.description(), apiOperation.summary());
		}

		String operationType = null;
		String methodName = method.getName();
		if (methodName.startsWith("add") || methodName.startsWith("insert") || methodName.startsWith("save")
				|| methodName.startsWith("create")) {
			operationType = BusinessType.INSERT.name();
		} else if (methodName.startsWith("delete") || methodName.startsWith("remove")) {
			operationType = BusinessType.DELETE.name();
		} else if (methodName.startsWith("update") || methodName.startsWith("edit")
				|| methodName.startsWith("modify")) {
			operationType = BusinessType.UPDATE.name();
		} else if (methodName.startsWith("query") || methodName.startsWith("get") || methodName.startsWith("list")
				|| methodName.startsWith("select")) {
			operationType = BusinessType.SELECT.name();
		} else if (methodName.startsWith("grant")) {
			operationType = BusinessType.GRANT.name();
		} else if (methodName.startsWith("export")) {
			operationType = BusinessType.EXPORT.name();
		} else if (methodName.startsWith("import")) {
			operationType = BusinessType.IMPORT.name();
		} else {
			operationType = BusinessType.OTHER.name();
		}

		OperationLogEntity operationLogEntity = buildLogger(point, appName, methodName, operationType, operationDesc,
				true, true, result, success, errorMsg, requestTime, responseTime);

		try {
			saveLog(operationLogService, operationLogEntity);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Failed to save operation log:{}", e.getMessage());
		}
	}

	protected OperationLogEntity buildLogger(ProceedingJoinPoint point, String appName, String moduleName,
			String operationType, String operationDesc, boolean saveRequest, boolean saveResponse, Object result,
			boolean success, String errorMsg, LocalDateTime requestTime, LocalDateTime responseTime) {
		HttpServletRequest request = WebHelpers.getRequest();
		HttpServletResponse response = WebHelpers.getResponse();
		MethodSignature signature = (MethodSignature) point.getSignature();

		OperationLogEntity operationLogEntity = OperationLogEntity.builder()
				.traceId(UUID.randomUUID().toString())

				.appName(appName)
				.module(StrHelper.getDefault(moduleName, point.getTarget().getClass().getName()))
				.operationType(StrHelper.getDefault(operationType, signature.getMethod().getName()))
				.operationDesc(StrHelper.getDefault(operationDesc, signature.getMethod().getName()))

				.methodName(signature.getMethod().getName())
				.className(point.getTarget().getClass().getName())

				.clientIp(IpHelpers.getIp(request))
				.requestBody(saveRequest ? JsonHelpers.toString(request.getParameterMap()) : null)
				.requestHeaders(JsonHelpers.toString(WebHelpers.getHeaders(request)))
				.requestMethod(request.getMethod())
				.requestParams(saveRequest ? extractParams(point) : null)
				.requestTime(requestTime)
				.requestUrl(request.getRequestURI())

				.responseHeaders(JsonHelpers.toString(WebHelpers.getHeaders(response)))
				.responseStatus(response.getStatus())
				.responseTime(responseTime)

				.success(success ? 1 : 0)
				.errorMsg(errorMsg)
				.costTime(Duration.between(requestTime, responseTime).toMillis())
				.userId(getCurrentUserId())
				.username(getCurrentUsername())
				.createTime(new Date())
				.build();

		if (success) {
			operationLogEntity.setResponseBody(saveResponse ? JsonHelpers.toString(result) : null);
		} else {
			operationLogEntity.setResponseBody(errorMsg);
		}

		return operationLogEntity;
	}

	/**
	 * 忽略敏感属性
	 */
	@Override
	public List<String> excludeProperties() {
		return EXCLUDE_PROPERTIES;
	}
}