package dream.flying.flower.autoconfigure.logger.support;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
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
import org.springframework.web.multipart.MultipartFile;

import dream.flying.flower.autoconfigure.logger.entity.OperationLogEntity;
import dream.flying.flower.autoconfigure.logger.properties.DreamLogProperties;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import dream.flying.flower.enums.YesNoEnum;
import dream.flying.flower.framework.core.helper.IpHelpers;
import dream.flying.flower.framework.json.JsonHelpers;
import dream.flying.flower.framework.web.WebHelpers;
import dream.flying.flower.helper.DateTimeHelper;
import dream.flying.flower.lang.ObjectHelper;
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
		if (!shouldLog(point, logger)) {
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

			applicationContext.getBean(LogAspectHandler.class)
					.saveLogger(point, logger, operationLogEntity, WebHelpers.getParameterMap(), result);

			MDC.clear();
		}
	}

	@Override
	public boolean shouldLog(ProceedingJoinPoint point, Logger logger) {
		if (null == logger) {
			MethodSignature signature = (MethodSignature) point.getSignature();
			Method method = signature.getMethod();
			logger = method.getAnnotation(Logger.class);
		}
		if (null != logger) {
			return logger.enabled();
		}

		Object[] args = point.getArgs();
		for (Object arg : args) {
			if (arg instanceof MultipartFile || (arg != null && arg.getClass().isArray()
					&& arg.getClass().getComponentType() == MultipartFile.class)) {
				log.info("A file upload request has been detected, skipping detailed logging");
				return false;
			}

			if (arg instanceof List) {
				List<?> list = (List<?>) arg;
				if (!list.isEmpty() && list.get(0) instanceof MultipartFile) {
					log.info("A file list upload request has been detected, skipping detailed logging");
					return false;
				}
			}

			if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) {
				log.info("The HttpServletRequest/HttpServletResponse has been detected, skipping detailed logging");
			}
		}

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
				.createdAt(LocalDateTime.now())
				.build();

		return operationLogEntity;
	}

	@Override
	@Async("operationLogExecutor")
	public void saveLogger(ProceedingJoinPoint point, Logger logger, OperationLogEntity operationLogEntity,
			Map<String, String[]> params, Object result) {

		String summary = handlerSummary(point, logger);
		if (null != logger) {
			operationLogEntity.setModuleName(
					ObjectHelper.defaultIfNull(logger.value(), point.getTarget().getClass().getSimpleName()));
			operationLogEntity.setOperationType(ObjectHelper.defaultIfNull(logger.businessType().name(),
					BusinessType.getByMsg(operationLogEntity.getMethodName()).name()));
			operationLogEntity.setSummary(summary);
		} else {
			operationLogEntity.setModuleName(point.getTarget().getClass().getSimpleName());
			operationLogEntity.setOperationType(BusinessType.getByMsg(operationLogEntity.getMethodName()).name());
			operationLogEntity.setSummary(summary);
		}

		completeLog(point, operationLogEntity, params, result,
				ObjectHelper.defaultIfNull(null != logger ? logger.saveRequest() : null,
						dreamLogProperties.isSaveRequest()),
				ObjectHelper.defaultIfNull(null != logger ? logger.saveResponse() : null,
						dreamLogProperties.isSaveResponse()));

		Boolean outputFile = ObjectHelper.defaultIfNull(null != logger ? logger.outputFile() : null,
				dreamLogProperties.isOutputFile());
		Boolean storeDb =
				ObjectHelper.defaultIfNull(null != logger ? logger.storeDb() : null, dreamLogProperties.isStoreDb());

		try {
			if (outputFile) {
				outputFile(operationLogEntity);
			}

			if (storeDb) {
				saveLog(operationLogService, operationLogEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Failed to save operation log:{}", e.getMessage());
		}
	}

	private String handlerSummary(ProceedingJoinPoint point, Logger logger) {
		String summary = null != logger && StrHelper.isNotBlank(logger.description()) ? logger.description() : null;
		if (StrHelper.isNotBlank(summary)) {
			return summary;
		}
		MethodSignature methodSignature = (MethodSignature) point.getSignature();
		Method method = methodSignature.getMethod();
		if (method.isAnnotationPresent(Operation.class)) {
			Operation apiOperation = method.getAnnotation(Operation.class);
			summary = StringUtils.defaultIfBlank(apiOperation.description(), apiOperation.summary());
		}
		return summary;
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
			operationLogEntity.setResponseBody(
					saveResponse && !filterResponseType(result.getClass()) ? JsonHelpers.toString(result) : null);
		} else {
			operationLogEntity.setResponseBody(saveResponse ? operationLogEntity.getErrorMsg() : null);
		}
	}

	protected void outputFile(OperationLogEntity operationLogEntity) {
		log.info("==================== 请求日志开始 ====================");
		log.info("请求开始时间: {}", DateTimeHelper.formatDateTime(operationLogEntity.getRequestTime()));
		log.info("请求日志TraceId: {}", operationLogEntity.getTraceId());
		log.info("请求用户ID: {}", operationLogEntity.getUserId());
		log.info("请求方式: {}", operationLogEntity.getRequestMethod());
		log.info("请求URL: {}", operationLogEntity.getRequestUrl());
		log.info("请求头JSON: {} ", operationLogEntity.getRequestHeader());
		log.info("请求方法:  {}.{}", operationLogEntity.getClassName(), operationLogEntity.getMethodName());
		log.info("请求体RequestBody : {} ", operationLogEntity.getRequestBody());
		log.info("请求参数RequestParam: {}", operationLogEntity.getRequestParam());
		log.info("请求 {} , 结果: {}, 失败信息: {}", YesNoEnum.isYes(operationLogEntity.getSuccess()) ? "成功" : "失败",
				operationLogEntity.getResponseBody(), operationLogEntity.getErrorMsg());
		log.info("请求结束时间: {}", DateTimeHelper.formatDateTime(operationLogEntity.getResponseTime()));
		log.info("请求耗时(毫秒):{}", operationLogEntity.getCostTime());
		log.info("==================== 请求日志结束 ====================");
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