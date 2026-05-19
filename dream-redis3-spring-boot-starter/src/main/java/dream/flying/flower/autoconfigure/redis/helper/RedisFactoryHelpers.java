package dream.flying.flower.autoconfigure.redis.helper;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 *
 * @author 飞花梦影
 * @date 2026-05-19 11:54:24
 */
@Slf4j
@Getter
@Component
@Scope("singleton")
@RequiredArgsConstructor
public class RedisFactoryHelpers {

	private final RedisProperties redisProperties;

	private final RedisConnectionFactory defaultConnectionFactory;

	// 分别缓存两种类型的连接工厂
	private final ConcurrentHashMap<Integer, RedisConnectionFactory> factoryCache = new ConcurrentHashMap<>(2);

	public RedisStandaloneConfiguration getDefaultConfiguration() {
		if (defaultConnectionFactory instanceof LettuceConnectionFactory lettuceConnectionFactory) {
			return lettuceConnectionFactory.getStandaloneConfiguration();
		} else if (defaultConnectionFactory instanceof JedisConnectionFactory jedisConnectionFactory) {
			return jedisConnectionFactory.getStandaloneConfiguration();
		}

		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(redisProperties.getHost());
		redisStandaloneConfiguration.setPort(redisProperties.getPort());
		redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
		redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());

		return redisStandaloneConfiguration;
	}

	public RedisConnectionFactory getConnectionFactory() {
		return factoryCache.computeIfAbsent(redisProperties.getDatabase(), this::createConnectionFactory);
	}

	public RedisConnectionFactory getConnectionFactory(Integer databaseIndex) {
		return factoryCache.computeIfAbsent(databaseIndex, this::createConnectionFactory);
	}

	private RedisConnectionFactory createConnectionFactory(Integer databaseIndex) {
		RedisStandaloneConfiguration redisStandaloneConfiguration = getDefaultConfiguration();
		RedisStandaloneConfiguration newConfig = new RedisStandaloneConfiguration();
		newConfig.setHostName(redisStandaloneConfiguration.getHostName());
		newConfig.setPort(redisStandaloneConfiguration.getPort());
		newConfig.setPassword(redisStandaloneConfiguration.getPassword());
		newConfig.setDatabase(databaseIndex);

		// 保持与原始连接工厂相同的类型
		if (defaultConnectionFactory instanceof LettuceConnectionFactory) {
			LettuceConnectionFactory factory = new LettuceConnectionFactory(newConfig);
			factory.afterPropertiesSet();
			return factory;
		} else if (defaultConnectionFactory instanceof JedisConnectionFactory) {
			JedisConnectionFactory factory = new JedisConnectionFactory(newConfig);
			factory.afterPropertiesSet();
			return factory;
		}

		throw new IllegalStateException(
				"Unsupported connection factory : " + defaultConnectionFactory.getClass().getName());
	}

	public <T> T executeWithDatabase(int databaseIndex, RedisOperationCallback<T> callback) {
		RedisConnectionFactory factory = getConnectionFactory(databaseIndex);
		try (RedisConnection connection = factory.getConnection()) {
			return callback.doInConnection(connection);
		}
	}

	@PreDestroy
	public void destroy() {
		factoryCache.values().forEach(factory -> {
			if (factory instanceof DisposableBean disposableBean) {
				try {
					disposableBean.destroy();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		factoryCache.clear();
	}

	@FunctionalInterface
	public interface RedisOperationCallback<T> {

		T doInConnection(RedisConnection connection);
	}
}