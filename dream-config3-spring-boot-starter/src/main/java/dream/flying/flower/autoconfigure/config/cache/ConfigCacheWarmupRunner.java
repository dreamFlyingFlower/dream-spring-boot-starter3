package dream.flying.flower.autoconfigure.config.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import dream.flying.flower.autoconfigure.config.constant.ConstConfig;
import dream.flying.flower.autoconfigure.config.entity.ConfigEntity;
import dream.flying.flower.autoconfigure.config.mapper.ConfigMapper;
import dream.flying.flower.autoconfigure.config.properties.DreamConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Config cache warmup service
 *
 * @author 飞花梦影
 * @date 2026-08-13
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Slf4j
@RequiredArgsConstructor
public class ConfigCacheWarmupRunner implements CommandLineRunner {

	private final RedisTemplate<String, Object> redisTemplate;

	private final ConfigMapper configMapper;

	private final DreamConfigProperties dreamConfigProperties;

	@Override
	public void run(String... args) {
		if (!dreamConfigProperties.isWarmupEnabled()) {
			log.info("Config cache warmup is disabled");
			return;
		}

		log.info("Starting config cache warmup...");
		warmupAll();
		log.info("Config cache warmup completed");
	}

	@Scheduled(cron = "0 0 * * * ?")
	public void scheduledWarmup() {
		if (!dreamConfigProperties.isWarmupEnabled()) {
			return;
		}

		log.info("Refreshing config cache...");
		warmupAll();
		log.info("Config cache refresh completed");
	}

	public void warmupAll() {
		try {
			List<ConfigEntity> configs =
					configMapper.selectList(new LambdaQueryWrapper<ConfigEntity>().eq(ConfigEntity::getStatus, 1)
							.eq(ConfigEntity::getDeleted, 0));

			for (ConfigEntity config : configs) {
				cache(config);
			}

			log.info("Config data warmup completed, total {} configs", configs.size());
		} catch (Exception e) {
			log.error("Config data warmup failed: error={}", e.getMessage());
		}
	}

	private void cache(ConfigEntity config) {
		String cacheKey = buildCacheKey(config.getConfigKey());
		try {
			redisTemplate.opsForValue()
					.set(cacheKey, config, dreamConfigProperties.getCacheExpireHours(), TimeUnit.HOURS);
		} catch (Exception e) {
			log.error("Cache config failed: configKey={}, error={}", config.getConfigKey(), e.getMessage());
		}
	}

	private String buildCacheKey(String configKey) {
		return "dream-config" + ":" + ConstConfig.MODULE_NAME + ":" + ConstConfig.CONFIG_CACHE_PREFIX + ":" + configKey;
	}

	public void evict(String configKey) {
		try {
			String cacheKey = buildCacheKey(configKey);
			redisTemplate.delete(cacheKey);
		} catch (Exception e) {
			log.error("Evict config cache failed: configKey={}, error={}", configKey, e.getMessage());
		}
	}

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
}