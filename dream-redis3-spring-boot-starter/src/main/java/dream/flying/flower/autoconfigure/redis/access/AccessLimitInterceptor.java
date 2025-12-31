package dream.flying.flower.autoconfigure.redis.access;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import dream.flying.flower.framework.constant.ConstRedis;
import dream.flying.flower.framework.core.helper.IpHelpers;
import dream.flying.flower.framework.web.WebHelpers;
import dream.flying.flower.limit.LimitAccessHandler;
import dream.flying.flower.limit.annotation.LimitAccess;
import dream.flying.flower.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Redis接口限流
 * 
 * FIXME 未完成
 *
 * @author 飞花梦影
 * @date 2021-11-09 17:10:49
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}
		Method method = ((HandlerMethod) handler).getMethod();
		LimitAccess methodAnnotation = method.getAnnotation(LimitAccess.class);
		LimitAccess classAnnotation = method.getDeclaringClass().getAnnotation(LimitAccess.class);
		// 如果方法上有注解就优先选择方法上的参数,否则类上的参数
		LimitAccess limitAccess = methodAnnotation != null ? methodAnnotation : classAnnotation;
		if (Objects.isNull(limitAccess)) {
			return true;
		}

		if (isLimit(request, limitAccess)) {
			WebHelpers.render(response, Result.error("被限流了"));
			return false;
		}

		return true;
	}

	/**
	 * 判断请求是否受限
	 * 
	 * @param request 请求
	 * @param limitAccess 限流注解
	 * @return 是否限流
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private boolean isLimit(HttpServletRequest request, LimitAccess limitAccess)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		if (limitAccess.custom()) {
			LimitAccessHandler limitAccessHandler = new DefaultAccessLimitHandler();
			if (limitAccess.handler() != LimitAccessHandler.class) {
				limitAccessHandler =
						limitAccess.handler().getDeclaredConstructor(new Class<?>[0]).newInstance(new Object[0]);
			}
			return limitAccessHandler.handler(limitAccess);
		}

		String limitKey =
				ConstRedis.buildKey(IpHelpers.getIp(request), request.getContextPath(), request.getServletPath());
		Object redisCount = redisTemplate.opsForValue().get(limitKey);
		if (redisCount == null) {
			redisTemplate.opsForValue().set(limitKey, 1, limitAccess.value(), limitAccess.timeUnit());
		} else {
			if (Integer.parseInt(redisCount.toString()) >= limitAccess.count()) {
				return true;
			}
			redisTemplate.opsForValue().increment(limitKey);
		}
		return false;
	}
}