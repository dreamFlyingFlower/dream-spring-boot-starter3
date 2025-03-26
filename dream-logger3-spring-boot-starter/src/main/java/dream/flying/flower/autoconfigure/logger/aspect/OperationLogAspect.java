package dream.flying.flower.autoconfigure.logger.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

import dream.flying.flower.autoconfigure.logger.properties.DreamLoggerProperties;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import dream.flying.flower.autoconfigure.logger.support.AbstractAspectLoggerHandler;
import dream.flying.flower.logger.Logger;

/**
 * 操作日志切面类.拦截带有Logger注解的方法,录方法的调用信息,包括请求参数、响应结果、执行时间等
 *
 * @author 飞花梦影
 * @date 2025-03-18 22:40:20
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Aspect
@EnableConfigurationProperties(DreamLoggerProperties.class)
public class OperationLogAspect extends AbstractAspectLoggerHandler {

	public OperationLogAspect(ApplicationContext applicationContext, OperationLogService operationLogService,
			DreamLoggerProperties dreamLoggerProperties) {
		super(applicationContext, operationLogService, dreamLoggerProperties);
	}

	@Override
	@Around("@annotation(logger)")
	public Object doLogger(ProceedingJoinPoint point, Logger logger) throws Throwable {
		return super.doLogger(point, logger);
	}

	@Override
	public boolean shouldLog(ProceedingJoinPoint point, ApplicationContext applicationContext,
			DreamLoggerProperties dreamLoggerProperties) {
		return true;
	}
}