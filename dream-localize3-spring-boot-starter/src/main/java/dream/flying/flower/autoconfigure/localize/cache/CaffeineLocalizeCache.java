package dream.flying.flower.autoconfigure.localize.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Caffeine Cache implement
 *
 * @author 飞花梦影
 * @date 2026-08-26 14:37:43
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@RequiredArgsConstructor
public class CaffeineLocalizeCache implements LocalizeCache {

	private final Cache<String, String> cache;

	@Override
	public String get(String key) {
		return cache.getIfPresent(key);
	}

	@Override
	public Map<String, String> getBatch(List<String> keys) {
		Map<String, String> result = new HashMap<>();
		for (String key : keys) {
			String value = cache.getIfPresent(key);
			if (value != null) {
				result.put(key, value);
			}
		}
		return result;
	}

	@Override
	public void put(String key, String value, long ttl, TimeUnit unit) {
		cache.put(key, value);
	}

	@Override
	public void putBatch(Map<String, String> entries, long ttl, TimeUnit unit) {
		cache.putAll(entries);
	}

	@Override
	public void evict(String key) {
		cache.invalidate(key);
	}

	@Override
	public void clear() {
		cache.invalidateAll();
	}
}