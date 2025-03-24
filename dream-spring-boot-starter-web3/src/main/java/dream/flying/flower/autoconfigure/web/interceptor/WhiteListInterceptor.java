package dream.flying.flower.autoconfigure.web.interceptor;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.HandlerInterceptor;

import dream.flying.flower.autoconfigure.web.properties.WhiteListProperties;
import dream.flying.flower.autoconfigure.web.whitelist.WhiteListHandler;
import dream.flying.flower.framework.core.helper.IpHelpers;
import dream.flying.flower.framework.web.helper.WebHelpers;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 白名单拦截器
 *
 * @author 飞花梦影
 * @date 2024-12-17 14:05:26
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@AllArgsConstructor
@EnableConfigurationProperties(WhiteListProperties.class)
public class WhiteListInterceptor implements HandlerInterceptor {

	private WhiteListHandler whiteListHandler;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (!whiteListHandler.enabled()) {
			return true;
		}
		String ip = IpHelpers.getIp(request);
		boolean yes = whiteListHandler.whiteList(ip);
		if (yes) {
			return true;
		} else {
			log.error("ip:{}不在白名单中!", ip);
			WebHelpers.writeError(response, "IP不在白名单内");
			return false;
		}
	}
}