package dream.flying.flower.autoconfigure.redis.access;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import dream.flying.flower.autoconfigure.redis.helper.RedisHelpers;
import dream.flying.flower.framework.constant.ConstRedis;
import dream.flying.flower.framework.core.helper.IpHelpers;
import dream.flying.flower.limit.LimitAccessHandler;
import dream.flying.flower.limit.annotation.LimitAccess;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认访问次数限制实现
 *
 * @author 飞花梦影
 * @date 2022-06-23 09:43:09
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Slf4j
public class DefaultAccessLimitHandler implements LimitAccessHandler {

	@Autowired
	private RedisHelpers redisHelpers;

	@Override
	public boolean handler(LimitAccess limitAccess) {
		ServletRequestAttributes servletRequestAttributes =
				(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();
		String key = ConstRedis.buildKey(IpHelpers.getIp(request), request.getContextPath(), request.getServletPath());
		Object count = redisHelpers.get(key);
		if (count == null) {
			redisHelpers.setExpire(key, 1, limitAccess.value(), limitAccess.timeUnit());
		} else {
			if (Integer.parseInt(count.toString()) >= limitAccess.count()) {
				return true;
			}
			redisHelpers.incr(key);
		}

		return false;
	}
}