package dream.flying.flower.autoconfigure.logger.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

import dream.flying.flower.autoconfigure.logger.properties.DreamLoggerProperties;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import dream.flying.flower.autoconfigure.logger.support.AbstractAspectLoggerHandler;

/**
 * 控制层日志切面类,拦截带有RestController和Controller注解的方法,记录方法的调用信息,包括请求参数、响应结果、执行时间等
 *
 * @author 飞花梦影
 * @date 2025-03-24 11:10:48
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Aspect
@EnableConfigurationProperties(DreamLoggerProperties.class)
public class ControllerLogAspect extends AbstractAspectLoggerHandler {

	public ControllerLogAspect(ApplicationContext applicationContext, OperationLogService operationLogService,
			DreamLoggerProperties dreamLoggerProperties) {
		super(applicationContext, operationLogService, dreamLoggerProperties);
	}

	@Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Controller *)")
	public void controllerPointcut() {
	}

	@Around("controllerPointcut()")
	public Object doLogger(ProceedingJoinPoint point) throws Throwable {
		return super.doLogger(point, null);
	}
}