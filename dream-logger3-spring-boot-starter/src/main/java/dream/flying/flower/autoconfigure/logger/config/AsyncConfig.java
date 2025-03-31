package dream.flying.flower.autoconfigure.logger.config;

import java.util.concurrent.Executor;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import dream.flying.flower.autoconfigure.logger.properties.DreamLogProperties;
import dream.flying.flower.framework.web.helper.WebHelpers;
import lombok.RequiredArgsConstructor;

/**
 * 异步配置类 配置异步线程池,用于异步记录操作日志
 *
 * @author 飞花梦影
 * @date 2025-03-18 22:40:38
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableAsync
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(DreamLogProperties.class)
public class AsyncConfig {

	private final DreamLogProperties dreamLogProperties;

	@Bean("operationLogExecutor")
	Executor operationLogExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(dreamLogProperties.getAsyncCorePoolSize());
		executor.setMaxPoolSize(dreamLogProperties.getAsyncMaxPoolSize());
		executor.setQueueCapacity(dreamLogProperties.getAsyncQueueCapacity());
		executor.setThreadNamePrefix("operation-log-");
		executor.setTaskDecorator(runnable -> {
			ServletRequestAttributes servletRequestAttributes = WebHelpers.getRequestAttributes();
			return () -> {
				RequestContextHolder.setRequestAttributes(servletRequestAttributes);
				try {
					runnable.run();
				} finally {
					RequestContextHolder.resetRequestAttributes();
				}
			};
		});

		executor.initialize();
		return executor;
	}
}