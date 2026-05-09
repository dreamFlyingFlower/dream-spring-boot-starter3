package dream.flying.flower.autoconfigure.redis.web;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisOperations;

/**
 * Redis Web管理自动配置类
 *
 * @author 飞花梦影
 * @date 2026-05-09 15:36:00
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@AutoConfiguration
@ConditionalOnClass(RedisOperations.class)
@ComponentScan(basePackages = "dream.flying.flower.web")
public class RedisWebAutoConfiguration {
}
