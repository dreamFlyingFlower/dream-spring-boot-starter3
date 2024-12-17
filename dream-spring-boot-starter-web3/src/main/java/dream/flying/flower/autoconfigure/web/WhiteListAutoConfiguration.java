package dream.flying.flower.autoconfigure.web;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import dream.flying.flower.autoconfigure.web.interceptor.WhiteListInterceptor;
import dream.flying.flower.autoconfigure.web.properties.WhiteListProperties;
import dream.flying.flower.framework.core.constant.ConstConfigPrefix;

/**
 * 白名单自动配置
 * 
 * @author 飞花梦影
 * @date 2022-12-05 17:28:05
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@AutoConfiguration
@ConditionalOnMissingClass
@EnableConfigurationProperties(WhiteListProperties.class)
@ConditionalOnProperty(prefix = ConstConfigPrefix.AUTO_WHITE_LIST, value = ConstConfigPrefix.ENABLED,
		matchIfMissing = true)
public class WhiteListAutoConfiguration implements WebMvcConfigurer {

	private WhiteListProperties whiteListProperties;

	private ObjectMapper objectMapper;

	public WhiteListAutoConfiguration(WhiteListProperties whiteListProperties, ObjectMapper objectMapper) {
		this.whiteListProperties = whiteListProperties;
		this.objectMapper = objectMapper;
	}

	@Bean
	WhiteListInterceptor whiteListInterceptor() {
		return new WhiteListInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(whiteListInterceptor());
	}
}