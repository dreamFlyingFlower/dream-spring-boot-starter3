package dream.flying.flower.autoconfigure.redis.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 切面配置
 *
 * @author 飞花梦影
 * @date 2022-11-15 20:26:13
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@ConfigurationProperties(prefix = "dream.redis.aspect")
@Data
public class AspectProperties {

	/** 幂等切面请求头中的key */
	private String idempontentHeaderName;
}