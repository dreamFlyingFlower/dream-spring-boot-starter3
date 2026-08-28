package dream.flying.flower.autoconfigure.localize.cache;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;

import dream.flying.flower.autoconfigure.localize.constant.ConstLocalize;
import dream.flying.flower.framework.constant.ConstCache;
import dream.flying.flower.framework.constant.ConstStarter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis cache implement
 *
 * @author 飞花梦影
 * @date 2026-08-26 14:44:33
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@RequiredArgsConstructor
public class RedisLocalizeCache implements LocalizeCache {

	private final RedisTemplate<String, String> redisTemplate;

	@Override
	public String get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	@Override
	public Map<String, String> get(List<String> keys) {
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
	public Map<String, String> getMap(String cacheKey) {
		Map<Object, Object> entries = redisTemplate.opsForHash().entries(cacheKey);
		if (!entries.isEmpty()) {
			return entries.entrySet()
					.stream()
					.collect(Collectors.toMap(e -> null == e.getKey() ? null : e.getKey().toString(),
							e -> null == e.getValue() ? null : e.getValue().toString(), (o, n) -> o));
		}
		return new HashMap<>();
	}

	@Override
	public String getMap(String cacheKey, String hashKey) {
		Object value = redisTemplate.opsForHash().get(cacheKey, hashKey);
		return null == value ? null : value.toString();
	}

	@Override
	public void put(String key, String value, Duration duration) {
		redisTemplate.opsForValue().set(key, value, duration);
	}

	@Override
	public void put(String key, String value, long ttl, TimeUnit unit) {
		redisTemplate.opsForValue().set(key, value, ttl, unit);
	}

	@Override
	public void put(Map<String, String> entries, Duration duration) {
		for (Map.Entry<String, String> entry : entries.entrySet()) {
			redisTemplate.opsForValue().set(entry.getKey(), entry.getValue(), duration);
		}
	}

	@Override
	public void put(Map<String, String> entries, long ttl, TimeUnit unit) {
		for (Map.Entry<String, String> entry : entries.entrySet()) {
			redisTemplate.opsForValue().set(entry.getKey(), entry.getValue(), ttl, unit);
		}
	}

	@Override
	public void putMap(String key, Map<String, String> map, Duration duration) {
		redisTemplate.opsForHash().putAll(key, map);
		redisTemplate.expire(key, duration);
	}

	@Override
	public void putMap(String key, Map<String, String> map, long ttl, TimeUnit unit) {
		redisTemplate.opsForHash().putAll(key, map);
		redisTemplate.expire(key, ttl, unit);
	}

	@Override
	public void evict(String key) {
		redisTemplate.delete(key);
	}

	@Override
	public void evict(List<String> keys) {
		redisTemplate.delete(keys);
	}

	@Override
	public void evictMap(String cacheKey, List<String> hashKeys) {
		redisTemplate.opsForHash().delete(cacheKey, hashKeys);
	}

	@Override
	public void evictPattern(String pattern) {
		redisTemplate.delete(redisTemplate.keys(pattern));
	}

	@Override
	public void clear() {
		redisTemplate.delete(redisTemplate
				.keys(ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME, ConstLocalize.MODULE_NAME, "*")));
	}
}