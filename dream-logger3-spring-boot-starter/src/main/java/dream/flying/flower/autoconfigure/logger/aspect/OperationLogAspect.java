package dream.flying.flower.autoconfigure.logger.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;

import dream.flying.flower.autoconfigure.logger.support.LogAspectHandler;
import dream.flying.flower.logger.Logger;
import lombok.RequiredArgsConstructor;

/**
 * 操作日志切面类.拦截带有Logger注解的方法,录方法的调用信息,包括请求参数、响应结果、执行时间等
 *
 * @author 飞花梦影
 * @date 2025-03-18 22:40:20
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Aspect
@RequiredArgsConstructor
public class OperationLogAspect {

	private final LogAspectHandler loggerAspectHandler;

	@Around("@annotation(logger)")
	public Object loggerAspect(ProceedingJoinPoint point, Logger logger) throws Throwable {
		MDC.put(LogAspectHandler.MDC_TRACT_ID_KEY, LogAspectHandler.getTraceId());
		return loggerAspectHandler.doLogAspect(point, logger);
	}
}