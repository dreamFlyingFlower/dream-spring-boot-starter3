package dream.flying.flower.autoconfigure.logger;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
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
import dream.flying.flower.autoconfigure.logger.convert.OperationLogConvert;
import dream.flying.flower.autoconfigure.logger.convert.OperationLogConvertImpl;
import dream.flying.flower.autoconfigure.logger.endpoint.OperationLogEndpoint;
import dream.flying.flower.autoconfigure.logger.processor.FlywayPropertiesBeanProcessor;
import dream.flying.flower.autoconfigure.logger.properties.DreamLogProperties;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import dream.flying.flower.autoconfigure.logger.service.impl.DefaultOperationLogService;
import dream.flying.flower.autoconfigure.logger.support.DefaultLogAspectHandler;
import dream.flying.flower.autoconfigure.logger.support.LogAspectHandler;
import dream.flying.flower.framework.core.constant.ConstConfigPrefix;

/**
 * 操作日志自动配置类 配置日志记录所需的各个组件 包括Logbook配置、异步配置、存储配置等
 * 
 * TODO
 * 
 * <pre>
 * 1.加入swagger文档扫描
 * 2.新增日志时的userId,username,nickname暂时没有值
 * 3.没有过滤敏感字段,过滤相关类型
 * 4.requestBody的形参没有值
 * </pre>
 *
 * @author 飞花梦影
 * @date 2024-01-06 15:30:45
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@EnableConfigurationProperties({ DreamLogProperties.class })
@MapperScan("dream.flying.flower.autoconfigure.logger.mapper")
@Import({ AsyncConfig.class, FlywayPropertiesBeanProcessor.class })
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@AutoConfiguration(before = { LogbookAutoConfiguration.class }, after = { FlywayAutoConfiguration.class })
@ConditionalOnProperty(prefix = ConstConfigPrefix.AUTO_LOGGER, name = ConstConfigPrefix.ENABLED, havingValue = "true",
		matchIfMissing = true)
public class OperationLogAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(OperationLogConvert.class)
	OperationLogConvert operationLogConvert() {
		return new OperationLogConvertImpl();
	}

	@Bean
	@ConditionalOnMissingBean(OperationLogService.class)
	OperationLogService operationLogService() {
		return new DefaultOperationLogService();
	}

	@Bean
	@ConditionalOnMissingBean(OperationLogEndpoint.class)
	OperationLogEndpoint operationLogEndpoint(OperationLogService operationLogService) {
		return new OperationLogEndpoint(operationLogService);
	}

	@Bean
	@ConditionalOnMissingBean(LogAspectHandler.class)
	LogAspectHandler loggerAspectHandler(OperationLogService operationLogService,
			DreamLogProperties dreamLogProperties) {
		return new DefaultLogAspectHandler(operationLogService, dreamLogProperties);
	}

	@Bean
	@ConditionalOnMissingBean(ControllerLogAspect.class)
	ControllerLogAspect controllerLogAspect(LogAspectHandler loggerAspectHandler) {
		return new ControllerLogAspect(loggerAspectHandler);
	}

	@Bean
	@ConditionalOnMissingBean(OperationLogAspect.class)
	OperationLogAspect operationLogAspect(LogAspectHandler loggerAspectHandler) {
		return new OperationLogAspect(loggerAspectHandler);
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