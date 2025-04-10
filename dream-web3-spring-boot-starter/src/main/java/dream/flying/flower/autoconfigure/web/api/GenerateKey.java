package dream.flying.flower.autoconfigure.web.api;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import dream.flying.flower.digest.RsaHelper;
import dream.flying.flower.lang.StrHelper;

/**
 * 生成各种类型公私钥
 *
 * @author 飞花梦影
 * @date 2024-07-06 19:06:24
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
public class GenerateKey {

	/**
	 * 提供给前端获取RSA公钥
	 * 
	 * @return ServerResponse
	 */
	@Bean
	RouterFunction<ServerResponse> generateRsaKey(ServerProperties serverProperties) {
		return RouterFunctions.route()
				.GET(StrHelper.getDefault(serverProperties.getServlet().getContextPath()) + "/generateKey/rsa",
						request -> {
							return ServerResponse.ok().bodyValue(RsaHelper.generateKey());
						})
				.build();
	}
}