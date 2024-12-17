package dream.flying.flower.autoconfigure.web.interceptor;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.HandlerInterceptor;

import dream.flying.flower.autoconfigure.web.properties.WhiteListProperties;
import dream.flying.flower.framework.core.json.JsonHelpers;
import dream.flying.flower.framework.web.helper.IpHelpers;
import dream.flying.flower.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

/**
 * 白名单拦截器
 *
 * @author 飞花梦影
 * @date 2024-12-17 14:05:26
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@AllArgsConstructor
@EnableConfigurationProperties(WhiteListProperties.class)
public class WhiteListInterceptor implements HandlerInterceptor {

	private WhiteListProvider whiteListProvider;

	private WhiteListProperties whiteListProperties;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (!whiteListProperties.getEnabled()) {
			return true;
		}
		String ipAddr = IpHelpers.getIp(request);
		boolean whiteListByIp = whiteListProvider.getWhiteListByIp(ipAddr);
		if (whiteListByIp) {
			return true;
		} else {
			result(response, JsonHelpers.toString(Result.build("IP不在白名单内", ResultCodeEnum.ERROR)));
			return false;
		}
	}

	private void result(HttpServletResponse response, String result) {
		PrintWriter writer = null;
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=utf-8");
		try {
			writer = response.getWriter();
			writer.println(result);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}