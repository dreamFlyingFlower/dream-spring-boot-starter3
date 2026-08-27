package dream.flying.flower.autoconfigure.localize.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import dream.flying.flower.ConstSymbol;
import dream.flying.flower.autoconfigure.localize.cache.LocalizeCache;
import dream.flying.flower.autoconfigure.localize.constant.ConstLocalize;
import dream.flying.flower.autoconfigure.localize.convert.LocalizeConvert;
import dream.flying.flower.autoconfigure.localize.entity.LanguageEntity;
import dream.flying.flower.autoconfigure.localize.entity.LocalizeEntity;
import dream.flying.flower.autoconfigure.localize.entity.LocalizeItemEntity;
import dream.flying.flower.autoconfigure.localize.helpers.LocalizeHelpers;
import dream.flying.flower.autoconfigure.localize.mapper.LanguageMapper;
import dream.flying.flower.autoconfigure.localize.mapper.LocalizeItemMapper;
import dream.flying.flower.autoconfigure.localize.mapper.LocalizeMapper;
import dream.flying.flower.autoconfigure.localize.properties.DreamLocalizeProperties;
import dream.flying.flower.autoconfigure.localize.query.LocalizeQuery;
import dream.flying.flower.autoconfigure.localize.service.LocalizeService;
import dream.flying.flower.autoconfigure.localize.vo.LocalizeVO;
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

	private final DreamLocalizeProperties dreamLocalizeProperties;

	private final LocalizeCache localizeCache;

	private final LanguageMapper languageMapper;

	private final LocalizeMapper localizeMapper;

	private final LocalizeItemMapper localizeItemMapper;

	/**
	 * 解析完整的
	 * @param localizeCode
	 * @return
	 */
	private String parseCode(String localizeCode) {
		int firstDot = localizeCode.indexOf(ConstSymbol.CHAR_DOT);
		if (firstDot > 0) {
			String namespace = localizeCode.substring(0, firstDot);
			String key = localizeCode.substring(firstDot + 1);
			return new String[] { namespace, key };
		}
		return new String[] { dreamLocalizeProperties.getDefaultNamespace(), localizeCode };
	}

	@Override
	public String getMessage(String localizeCode) {
		return getMessage(localizeCode, LocalizeHelpers.getLang());
	}

	@Override
	public String getMessage(String localizeCode, String lang) {
		return getMessage(localizeCode, lang, dreamLocalizeProperties.getDefaultNamespace());
	}

	@Override
	public String getMessage(String localizeCode, String lang, String namespace) {
		String cacheKey = buildCacheKey(localizeCode, lang, namespace);

		// Try to get from cache first
		try {
			String cached = localizeCache.get(cacheKey);
			if (cached != null) {
				return cached;
			}
		} catch (Exception e) {
			// Redis connection failed, query directly from database
			log.info("Localize cache does not exist or query failed from redis,retrieving from databse");
		}

		// Query from database
		try {
			String result = getContent(namespace, localizeCode, lang);
			if (result != null) {
				localizeCache.put(cacheKey, result, 3600, TimeUnit.SECONDS);
			}
			return result;
		} catch (Exception e) {
			// Redis connection failed, query directly from database
			log.info("Localize cache does not exist or query failed from redis,retrieving from databse");
		}
		return null;
	}

	@Override
	public Map<String, String> getAllMessages(String lang) {
		String cacheKey = buildCacheKey(lang);

		// Try to get from cache first
		try {
			return localizeCache.getMap(cacheKey);
		} catch (Exception e) {
			// Redis connection failed, query directly from database
			e.printStackTrace();
		}

		// Query from database
		List<LocalizeEntity> messages = list(new LambdaQueryWrapper<LocalizeEntity>().eq(LocalizeEntity::getLang, lang)
				.eq(LocalizeEntity::getDeleted, 0));

		Map<String, String> messageMap = messages.stream()
				.collect(Collectors.toMap(LocalizeEntity::getLocalizeCode, LocalizeEntity::getLocalizeMessage));

		// Put into cache
		try {
			if (!messageMap.isEmpty()) {
				redisTemplate.opsForHash().putAll(cacheKey, messageMap);
				redisTemplate.expire(cacheKey, dreamLocalizeProperties.getCacheExpireHours(), TimeUnit.HOURS);
			}
		} catch (Exception e) {
			// Redis connection failed, ignore cache operation
			e.printStackTrace();
		}

		return messageMap;
	}

	@Override
	public void clearCache() {
		try {
			localizeCache.clear();
		} catch (Exception e) {
			// Redis connection failed, ignore cache operation
			e.printStackTrace();
		}
	}

	@Override
	public void evictCache(String lang) {
		try {
			// Clear all message caches for this language
			String pattern = ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME, ConstLocalize.MODULE_NAME,
					dreamLocalizeProperties.getCachePrefix(), lang, "*");
			redisTemplate.delete(redisTemplate.keys(pattern));

			localizeCache.evict(keys);

		} catch (Exception e) {
			// Redis connection failed, ignore cache operation
			e.printStackTrace();
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

	public Map<String, String> getTranslations(String namespace, List<String> keys, String languageTag) {
		List<String> cacheKeys =
				keys.stream().map(key -> buildCacheKey(namespace, key, languageTag)).collect(Collectors.toList());

		Map<String, String> cached = localizeCache.get(cacheKeys);
		Map<String, String> result = new LinkedHashMap<>();
		List<String> missKeys = new ArrayList<>();

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String cacheKey = cacheKeys.get(i);
			if (cached.containsKey(cacheKey)) {
				result.put(key, cached.get(cacheKey));
			} else {
				missKeys.add(key);
			}
		}

		if (!missKeys.isEmpty()) {
			Map<String, String> dbResult = getTranslationsFromDB(namespace, missKeys, languageTag);
			Map<String, String> cacheEntries = new HashMap<>();
			for (Map.Entry<String, String> entry : dbResult.entrySet()) {
				String cacheKey = buildCacheKey(namespace, entry.getKey(), languageTag);
				cacheEntries.put(cacheKey, entry.getValue());
			}
			if (!cacheEntries.isEmpty()) {
				localizeCache.put(cacheEntries, 3600, TimeUnit.SECONDS);
			}
			result.putAll(dbResult);
		}

		return result;
	}

	private String getContent(String namespace, String localizeCode, String languageTag) {
		LocalizeEntity resource = localizeMapper
				.selectOne(new LambdaQueryWrapper<LocalizeEntity>().eq(LocalizeEntity::getLocalizeCode, localizeCode)
						.eq(LocalizeEntity::getNamespace, namespace));

		if (resource == null) {
			log.warn("Resource not found: {} in namespace {}", localizeCode, namespace);
			return localizeCode;
		}

		List<String> fallbackChain = LocalizeHelpers.buildFallback(languageTag);

		List<LanguageEntity> tags = languageMapper.selectList(new LambdaQueryWrapper<LanguageEntity>().in(
				LanguageEntity::getLang,
				fallbackChain.stream().map(t -> LocalizeHelpers.parse(t).getLanguage()).collect(Collectors.toList())));

		Map<String, LanguageEntity> tagMap = tags.stream()
				.collect(Collectors.toMap(
						t -> LocalizeHelpers.toStandard(t.getLang(), t.getScript(), t.getCountry(), t.getVariant()),
						t -> t));

		for (String tag : fallbackChain) {
			String normalizedTag = tag.replace('_', '-');
			normalizedTag = normalizedTag.replaceAll("-+", "-");
			LanguageEntity matchedTag = tagMap.get(normalizedTag);
			if (matchedTag != null) {
				LocalizeItemEntity item = localizeItemMapper.selectOne(new LambdaQueryWrapper<LocalizeItemEntity>()
						.eq(LocalizeItemEntity::getLocalizeId, resource.getId())
						.eq(LocalizeItemEntity::getLanguageId, matchedTag.getId()));
				if (null != item && StrHelper.isNotBlank(item.getContent())) {
					return item.getContent();
				}
			}
		}

		return resource.getDefaultValue() != null ? resource.getDefaultValue() : localizeCode;
	}

	private Map<String, String> getTranslationsFromDB(String namespace, List<String> keys, String lang) {
		Map<String, String> result = new LinkedHashMap<>();
		for (String key : keys) {
			result.put(key, getContent(namespace, key, lang));
		}
		return result;
	}

	private String buildCacheKey(String lang) {
		String formatLang = LocalizeHelpers.parse(lang).getLanguage();

		return ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME, ConstLocalize.MODULE_NAME,
				dreamLocalizeProperties.getDefaultNamespace(), formatLang, "*");
	}

	private String buildCacheKey(String localizeCode, String lang, String namespace) {
		String formatLang = LocalizeHelpers.parse(lang).getLanguage();

		return ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME, ConstLocalize.MODULE_NAME, namespace, formatLang,
				localizeCode);
	}
}