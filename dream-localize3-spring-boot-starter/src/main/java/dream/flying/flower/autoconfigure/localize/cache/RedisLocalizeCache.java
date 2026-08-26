package dream.flying.flower.autoconfigure.localize.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;

import dream.flying.flower.autoconfigure.localize.constant.ConstLocalize;
import dream.flying.flower.autoconfigure.localize.properties.DreamLocalizeProperties;
import dream.flying.flower.framework.constant.ConstCache;
import dream.flying.flower.framework.constant.ConstStarter;
import lombok.RequiredArgsConstructor;

/**
 * Redis cache implement
 *
 * @author 飞花梦影
 * @date 2026-08-26 14:44:33
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RequiredArgsConstructor
public class RedisLocalizeCache implements LocalizeCache {

	private final RedisTemplate<String, String> redisTemplate;

	private final DreamLocalizeProperties properties;

	@Override
	public String get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	@Override
	public Map<String, String> getBatch(List<String> keys) {
		List<String> values = redisTemplate.opsForValue().multiGet(keys);
		Map<String, String> result = new HashMap<>();
		if (values != null) {
			for (int i = 0; i < keys.size(); i++) {
				if (values.get(i) != null) {
					result.put(keys.get(i), values.get(i));
				}
			}
		}
		return result;
	}

	@Override
	public void put(String key, String value, long ttl, TimeUnit unit) {
		redisTemplate.opsForValue().set(key, value, ttl, unit);
	}

	@Override
	public void putBatch(Map<String, String> entries, long ttl, TimeUnit unit) {
		for (Map.Entry<String, String> entry : entries.entrySet()) {
			redisTemplate.opsForValue().set(entry.getKey(), entry.getValue(), ttl, unit);
		}
	}

	@Override
	public void evict(String key) {
		redisTemplate.delete(key);
	}

	@Override
	public void clear() {
		redisTemplate.delete(redisTemplate.keys(ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME,
				ConstLocalize.MODULE_NAME, properties.getCachePrefix(), "*")));
	}
}