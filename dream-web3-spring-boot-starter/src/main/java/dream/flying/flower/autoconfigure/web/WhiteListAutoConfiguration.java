package dream.flying.flower.autoconfigure.web;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import dream.flying.flower.autoconfigure.web.interceptor.WhiteListInterceptor;
import dream.flying.flower.autoconfigure.web.properties.WhiteListProperties;
import dream.flying.flower.autoconfigure.web.whitelist.MemoryWhiteListHandler;
import dream.flying.flower.autoconfigure.web.whitelist.WhiteListHandler;
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
public class WhiteListAutoConfiguration implements WebMvcConfigurer, ApplicationContextAware {

	private WhiteListProperties whiteListProperties;

	private ApplicationContext applicationContext;

	public WhiteListAutoConfiguration(WhiteListProperties whiteListProperties) {
		this.whiteListProperties = whiteListProperties;
	}

	@Bean
	WhiteListInterceptor whiteListInterceptor() {
		Map<String, WhiteListHandler> mapWhiteListHandler = applicationContext.getBeansOfType(WhiteListHandler.class);
		if (CollectionUtils.isEmpty(mapWhiteListHandler)) {
			return new WhiteListInterceptor(new MemoryWhiteListHandler(whiteListProperties));
		}
		Entry<String, WhiteListHandler> entry = mapWhiteListHandler.entrySet().iterator().next();
		return new WhiteListInterceptor(entry.getValue());
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(whiteListInterceptor());
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}