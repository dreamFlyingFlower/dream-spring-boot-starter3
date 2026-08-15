package dream.flying.flower.autoconfigure.config.cache;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import dream.flying.flower.autoconfigure.config.constant.ConstConfig;
import dream.flying.flower.autoconfigure.config.entity.ConfigEntity;
import dream.flying.flower.autoconfigure.config.mapper.ConfigMapper;
import dream.flying.flower.autoconfigure.config.properties.DreamConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Config cache service
 *
 * <p>Responsible for config cache read/write/evict/refresh operations.
 *
 * @author 飞花梦影
 * @date 2026-08-15
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Slf4j
@RequiredArgsConstructor
public class ConfigCacheManager {

	private final RedisTemplate<String, Object> redisTemplate;

	private final ConfigMapper configMapper;

	private final DreamConfigProperties dreamConfigProperties;

	/**
	 * Write single config to cache
	 */
	public void cache(ConfigEntity config) {
		String cacheKey = buildCacheKey(config.getConfigKey());
		try {
			redisTemplate.opsForValue()
					.set(cacheKey, config, dreamConfigProperties.getCacheExpireHours(), TimeUnit.HOURS);
		} catch (Exception e) {
			log.error("Cache config failed: configKey={}, error={}", config.getConfigKey(), e.getMessage());
		}
	}

	/**
	 * Evict config from cache by configKey
	 */
	public void evict(String configKey) {
		try {
			String cacheKey = buildCacheKey(configKey);
			redisTemplate.delete(cacheKey);
		} catch (Exception e) {
			log.error("Evict config cache failed: configKey={}, error={}", configKey, e.getMessage());
		}
	}

	/**
	 * Refresh single config cache by configKey
	 */
	public void refresh(String configKey) {
		try {
			ConfigEntity config = configMapper
					.selectOne(new LambdaQueryWrapper<ConfigEntity>().eq(ConfigEntity::getConfigKey, configKey)
							.eq(ConfigEntity::getStatus, 1)
							.eq(ConfigEntity::getDeleted, 0));
			if (config != null) {
				cache(config);
			} else {
				evict(configKey);
			}
		} catch (Exception e) {
			log.error("Refresh config cache failed: configKey={}, error={}", configKey, e.getMessage());
		}
	}

	/**
	 * Get config from cache, fallback to DB if cache miss, then write-through cache
	 */
	public ConfigEntity get(String configKey) {
		String cacheKey = buildCacheKey(configKey);
		try {
			Object cached = redisTemplate.opsForValue().get(cacheKey);
			if (cached instanceof ConfigEntity) {
				return (ConfigEntity) cached;
			}
		} catch (Exception e) {
			log.error("Get config from cache failed: configKey={}, error={}", configKey, e.getMessage());
		}
		// Cache miss, load from DB
		try {
			ConfigEntity config = configMapper
					.selectOne(new LambdaQueryWrapper<ConfigEntity>().eq(ConfigEntity::getConfigKey, configKey)
							.eq(ConfigEntity::getStatus, 1)
							.eq(ConfigEntity::getDeleted, 0));
			if (config != null) {
				cache(config);
			}
			return config;
		} catch (Exception e) {
			log.error("Get config from DB failed: configKey={}, error={}", configKey, e.getMessage());
			return null;
		}
	}

	private String buildCacheKey(String configKey) {
		return "dream-config" + ":" + ConstConfig.MODULE_NAME + ":" + ConstConfig.CONFIG_CACHE_PREFIX + ":" + configKey;
	}
}