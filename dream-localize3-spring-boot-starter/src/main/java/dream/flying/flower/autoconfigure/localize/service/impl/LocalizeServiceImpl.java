package dream.flying.flower.autoconfigure.localize.service.impl;

import java.util.ArrayList;
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

		List<LanguageEntity> tags = languageMapper
				.selectList(new LambdaQueryWrapper<LanguageEntity>().in(LanguageEntity::getFullLang, fallbackChain));

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
	public Map<String, String> getAllMessages(String lang) {
		String formatLang = LocalizeHelpers.parse(lang).toLanguageTag();

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
				list(new LambdaQueryWrapper<LocalizeEntity>().eq(LocalizeEntity::getFullLang, formatLang)
						.eq(LocalizeEntity::getDeleted, 0));

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
		return getAllMessages(lang).entrySet()
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
		return getAllMessages(lang).entrySet()
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
		// Direct query by localizeCodes + exact fullLang, batch
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

		// Build fallback chain and collect all candidate languageIds
		List<String> fallbackChain = LocalizeHelpers.buildFallback(lang);

		List<LanguageEntity> tags = languageMapper
				.selectList(new LambdaQueryWrapper<LanguageEntity>().in(LanguageEntity::getFullLang, fallbackChain));

		if (CollectionUtils.isEmpty(tags)) {
			return result;
		}

		// Order languageIds by fallback chain priority (most specific first)
		Map<String, LanguageEntity> fullLang2Language =
				tags.stream().collect(Collectors.toMap(LanguageEntity::getFullLang, Function.identity()));

		List<Long> orderedLanguageIds = new ArrayList<>();
		for (String languageTag : fallbackChain) {
			LanguageEntity matchedTag = fullLang2Language.get(languageTag);
			if (matchedTag != null) {
				orderedLanguageIds.add(matchedTag.getId());
			}
		}

		if (orderedLanguageIds.isEmpty()) {
			return result;
		}

		// Batch fallback query by (languageIds IN list) AND (localizeCode IN
		// unresolved)
		List<LocalizeEntity> fallbackEntities = baseMapper.selectList(
				new LambdaQueryWrapper<LocalizeEntity>().in(LocalizeEntity::getLanguageId, orderedLanguageIds)
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

		// For each unresolved code, apply fallback in priority order
		Set<String> resolvedSet = new HashSet<>();
		for (Long langId : orderedLanguageIds) {
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
}