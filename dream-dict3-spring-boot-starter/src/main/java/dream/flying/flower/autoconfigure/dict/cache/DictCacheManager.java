package dream.flying.flower.autoconfigure.dict.cache;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import dream.flying.flower.autoconfigure.dict.constant.ConstDict;
import dream.flying.flower.autoconfigure.dict.entity.DictEntity;
import dream.flying.flower.autoconfigure.dict.entity.DictItemEntity;
import dream.flying.flower.autoconfigure.dict.mapper.DictItemMapper;
import dream.flying.flower.autoconfigure.dict.mapper.DictMapper;
import dream.flying.flower.autoconfigure.dict.properties.DreamDictProperties;
import dream.flying.flower.framework.constant.ConstCache;
import dream.flying.flower.framework.constant.ConstStarter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Dict cache manager
 *
 * <p>
 * Responsible for dict cache read/write/evict/refresh operations.
 *
 * @author 飞花梦影
 * @date 2026-08-15 09:34:11
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Slf4j
@RequiredArgsConstructor
public class DictCacheManager {

	private final RedisTemplate<String, Object> redisTemplate;

	private final DictMapper dictMapper;

	private final DictItemMapper dictItemMapper;

	private final DreamDictProperties dreamDictProperties;

	/**
	 * Write single dict to cache
	 * 
	 * @param dict 字典
	 */
	public void cacheDict(DictEntity dict) {
		String cacheKey = ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME, ConstDict.MODULE_NAME,
				ConstDict.DICT_CACHE_PREFIX, dict.getDictCode());
		try {
			redisTemplate.opsForValue().set(cacheKey, dict, dreamDictProperties.getCacheExpireHours(), TimeUnit.HOURS);
		} catch (Exception e) {
			log.error("Cache dict failed: dictCode={}, error={}", dict.getDictCode(), e.getMessage());
		}
	}

	/**
	 * Write dict items to cache by dictId
	 * 
	 * @param dictId 字典ID
	 */
	public void cacheDictItems(Long dictId) {
		List<DictItemEntity> items =
				dictItemMapper.selectList(new LambdaQueryWrapper<DictItemEntity>().eq(DictItemEntity::getDictId, dictId)
						.eq(DictItemEntity::getDeleted, 0)
						.orderByAsc(DictItemEntity::getSortIndex));

		if (!items.isEmpty()) {
			String cacheKey = ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME, ConstDict.MODULE_NAME,
					ConstDict.DICT_ITEMS_CACHE_PREFIX, dictId + "");
			try {
				redisTemplate.opsForValue()
						.set(cacheKey, items, dreamDictProperties.getCacheExpireHours(), TimeUnit.HOURS);
			} catch (Exception e) {
				log.error("Cache dict items failed: dictId={}, error={}", dictId, e.getMessage());
			}
		}
	}

	/**
	 * Evict dict and its items from cache by dictCode
	 * 
	 * @param dictCode 字典编码
	 */
	public void evict(String dictCode) {
		try {
			DictEntity dict =
					dictMapper.selectOne(new LambdaQueryWrapper<DictEntity>().eq(DictEntity::getDictCode, dictCode)
							.eq(DictEntity::getDeleted, 0));
			if (dict != null) {
				redisTemplate.delete(ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME, ConstDict.MODULE_NAME,
						ConstDict.DICT_ITEMS_CACHE_PREFIX, dict.getId() + ""));
			}
			String cacheKey = ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME, ConstDict.MODULE_NAME,
					ConstDict.DICT_CACHE_PREFIX, dictCode);
			redisTemplate.delete(cacheKey);
		} catch (Exception e) {
			log.error("Evict dict cache failed: dictCode={}, error={}", dictCode, e.getMessage());
		}
	}

	/**
	 * Refresh single dict cache by dict code
	 * 
	 * @param dictCode 字典编码
	 */
	public void refresh(String dictCode) {
		try {
			DictEntity dict =
					dictMapper.selectOne(new LambdaQueryWrapper<DictEntity>().eq(DictEntity::getDictCode, dictCode)
							.eq(DictEntity::getDeleted, 0));
			if (dict != null) {
				cacheDict(dict);
				cacheDictItems(dict.getId());
				log.info("Refresh dict cache success: dictCode={}", dictCode);
			} else {
				log.info("Dict not found, evict cache: dictCode={}", dictCode);
				evict(dictCode);
			}
		} catch (Exception e) {
			log.error("Refresh dict cache failed: dictCode={}, error={}", dictCode, e.getMessage());
		}
	}

	/**
	 * Get dict from cache, fallback to DB if cache miss, then write-through cache
	 * 
	 * @param dictCode 字典编码
	 * @return 字典
	 */
	public DictEntity getDict(String dictCode) {
		String cacheKey = ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME, ConstDict.MODULE_NAME,
				ConstDict.DICT_CACHE_PREFIX, dictCode);
		try {
			Object cached = redisTemplate.opsForValue().get(cacheKey);
			if (cached instanceof DictEntity) {
				return (DictEntity) cached;
			}
		} catch (Exception e) {
			log.error("Get dict from cache failed: dictCode={}, error={}", dictCode, e.getMessage());
		}
		// Cache miss, load from DB
		try {
			DictEntity dict =
					dictMapper.selectOne(new LambdaQueryWrapper<DictEntity>().eq(DictEntity::getDictCode, dictCode)
							.eq(DictEntity::getStatus, 1)
							.eq(DictEntity::getDeleted, 0));
			if (dict != null) {
				cacheDict(dict);
			}
			return dict;
		} catch (Exception e) {
			log.error("Get dict from DB failed: dictCode={}, error={}", dictCode, e.getMessage());
			return null;
		}
	}

	/**
	 * Get dict item list by dictCode from cache, fallback to DB if cache miss
	 * 
	 * @param dictCode 字典编码
	 * @return 字典项列表
	 */
	public List<DictItemEntity> getDictItems(String dictCode) {
		try {
			DictEntity dict =
					dictMapper.selectOne(new LambdaQueryWrapper<DictEntity>().eq(DictEntity::getDictCode, dictCode)
							.eq(DictEntity::getStatus, 1)
							.eq(DictEntity::getDeleted, 0));
			if (dict == null) {
				return Collections.emptyList();
			}
			String cacheKey = ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME, ConstDict.MODULE_NAME,
					ConstDict.DICT_ITEMS_CACHE_PREFIX, dict.getId() + "");
			try {
				@SuppressWarnings("unchecked")
				List<DictItemEntity> cached = (List<DictItemEntity>) redisTemplate.opsForValue().get(cacheKey);
				if (cached != null && !cached.isEmpty()) {
					return cached;
				}
			} catch (Exception e) {
				log.error("Get dict items from cache failed: dictCode={}, error={}", dictCode, e.getMessage());
			}
			// Cache miss, load from DB
			List<DictItemEntity> items = dictItemMapper
					.selectList(new LambdaQueryWrapper<DictItemEntity>().eq(DictItemEntity::getDictId, dict.getId())
							.eq(DictItemEntity::getDeleted, 0)
							.orderByAsc(DictItemEntity::getSortIndex));
			if (!items.isEmpty()) {
				try {
					redisTemplate.opsForValue()
							.set(cacheKey, items, dreamDictProperties.getCacheExpireHours(), TimeUnit.HOURS);
				} catch (Exception e) {
					log.error("Cache dict items on get failed: dictCode={}, error={}", dictCode, e.getMessage());
				}
			}
			return items;
		} catch (Exception e) {
			log.error("Get dict items failed: dictCode={}, error={}", dictCode, e.getMessage());
			return Collections.emptyList();
		}
	}
}