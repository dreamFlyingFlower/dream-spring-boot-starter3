package dream.flying.flower.autoconfigure.dict.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import dream.flying.flower.autoconfigure.dict.constant.ConstDict;
import dream.flying.flower.autoconfigure.dict.entity.DictEntity;
import dream.flying.flower.autoconfigure.dict.entity.DictItemEntity;
import dream.flying.flower.autoconfigure.dict.mapper.DictItemMapper;
import dream.flying.flower.autoconfigure.dict.mapper.DictMapper;
import dream.flying.flower.autoconfigure.dict.properties.DictProperties;
import dream.flying.flower.framework.constant.ConstCache;
import dream.flying.flower.framework.constant.ConstStarter;
import lombok.extern.slf4j.Slf4j;

/**
 * Dict cache warmup service
 *
 * @author 飞花梦影
 * @date 2026-05-18
 */
@Slf4j
public class DictCacheWarmupService implements CommandLineRunner {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private DictMapper dictMapper;

	@Autowired
	private DictItemMapper dictItemMapper;

	@Autowired
	private DictProperties dictProperties;

	@Override
	public void run(String... args) {
		if (!dictProperties.isWarmupEnabled()) {
			log.info("Dict cache warmup is disabled");
			return;
		}

		log.info("Starting dict cache warmup...");
		warmupAllDicts();
		log.info("Dict cache warmup completed");
	}

	@Scheduled(cron = "0 0 2 * * ?")
	public void scheduledWarmup() {
		if (!dictProperties.isWarmupEnabled()) {
			return;
		}

		log.info("Refreshing dict cache...");
		warmupAllDicts();
		log.info("Dict cache refresh completed");
	}

	private void warmupAllDicts() {
		try {
			List<DictEntity> dicts =
					dictMapper.selectList(new LambdaQueryWrapper<DictEntity>().eq(DictEntity::getDeleted, 0));

			for (DictEntity dict : dicts) {
				cacheDict(dict);
				cacheDictItems(dict.getId());
			}

			log.info("Dict data warmup completed, total {} dicts", dicts.size());
		} catch (Exception e) {
			log.error("Dict data warmup failed: error={}", e.getMessage());
		}
	}

	private void cacheDict(DictEntity dict) {
		String cacheKey = ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME, ConstDict.MODULE_NAME,
				ConstDict.DICT_CACHE_PREFIX, dict.getDictCode());
		try {
			redisTemplate.opsForValue().set(cacheKey, dict, dictProperties.getCacheExpireHours(), TimeUnit.HOURS);
		} catch (Exception e) {
			log.error("Cache dict failed: dictCode={}, error={}", dict.getDictCode(), e.getMessage());
		}
	}

	private void cacheDictItems(Long dictId) {
		List<DictItemEntity> items =
				dictItemMapper.selectList(new LambdaQueryWrapper<DictItemEntity>().eq(DictItemEntity::getDictId, dictId)
						.eq(DictItemEntity::getStatus, 1)
						.eq(DictItemEntity::getDeleted, 0)
						.orderByAsc(DictItemEntity::getSortIndex));

		if (!items.isEmpty()) {
			String cacheKey = ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME, ConstDict.MODULE_NAME,
					ConstDict.DICT_ITEMS_CACHE_PREFIX, dictId + "");
			try {
				redisTemplate.opsForValue().set(cacheKey, items, dictProperties.getCacheExpireHours(), TimeUnit.HOURS);
			} catch (Exception e) {
				log.error("Cache dict items failed: dictId={}, error={}", dictId, e.getMessage());
			}
		}
	}

	public void evictDictCache(String dictCode) {
		try {
			String cacheKey = ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME, ConstDict.MODULE_NAME,
					ConstDict.DICT_CACHE_PREFIX, dictCode);
			redisTemplate.delete(cacheKey);
		} catch (Exception e) {
			log.error("Evict dict cache failed: dictCode={}, error={}", dictCode, e.getMessage());
		}
	}
}