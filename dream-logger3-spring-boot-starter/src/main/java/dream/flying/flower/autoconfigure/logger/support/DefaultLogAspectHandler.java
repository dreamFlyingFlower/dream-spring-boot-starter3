package dream.flying.flower.autoconfigure.logger.support;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.CollectionUtils;

import dream.flying.flower.autoconfigure.logger.entity.OperationLogEntity;
import dream.flying.flower.autoconfigure.logger.properties.DreamLogProperties;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import dream.flying.flower.enums.YesNoEnum;
import dream.flying.flower.framework.core.helper.IpHelpers;
import dream.flying.flower.framework.json.JsonHelpers;
import dream.flying.flower.framework.web.WebHelpers;
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
public class DefaultLogAspectHandler implements LogAspectHandler {

	protected ApplicationContext applicationContext;

	protected final OperationLogService operationLogService;

	protected final DreamLogProperties dreamLogProperties;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public Object doLogAspect(ProceedingJoinPoint point, Logger logger) throws Throwable {
		if (!shouldLog(point)) {
			return point.proceed();
		}

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

			// 下面要用异步,拿到请求和响应有点麻烦,直接在此处拿到主线程的请求和响应进行处理
			OperationLogEntity operationLogEntity =
					createLog(point, success, errorMsg, requestTime, LocalDateTime.now());

			if (null == logger) {
				applicationContext.getBean(LogAspectHandler.class)
						.saveController(point, operationLogEntity, WebHelpers.getParameterMap(), result);
			} else {
				applicationContext.getBean(LogAspectHandler.class)
						.saveLogger(point, logger, operationLogEntity, WebHelpers.getParameterMap(), result);
			}

			MDC.clear();
		}
	}

	@Override
	public boolean shouldLog(ProceedingJoinPoint point) {
		List<String> scanPackages = dreamLogProperties.getScanPackages();
		if (CollectionUtils.isEmpty(scanPackages)) {
			scanPackages = AutoConfigurationPackages.get(applicationContext);
		}
		String packageName = point.getTarget().getClass().getPackage().getName();
		return scanPackages.stream().anyMatch(packageName::startsWith);
	}

	@Override
	public OperationLogEntity createLog(ProceedingJoinPoint point, boolean success, String errorMsg,
			LocalDateTime requestTime, LocalDateTime responseTime) {
		HttpServletRequest httpServletRequest = WebHelpers.getRequest();
		HttpServletResponse httpServletResponse = WebHelpers.getResponse();
		MethodSignature signature = (MethodSignature) point.getSignature();

		OperationLogEntity operationLogEntity = OperationLogEntity.builder()
				.traceId(MDC.get(MDC_TRACT_ID_KEY))

				.appName(dreamLogProperties.getAppName())

				.className(point.getTarget().getClass().getName())
				.methodName(signature.getMethod().getName())

				.requestIp(IpHelpers.getIp(httpServletRequest))
				.requestUrl(httpServletRequest.getRequestURI())
				.requestMethod(httpServletRequest.getMethod())
				.requestHeader(JsonHelpers.toString(WebHelpers.getHeaders(httpServletRequest)))
				.requestTime(requestTime)

				.responseStatus(httpServletResponse.getStatus())
				.responseHeader(JsonHelpers.toString(WebHelpers.getHeaders(httpServletResponse)))
				.responseTime(responseTime)

				.success(success ? 1 : 0)
				.errorMsg(errorMsg)
				.costTime(Duration.between(requestTime, responseTime).toMillis())
				.userId(getCurrentUserId())
				.username(getCurrentUsername())
				.createdAt(new Date())
				.build();

		return operationLogEntity;
	}

	@Override
	@Async("operationLogExecutor")
	public void saveController(ProceedingJoinPoint point, OperationLogEntity operationLogEntity,
			Map<String, String[]> params, Object result) {
		String summary = null;
		MethodSignature methodSignature = (MethodSignature) point.getSignature();
		Method method = methodSignature.getMethod();
		if (method.isAnnotationPresent(Operation.class)) {
			Operation apiOperation = method.getAnnotation(Operation.class);
			summary = StringUtils.defaultIfBlank(apiOperation.description(), apiOperation.summary());
		}

		operationLogEntity.setModuleName(point.getTarget().getClass().getSimpleName());
		operationLogEntity.setOperationType(BusinessType.getByMsg(operationLogEntity.getMethodName()).name());
		operationLogEntity.setSummary(summary);

		completeLog(point, operationLogEntity, params, result, true, true);

		try {
			saveLog(operationLogService, operationLogEntity);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Failed to save operation log:{}", e.getMessage());
		}
	}

	@Override
	@Async("operationLogExecutor")
	public void saveLogger(ProceedingJoinPoint point, Logger logger, OperationLogEntity operationLogEntity,
			Map<String, String[]> params, Object result) {
		operationLogEntity.setModuleName(logger.value());
		operationLogEntity.setOperationType(logger.businessType().name());
		operationLogEntity.setSummary(logger.description());

		completeLog(point, operationLogEntity, params, result, logger.saveRequest(), logger.saveResponse());

		try {
			saveLog(operationLogService, operationLogEntity);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Failed to save operation log:{}", e.getMessage());
		}
	}

	@Override
	public void completeLog(ProceedingJoinPoint point, OperationLogEntity operationLogEntity,
			Map<String, String[]> params, Object result, boolean saveRequest, boolean saveResponse) {
		MethodSignature signature = (MethodSignature) point.getSignature();

		operationLogEntity.setModuleName(
				StringUtils.defaultIfBlank(operationLogEntity.getModuleName(), point.getTarget().getClass().getName()));
		operationLogEntity.setOperationType(
				StringUtils.defaultIfBlank(operationLogEntity.getOperationType(), signature.getMethod().getName()));
		operationLogEntity.setSummary(
				StringUtils.defaultIfBlank(operationLogEntity.getSummary(), signature.getMethod().getName()));

		if (saveRequest) {
			operationLogEntity.setRequestParam(JsonHelpers.toString(params));
			operationLogEntity.setRequestBody(extractBody(point, signature.getMethod()));
		}

		if (YesNoEnum.isYes(operationLogEntity.getSuccess())) {
			operationLogEntity.setResponseBody(saveResponse ? JsonHelpers.toString(result) : null);
		} else {
			operationLogEntity.setResponseBody(saveResponse ? operationLogEntity.getErrorMsg() : null);
		}
	}

	@Override
	public List<Class<?>> excludeParamTypes() {
		return dreamLogProperties.getExcludeParamTypes();
	}

	@Override
	public List<String> excludeParamNames() {
		return dreamLogProperties.getExcludeParamNames();
	}
}