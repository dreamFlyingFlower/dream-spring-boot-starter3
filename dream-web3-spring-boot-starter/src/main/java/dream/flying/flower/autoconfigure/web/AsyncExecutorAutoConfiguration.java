package dream.flying.flower.autoconfigure.web;

import java.util.concurrent.Executor;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import dream.flying.flower.autoconfigure.web.properties.AsyncExecutorProperties;
import dream.flying.flower.framework.constant.ConstConfig;
import dream.flying.flower.framework.web.WebHelpers;

/**
 * 初始化异步线程池
 *
 * @author 飞花梦影
 * @date 2022-12-21 14:25:23
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@EnableAsync
@AutoConfiguration
@ConditionalOnMissingClass
@EnableConfigurationProperties({ AsyncExecutorProperties.class })
@ConditionalOnProperty(prefix = ConstConfig.AUTO_ASYNC_EXECUTOR, value = ConstConfig.ENABLED, matchIfMissing = true)
public class AsyncExecutorAutoConfiguration {

	/**
	 * 使用该方式配置的线程池,可以配置多个,只需要使用的beanName不同即可.同时在{@link Async}中需要指定线程池的名称
	 * 
	 * 该方式配置的Executor不受{@link AsyncConfigurer}接口实现类的影响,只和beanName有关
	 * 
	 * @return 线程执行器
	 */
	@Bean
	@ConditionalOnMissingBean
	Executor defaultAsyncExecutor(AsyncExecutorProperties asyncExecutorProperties) {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		// 线程拷贝上下文
		executor.setTaskDecorator(r -> {
			ServletRequestAttributes requestAttributes = WebHelpers.getRequestAttributes();
			return () -> {
				try {
					RequestContextHolder.setRequestAttributes(requestAttributes);
					r.run();
				} finally {
					RequestContextHolder.resetRequestAttributes();
				}
			};
		});
		// 配置核心线程数
		executor.setCorePoolSize(asyncExecutorProperties.getCorePoolSize());
		// 配置最大线程数
		executor.setMaxPoolSize(asyncExecutorProperties.getMaxPoolSize());
		// 配置队列大小
		executor.setQueueCapacity(asyncExecutorProperties.getQueueCapacity());
		// 配置空闲线程的最大时间,超过该时间,线程将被回收
		executor.setKeepAliveSeconds(asyncExecutorProperties.getKeepAliveSeconds());
		// 配置线程名前缀
		executor.setThreadNamePrefix(asyncExecutorProperties.getThreadNamePrefix());
		// 当线程池关闭时,定时任务是否立刻关闭,清空队列,而不等待他们执行完成,默认false,立刻关闭
		executor.setWaitForTasksToCompleteOnShutdown(asyncExecutorProperties.getWaitForTasksToCompleteOnShutdown());
		// 当线程池要关闭时,仍有线程在执行,此时等待的最大时长
		executor.setAwaitTerminationSeconds(asyncExecutorProperties.getAwaitTerminationSeconds());
		// 当线程池中的队列满时,设置拒绝策略
		executor.setRejectedExecutionHandler(asyncExecutorProperties.getRejectedExecutionHandler());
		executor.initialize();
		return executor;
	}
}