package dream.flying.flower.autoconfigure.web.handler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import dream.flying.flower.enums.ResponseEnum;
import dream.flying.flower.framework.core.helper.IpHelpers;
import dream.flying.flower.framework.core.json.JsonHelpers;
import dream.flying.flower.framework.web.handler.OperateLogHandler;
import dream.flying.flower.framework.web.helper.WebHelpers;
import dream.flying.flower.framework.web.model.OperateLog;
import dream.flying.flower.lang.StrHelper;
import dream.flying.flower.logger.BusinessType;
import dream.flying.flower.logger.Logger;
import dream.flying.flower.logger.OperatorType;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认异步日志业务实现类
 *
 * @author 飞花梦影
 * @date 2022-11-14 10:29:09
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Slf4j
@Async
@EnableAsync
public class DefaultOperateLogHandler implements OperateLogHandler {

	protected Object doAspectAround(ProceedingJoinPoint joinPoint, Logger logger) {
		// 计时
		StopWatch stopWatch = new StopWatch(Thread.currentThread().getName());
		stopWatch.start();

		OperateLog operateLog = buildOperateLog(joinPoint);

		Object result = doMethod(joinPoint, operateLog, logger);

		handleTail(operateLog, stopWatch);

		return result;
	}

	/**
	 * 获取注解中对方法的描述信息 用于Controller层注解
	 * 
	 * @param joinPoint 切入点
	 * @param operateLog 操作日志
	 * @param result 接口调用结果
	 * @throws Exception
	 */
	protected void handleMethodController(JoinPoint joinPoint, OperateLog operateLog, Object result) throws Exception {
		
		operateLog.setOperateParam(JsonHelpers.toString(getParameter(method, joinPoint.getArgs())));

		// 设置操作人类别
		operateLog.setOperateType(OperatorType.OTHER.ordinal());

		// 获取参数的信息,传入到数据库中
		setRequestValue(joinPoint, operateLog);
		operateLog.setJsonResult(StrHelper.substring(JsonHelpers.toString(result), 0, 2000));
	}

	/**
	 * 根据方法和传入的参数获取请求参数
	 */
	protected Object getParameter(Method method, Object[] args) {
		List<Object> argRets = new ArrayList<>();
		Parameter[] parameters = method.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			// 跳过文件类型参数
			if (MultipartFile.class.isAssignableFrom(parameter.getType())) {
				continue;
			}
			// 将RequestBody注解修饰的参数作为请求参数
			if (parameter.isAnnotationPresent(RequestBody.class)) {
				argRets.add(args[i]);
			}
			// 将RequestParam注解修饰的参数作为请求参数
			if (parameter.isAnnotationPresent(RequestParam.class)) {
				RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
				Map<String, Object> map = new HashMap<>();
				String key = parameters[i].getName();
				if (StrHelper.isNotBlank(requestParam.value())) {
					key = requestParam.value();
				}
				map.put(key, args[i]);
				argRets.add(map);
			}
		}
		if (argRets.size() == 0) {
			return null;
		} else if (argRets.size() == 1) {
			return argRets.get(0);
		} else {
			return argRets;
		}
	}

	/**
	 * 设置相关数据
	 * 
	 * @param operateLog 日志
	 * @param stopWatch 计时
	 */
	protected void handleTail(OperateLog operateLog, StopWatch stopWatch) {
		stopWatch.stop();
		operateLog.setEndTime(new Date());
		operateLog.setOperateTime(stopWatch.getTotalTimeMillis());
		// 保存数据
		saveOperateLog(operateLog);
	}

	@Override
	public void doAfterThrowingLogger(JoinPoint joinPoint, Logger logger, Exception e) {
		doAspectThrowing(joinPoint, logger, e);
	}

	protected void doAspectThrowing(JoinPoint joinPoint, Logger logger, Exception e) {
		// 计时
		StopWatch stopWatch = new StopWatch(Thread.currentThread().getName());
		stopWatch.start();

		OperateLog operateLog = buildOperateLog(joinPoint);
		operateLog.setStatus(ResponseEnum.FAIL.ordinal());
		operateLog.setErrorMsg(StrHelper.substring(e.getMessage(), 0, 2000));

		try {
			// 处理设置注解上的参数
			handleOtherInfo(joinPoint, logger, operateLog, null);
		} catch (Exception ex) {
			// 记录本地异常日志
			log.error("###异常切面异常:{}###", ex.getMessage());
			ex.printStackTrace();
		}

		handleTail(operateLog, stopWatch);
	}

	@Async
	@Override
	public void saveOperateLog(OperateLog operateLog) {
		log.info(JsonHelpers.toString(operateLog));
	}
}