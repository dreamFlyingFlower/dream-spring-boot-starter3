package dream.flying.flower.autoconfigure.security.handler;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import dream.flying.flower.framework.web.WebHelpers;
import dream.flying.flower.result.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 登出成功
 *
 * @author 飞花梦影
 * @date 2022-06-17 16:54:35
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Configuration
public class SecurityLogoutHandler extends SimpleUrlLogoutSuccessHandler {

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		WebHelpers.write(response, Result.ok());
	}
}