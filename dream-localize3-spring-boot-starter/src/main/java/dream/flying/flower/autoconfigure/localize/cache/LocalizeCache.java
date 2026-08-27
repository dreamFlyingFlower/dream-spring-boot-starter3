package dream.flying.flower.autoconfigure.localize.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 缓存接口
 *
 * @author 飞花梦影
 * @date 2026-08-26 14:36:56
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface LocalizeCache {

	String get(String key);

	Map<String, String> get(List<String> keys);

	Map<String, String> getMap(String cacheKey);

	String getMap(String cacheKey, String hashKey);

	void put(String key, String value, long ttl, TimeUnit unit);

	void put(Map<String, String> entries, long ttl, TimeUnit unit);

	void putMap(String key, Map<String, String> map, long ttl, TimeUnit unit);

	void evict(String key);

	void evict(List<String> keys);

	void evictPattern(String pattern);

	void evictLang(String namespace, String lang);

	void evictNamespace(String namespace, String lang);

	void clear();
}