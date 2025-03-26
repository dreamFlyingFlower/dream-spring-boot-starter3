package dream.flying.flower.autoconfigure.logger;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.zalando.logbook.Sink;
import org.zalando.logbook.autoconfigure.LogbookAutoConfiguration;
import org.zalando.logbook.core.DefaultHttpLogFormatter;
import org.zalando.logbook.core.DefaultHttpLogWriter;
import org.zalando.logbook.core.DefaultSink;

import dream.flying.flower.autoconfigure.logger.aspect.ControllerLogAspect;
import dream.flying.flower.autoconfigure.logger.aspect.OperationLogAspect;
import dream.flying.flower.autoconfigure.logger.config.AsyncConfig;
import dream.flying.flower.autoconfigure.logger.processor.FlywayPropertiesBeanProcessor;
import dream.flying.flower.autoconfigure.logger.properties.DreamLoggerProperties;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import dream.flying.flower.autoconfigure.logger.service.impl.OperationLogServiceImpl;
import dream.flying.flower.framework.core.constant.ConstConfigPrefix;

/**
 * 操作日志自动配置类 配置日志记录所需的各个组件 包括Logbook配置、异步配置、存储配置等
 *
 * @author 飞花梦影
 * @date 2024-01-06 15:30:45
 * @since 1.0.0
 */
@AutoConfiguration(before = { LogbookAutoConfiguration.class })
@EnableConfigurationProperties({ DreamLoggerProperties.class })
@MapperScan("dream.flying.flower.autoconfigure.logger.mapper")
@Import({ AsyncConfig.class, FlywayPropertiesBeanProcessor.class })
@ConditionalOnProperty(prefix = ConstConfigPrefix.AUTO_LOGGER, name = ConstConfigPrefix.ENABLED, havingValue = "true",
		matchIfMissing = true)
public class OperationLogAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(OperationLogService.class)
	OperationLogService operationLogService() {
		return new OperationLogServiceImpl();
	}

	@Bean
	@ConditionalOnMissingBean(OperationLogAspect.class)
	OperationLogAspect operationLogAspect(ApplicationContext applicationContext,
			OperationLogService operationLogService, DreamLoggerProperties dreamLoggerProperties) {
		return new OperationLogAspect(applicationContext, operationLogService, dreamLoggerProperties);
	}

	@Bean
	@ConditionalOnMissingBean(ControllerLogAspect.class)
	ControllerLogAspect controllerLogAspect(ApplicationContext applicationContext,
			OperationLogService operationLogService, DreamLoggerProperties dreamLoggerProperties) {
		return new ControllerLogAspect(applicationContext, operationLogService, dreamLoggerProperties);
	}

	@Bean
	@ConditionalOnMissingBean(Sink.class)
	Sink sink(Environment environment, LoggingSystem loggingSystem) {
		// 设置logbook日志级别
		if (!environment.containsProperty("logging.level.org.zalando.logbook")) {
			loggingSystem.setLogLevel("org.zalando.logbook", LogLevel.TRACE);
		}
		// 输出到控制台的日志
		return new DefaultSink(new DefaultHttpLogFormatter(), new DefaultHttpLogWriter());
	}
}