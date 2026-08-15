package dream.flying.flower.autoconfigure.config.cache;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import dream.flying.flower.autoconfigure.config.entity.ConfigEntity;
import dream.flying.flower.autoconfigure.config.mapper.ConfigMapper;
import dream.flying.flower.autoconfigure.config.properties.DreamConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Config cache warmup runner
 *
 * <p>Responsible for cache warmup on startup and scheduled refresh.
 * Delegates actual cache read/write operations to {@link ConfigCacheManager}.
 *
 * @author 飞花梦影
 * @date 2026-08-13
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Slf4j
@RequiredArgsConstructor
public class ConfigCacheWarmupRunner implements CommandLineRunner {

	private final ConfigCacheManager configCacheManager;

	private final ConfigMapper configMapper;

	private final DreamConfigProperties dreamConfigProperties;

	@Override
	public void run(String... args) {
		if (!dreamConfigProperties.isEnabledWarmup()) {
			log.info("Config cache warmup is disabled");
			return;
		}

		log.info("Starting config cache warmup...");
		warmup();
		log.info("Config cache warmup completed");
	}

	@Scheduled(cron = "0 0 * * * ?")
	public void scheduledWarmup() {
		if (!dreamConfigProperties.isEnabledWarmup()) {
			return;
		}

		log.info("Refreshing config cache...");
		warmup();
		log.info("Config cache refresh completed");
	}

	/**
	 * Batch warmup all enabled configs into cache
	 */
	public void warmup() {
		try {
			List<ConfigEntity> configs =
					configMapper.selectList(new LambdaQueryWrapper<ConfigEntity>().eq(ConfigEntity::getStatus, 1)
							.eq(ConfigEntity::getDeleted, 0));

			for (ConfigEntity config : configs) {
				configCacheManager.cache(config);
			}

			log.info("Config data warmup completed, total {} configs", configs.size());
		} catch (Exception e) {
			log.error("Config data warmup failed: error={}", e.getMessage());
		}
	}
}