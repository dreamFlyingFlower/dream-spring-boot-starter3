package dream.flying.flower.autoconfigure.dict.cache;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import dream.flying.flower.autoconfigure.dict.entity.DictEntity;
import dream.flying.flower.autoconfigure.dict.mapper.DictMapper;
import dream.flying.flower.autoconfigure.dict.properties.DreamDictProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Dict cache warmup runner
 *
 * <p>
 * Responsible for cache warmup on startup and scheduled refresh. Delegates
 * actual cache read/write operations to {@link DictCacheManager}.
 *
 * @author 飞花梦影
 * @date 2026-08-15 09:34:11
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Slf4j
@RequiredArgsConstructor
public class DictCacheWarmupRunner implements CommandLineRunner {

	private final DictCacheManager dictCacheManager;

	private final DictMapper dictMapper;

	private final DreamDictProperties dreamDictProperties;

	@Override
	public void run(String... args) {
		if (!dreamDictProperties.isEnabledWarmup()) {
			log.info("Dict cache warmup is disabled");
			return;
		}

		log.info("Starting dict cache warmup...");
		warmup();
		log.info("Dict cache warmup completed");
	}

	@Scheduled(cron = "0 0 2 * * ?")
	public void scheduledWarmup() {
		if (!dreamDictProperties.isEnabledWarmup()) {
			return;
		}

		log.info("Refreshing dict cache...");
		warmup();
		log.info("Dict cache refresh completed");
	}

	/**
	 * Batch warmup all dicts and their items into cache
	 */
	public void warmup() {
		try {
			List<DictEntity> dicts =
					dictMapper.selectList(new LambdaQueryWrapper<DictEntity>().eq(DictEntity::getDeleted, 0));

			for (DictEntity dict : dicts) {
				dictCacheManager.cacheDict(dict);
				dictCacheManager.cacheDictItems(dict.getId());
			}

			log.info("Dict data warmup completed, total {} dicts", dicts.size());
		} catch (Exception e) {
			log.error("Dict data warmup failed: error={}", e.getMessage());
		}
	}
}