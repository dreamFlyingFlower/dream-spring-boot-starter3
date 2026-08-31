package dream.flying.flower.autoconfigure.localize.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import dream.flying.flower.autoconfigure.localize.cache.LocalizeCache;
import dream.flying.flower.autoconfigure.localize.constant.ConstLocalize;
import dream.flying.flower.autoconfigure.localize.convert.LocalizeConvert;
import dream.flying.flower.autoconfigure.localize.entity.LanguageEntity;
import dream.flying.flower.autoconfigure.localize.entity.LocalizeEntity;
import dream.flying.flower.autoconfigure.localize.helpers.LocalizeHelpers;
import dream.flying.flower.autoconfigure.localize.mapper.LanguageMapper;
import dream.flying.flower.autoconfigure.localize.mapper.LocalizeMapper;
import dream.flying.flower.autoconfigure.localize.properties.DreamLocalizeProperties;
import dream.flying.flower.autoconfigure.localize.query.LocalizeQuery;
import dream.flying.flower.autoconfigure.localize.service.LocalizeService;
import dream.flying.flower.autoconfigure.localize.vo.LocalizeVO;
import dream.flying.flower.collection.ListHelper;
import dream.flying.flower.framework.constant.ConstCache;
import dream.flying.flower.framework.constant.ConstCore;
import dream.flying.flower.framework.constant.ConstStarter;
import dream.flying.flower.framework.mybatis.plus.service.impl.AbstractServiceImpl;
import dream.flying.flower.lang.StrHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Localize service implement
 *
 * @author 飞花梦影
 * @date 2026-05-20 10:43:03
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@RequiredArgsConstructor
public class LocalizeServiceImpl
		extends AbstractServiceImpl<LocalizeEntity, LocalizeVO, LocalizeQuery, LocalizeConvert, LocalizeMapper>
		implements LocalizeService {

	private final LocalizeCache localizeCache;

	private final LanguageMapper languageMapper;

	private final DreamLocalizeProperties dreamLocalizeProperties;

	@Override
	public String getMessage(String localizeCode) {
		return getMessage(localizeCode, LocalizeHelpers.getLang());
	}

	@Override
	public String getMessage(String localizeCode, String lang) {
		String formatLang = LocalizeHelpers.parse(lang).toLanguageTag();
		String cacheKey = buildCacheKey(formatLang, localizeCode);
		// Try to get from cache first
		try {
			String cached = localizeCache.get(cacheKey);
			if (cached != null) {
				return cached;
			}
		} catch (Exception e) {
			// Redis connection failed, query directly from database
			log.info("Localize cache query failed from redis, retrieving from database");
		}

		// Query from database
		try {
			String result = getContent(localizeCode, formatLang);
			if (result != null) {
				localizeCache.put(cacheKey, result, dreamLocalizeProperties.getExpire());
			}
			return result;
		} catch (Exception e) {
			// Database query failed
			log.error("Localize database query failed for code: {}, lang: {}", localizeCode, formatLang, e);
		}
		return localizeCode;
	}

	private String getContent(String localizeCode, String lang) {
		LocalizeEntity localizeEntity =
				getOne(new LambdaQueryWrapper<LocalizeEntity>().eq(LocalizeEntity::getLocalizeCode, localizeCode)
						.eq(LocalizeEntity::getFullLang, lang));

		// Direct match with non-empty content, return immediately
		if (localizeEntity != null && StrHelper.isNotBlank(localizeEntity.getContent())) {
			return localizeEntity.getContent();
		}

		// Fallback processing
		List<String> fallbackChain = LocalizeHelpers.buildFallback(lang);

		// Only enabled (enabled=1, deleted=0) languages participate in fallback chain.
		List<LanguageEntity> tags = languageMapper
				.selectList(new LambdaQueryWrapper<LanguageEntity>().in(LanguageEntity::getFullLang, fallbackChain)
						.eq(LanguageEntity::getEnabled, ConstCore.ENABLE));

		Map<String, LanguageEntity> fullLang2Language =
				tags.stream().collect(Collectors.toMap(LanguageEntity::getFullLang, Function.identity()));

		for (String languageTag : fallbackChain) {
			LanguageEntity matchedTag = fullLang2Language.get(languageTag);
			if (matchedTag != null) {
				LocalizeEntity item = baseMapper.selectOne(
						new LambdaQueryWrapper<LocalizeEntity>().eq(LocalizeEntity::getLanguageId, matchedTag.getId())
								.eq(LocalizeEntity::getLocalizeCode, localizeCode));
				if (null != item && StrHelper.isNotBlank(item.getContent())) {
					return item.getContent();
				}
			}
		}

		return localizeCode;
	}

	@Override
	public Map<String, String> getMessages() {
		return getMessages(LocalizeHelpers.getLang());
	}

	@Override
	public Map<String, String> getMessages(String lang) {
		String formatLang =
				StrHelper.isNotBlank(lang) ? LocalizeHelpers.parse(lang).toLanguageTag() : LocalizeHelpers.getLang();
		String cacheKey = buildCacheKey(formatLang);

		// Try to get from cache first
		try {
			Map<String, String> cachedMap = localizeCache.getMap(cacheKey);
			if (cachedMap != null && !cachedMap.isEmpty()) {
				return cachedMap;
			}
		} catch (Exception e) {
			// Redis connection failed, query directly from database
			log.error("Localize getAllMessages cache query failed for lang: {}", formatLang, e);
		}

		// Query from database using standardized lang
		List<LocalizeEntity> messages =
				list(new LambdaQueryWrapper<LocalizeEntity>().eq(LocalizeEntity::getFullLang, formatLang));

		Map<String, String> messageMap = messages.stream()
				.collect(Collectors.toMap(LocalizeEntity::getLocalizeCode, LocalizeEntity::getContent));

		// Put into cache with valid TTL
		try {
			if (!messageMap.isEmpty()) {
				localizeCache.putMap(cacheKey, messageMap, dreamLocalizeProperties.getExpire());
			}
		} catch (Exception e) {
			// Redis connection failed, ignore cache operation
			log.error("Localize getAllMessages cache put failed for lang: {}", formatLang, e);
		}

		return messageMap;
	}

	@Override
	public void clearCache() {
		try {
			localizeCache.clear();
		} catch (Exception e) {
			// Redis connection failed, ignore cache operation
			log.error("Localize clearCache failed", e);
		}
	}

	@Override
	public void evictCache(String lang) {
		try {
			// Clear all message caches for this language
			localizeCache.evictPattern(buildCacheKey(lang));
		} catch (Exception e) {
			// Redis connection failed, ignore cache operation
			log.error("Localize evictCache failed for lang: {}", lang, e);
		}
	}

	/**
	 * Get internationalized dict name
	 *
	 * @param localizeCode localize code
	 * @param locale locale
	 * @return internationalized dict name, or null if not exists
	 */
	@Override
	public String getDictName(String localizeCode, Locale locale) {
		if (StrHelper.isBlank(localizeCode)) {
			return null;
		}
		String lang = LocalizeHelpers.getLang(locale);
		return getMessage(localizeCode, lang);
	}

	/**
	 * Get internationalized dict item name
	 *
	 * @param localizeCode localize code
	 * @param locale locale
	 * @return internationalized dict item name, or null if not exists
	 */
	@Override
	public String getDictItemName(String localizeCode, Locale locale) {
		if (StrHelper.isBlank(localizeCode)) {
			return null;
		}
		String lang = LocalizeHelpers.getLang(locale);
		return getMessage(localizeCode, lang);
	}

	/**
	 * Batch get dict internationalized content
	 *
	 * @param localizeCodes localize codes list
	 * @param locale locale
	 * @return Map<localizeCode, internationalized content>
	 */
	@Override
	public Map<String, String> getDictI18nNames(List<String> localizeCodes, Locale locale) {
		if (CollectionUtils.isEmpty(localizeCodes)) {
			return Map.of();
		}
		String lang = LocalizeHelpers.getLang(locale);
		return getMessages(lang).entrySet()
				.stream()
				.filter(entry -> localizeCodes.contains(entry.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	/**
	 * Batch get dict item internationalized content
	 *
	 * @param localizeCodes message codes list
	 * @param locale locale
	 * @return Map<localizeCode, internationalized content>
	 */
	@Override
	public Map<String, String> getDictItemI18nNames(List<String> localizeCodes, Locale locale) {
		if (CollectionUtils.isEmpty(localizeCodes)) {
			return Map.of();
		}
		String lang = LocalizeHelpers.getLang(locale);
		return getMessages(lang).entrySet()
				.stream()
				.filter(entry -> localizeCodes.contains(entry.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@Override
	public Map<String, String> getMessages(List<String> localizeCodes, String lang) {
		if (CollectionUtils.isEmpty(localizeCodes)) {
			return Map.of();
		}
		String formatLang = LocalizeHelpers.parse(lang).toLanguageTag();
		List<String> cacheKeys =
				localizeCodes.stream().map(key -> buildCacheKey(formatLang, key)).collect(Collectors.toList());

		Map<String, String> cached = new HashMap<>();
		try {
			cached = localizeCache.get(cacheKeys);
		} catch (Exception e) {
			log.info("Localize getMessages cache query failed from redis, retrieving from database");
		}
		Map<String, String> result = new LinkedHashMap<>();
		List<String> missKeys = new ArrayList<>();

		for (int i = 0; i < localizeCodes.size(); i++) {
			String key = localizeCodes.get(i);
			String cacheKey = cacheKeys.get(i);
			if (cached.containsKey(cacheKey)) {
				result.put(key, cached.get(cacheKey));
			} else {
				missKeys.add(key);
			}
		}

		if (!missKeys.isEmpty()) {
			Map<String, String> dbResult = getContents(missKeys, formatLang);
			Map<String, String> cacheEntries = new HashMap<>();
			for (Map.Entry<String, String> entry : dbResult.entrySet()) {
				// Only cache results that differ from the code itself (i.e., real translations)
				if (!entry.getKey().equals(entry.getValue())) {
					String cacheKey = buildCacheKey(formatLang, entry.getKey());
					cacheEntries.put(cacheKey, entry.getValue());
				}
			}
			if (!cacheEntries.isEmpty()) {
				try {
					localizeCache.put(cacheEntries, dreamLocalizeProperties.getExpire());
				} catch (Exception e) {
					log.error("Localize getMessages cache put failed for lang: {}", formatLang, e);
				}
			}
			result.putAll(dbResult);
		}

		return result;
	}

	private Map<String, String> getContents(List<String> localizeCodes, String lang) {
		// Direct query by localizeCodes + exact fullLang, batch, ignore logically
		// deleted rows
		List<LocalizeEntity> localizeEntitys =
				list(new LambdaQueryWrapper<LocalizeEntity>().in(LocalizeEntity::getLocalizeCode, localizeCodes)
						.eq(LocalizeEntity::getFullLang, lang));

		Map<String, String> result = new LinkedHashMap<>();
		// Default all requested codes to the code itself
		localizeCodes.stream().forEach(t -> result.put(t, t));

		// Track codes that still need fallback resolution
		List<String> unresolvedKeys = new ArrayList<>(localizeCodes);

		// Apply direct matches first, removing them from unresolved set
		if (ListHelper.isNotEmpty(localizeEntitys)) {
			for (LocalizeEntity entity : localizeEntitys) {
				if (StrHelper.isNotBlank(entity.getContent())) {
					result.put(entity.getLocalizeCode(), entity.getContent());
					unresolvedKeys.remove(entity.getLocalizeCode());
				}
			}
		}

		// All codes resolved, no fallback needed
		if (unresolvedKeys.isEmpty()) {
			return result;
		}

		// Build fallback chain and collect enabled/not-deleted candidate languageIds
		List<String> fallbackChain = LocalizeHelpers.buildFallback(lang);

		List<LanguageEntity> tags = languageMapper
				.selectList(new LambdaQueryWrapper<LanguageEntity>().in(LanguageEntity::getFullLang, fallbackChain)
						.eq(LanguageEntity::getEnabled, ConstCore.ENABLE));

		if (CollectionUtils.isEmpty(tags)) {
			return result;
		}

		// Map fullLang -> LanguageEntity, built from matched tags for quick lookup
		Map<String, LanguageEntity> fullLang2Language =
				tags.stream().collect(Collectors.toMap(LanguageEntity::getFullLang, Function.identity()));

		// Batch fallback query by (all ids in matched tags) AND (unresolved
		// localizeCodes).
		// The order inside the SQL IN() list does not affect the result set; the strict
		// fallback priority is enforced later when iterating fallbackChain directly.
		List<Long> tagIds = tags.stream().map(LanguageEntity::getId).collect(Collectors.toList());
		List<LocalizeEntity> fallbackEntities =
				baseMapper.selectList(new LambdaQueryWrapper<LocalizeEntity>().in(LocalizeEntity::getLanguageId, tagIds)
						.in(LocalizeEntity::getLocalizeCode, unresolvedKeys));

		if (ListHelper.isEmpty(fallbackEntities)) {
			return result;
		}

		// Build lookup: (languageId, localizeCode) -> content
		Map<String, String> fallbackLookup = new HashMap<>();
		for (LocalizeEntity e : fallbackEntities) {
			if (StrHelper.isNotBlank(e.getContent())) {
				fallbackLookup.put(e.getLanguageId() + "_" + e.getLocalizeCode(), e.getContent());
			}
		}

		// Apply fallback in the exact priority order defined by fallbackChain
		// (most specific tag first, e.g. zh-Hans-CN -> zh-CN -> zh-Hans -> zh).
		// Reuse the already-built fullLang2Language index to resolve the languageId,
		// so we avoid an extra intermediate orderedLanguageIds materialization.
		// Codes already resolved by the higher-priority language are skipped
		// (resolvedSet),
		// so a lower-priority language never overwrites a higher-priority match.
		Set<String> resolvedSet = new HashSet<>();
		for (String languageTag : fallbackChain) {
			LanguageEntity matchedTag = fullLang2Language.get(languageTag);
			if (matchedTag == null) {
				continue;
			}
			Long langId = matchedTag.getId();
			for (String code : unresolvedKeys) {
				if (resolvedSet.contains(code)) {
					continue;
				}
				String lookupKey = langId + "_" + code;
				if (fallbackLookup.containsKey(lookupKey)) {
					result.put(code, fallbackLookup.get(lookupKey));
					resolvedSet.add(code);
				}
			}
			if (resolvedSet.size() == unresolvedKeys.size()) {
				break;
			}
		}

		return result;
	}

	private String buildCacheKey(String lang) {
		return ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME, ConstLocalize.MODULE_NAME, lang, "*");
	}

	private String buildCacheKey(String lang, String localizeCode) {
		return ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME, ConstLocalize.MODULE_NAME, lang, localizeCode);
	}

	// ============== Write hooks: invalidate cache after any DB modification
	// =========

	@Override
	public boolean save(LocalizeEntity entity) {
		boolean ok = super.save(entity);
		if (ok) {
			evictCacheByEntity(entity);
		}
		return ok;
	}

	@Override
	public boolean updateById(LocalizeEntity entity) {
		boolean ok = super.updateById(entity);
		if (ok) {
			evictCacheByEntity(entity);
		}
		return ok;
	}

	@Override
	public boolean removeById(Serializable id) {
		LocalizeEntity old = getById(id);
		boolean ok = super.removeById(id);
		if (ok) {
			if (old != null) {
				evictCacheByEntity(old);
			} else {
				clearCache();
			}
		}
		return ok;
	}

	@Override
	public boolean saveBatch(Collection<LocalizeEntity> entityList) {
		boolean ok = super.saveBatch(entityList);
		if (ok) {
			evictCacheByEntities(entityList);
		}
		return ok;
	}

	@Override
	public boolean updateBatchById(Collection<LocalizeEntity> entityList) {
		boolean ok = super.updateBatchById(entityList);
		if (ok) {
			evictCacheByEntities(entityList);
		}
		return ok;
	}

	@Override
	public boolean remove(com.baomidou.mybatisplus.core.conditions.Wrapper<LocalizeEntity> queryWrapper) {
		boolean ok = super.remove(queryWrapper);
		if (ok) {
			clearCache();
		}
		return ok;
	}

	/**
	 * Evict the entire language cache for the fullLang tag carried on the entity.
	 * If the entity does not carry fullLang, fall back to full cache clear.
	 *
	 * @param entity written localize entity
	 */
	private void evictCacheByEntity(LocalizeEntity entity) {
		if (entity == null) {
			return;
		}
		String tag = entity.getFullLang();
		if (tag != null && !tag.isEmpty()) {
			evictCache(tag);
		} else {
			clearCache();
		}
	}

	/**
	 * Evict language caches for all distinct fullLang tags present on the
	 * collection. Accepts any Collection subtype (List, Set, Iterable returned by
	 * callers) and guards against null/empty via ListHelper.isEmpty. If any entity
	 * has no fullLang tag, fall back to full cache clear.
	 *
	 * @param entityList written batch collection (any subtype, nullable)
	 */
	private void evictCacheByEntities(Collection<LocalizeEntity> entityList) {
		if (ListHelper.isEmpty(entityList)) {
			return;
		}
		boolean safe = true;
		Set<String> tags = new HashSet<>(4);
		for (LocalizeEntity e : entityList) {
			if (e == null || StrHelper.isBlank(e.getFullLang())) {
				safe = false;
				break;
			}
			tags.add(e.getFullLang());
		}
		if (safe) {
			for (String tag : tags) {
				evictCache(tag);
			}
		} else {
			clearCache();
		}
	}
}