package dream.flying.flower.autoconfigure.logger.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;

import dream.flying.flower.autoconfigure.logger.support.LogAspectHandler;
import lombok.RequiredArgsConstructor;

/**
 * 控制层日志切面类,拦截带有RestController和Controller注解的方法,记录方法的调用信息,包括请求参数、响应结果、执行时间等
 *
 * @author 飞花梦影
 * @date 2025-03-24 11:10:48
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Aspect
@RequiredArgsConstructor
public class ControllerLogAspect {

	private final LogAspectHandler logAspectHandler;

	/**
	 * 切面.注意,若子类标注了注解,但是实际调用的是父类方法,父类未标注注解,无法拦截
	 */
	@Pointcut("@within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Controller)")
	public void controllerPointcut() {
	}

	@Around("controllerPointcut()")
	public Object logAspect(ProceedingJoinPoint point) throws Throwable {
		MDC.put(LogAspectHandler.MDC_TRACT_ID_KEY, LogAspectHandler.getTraceId());
		return logAspectHandler.doLogAspect(point, null);
	}
}