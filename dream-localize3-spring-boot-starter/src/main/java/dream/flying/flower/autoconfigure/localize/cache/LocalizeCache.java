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

	Map<String, String> getBatch(List<String> keys);

	void put(String key, String value, long ttl, TimeUnit unit);

	void putBatch(Map<String, String> entries, long ttl, TimeUnit unit);

	void evict(String key);

	void clear();
}