package dream.flying.flower.autoconfigure.localize.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import dream.flying.flower.autoconfigure.localize.constant.ConstLocalize;
import dream.flying.flower.autoconfigure.localize.convert.LocalizeConvert;
import dream.flying.flower.autoconfigure.localize.entity.LocalizeEntity;
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
 * 
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

	private final RedisTemplate<String, String> redisTemplate;

	private final DreamLocalizeProperties dreamLocalizeProperties;

	@Override
	public String getMessage(String localizeCode) {
		return getMessage(localizeCode, getLang());
	}

	@Override
	public String getMessage(String localizeCode, String lang) {
		String cacheKey = ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME, ConstLocalize.MODULE_NAME,
				ConstLocalize.I18N_CACHE_PREFIX, lang, localizeCode);

		// Try to get from cache first
		try {
			String cachedMessage = redisTemplate.opsForValue().get(cacheKey);
			if (cachedMessage != null) {
				return cachedMessage;
			}
		} catch (Exception e) {
			// Redis connection failed, query directly from database
			log.info("Localize cache does not exist or query failed from redis,retrieving from databse");
		}

		// Query from database
		List<LocalizeEntity> messages = list(lqw -> lqw.eq(LocalizeEntity::getLang, lang)
				.eq(LocalizeEntity::getLocalizeCode, localizeCode)
				.eq(LocalizeEntity::getDeleted, 0));

		if (!messages.isEmpty()) {
			String localizeContent = messages.get(0).getLocalizeMessage();
			// Put into cache
			try {
				redisTemplate.opsForValue()
						.set(cacheKey, localizeContent, dreamLocalizeProperties.getCacheExpireHours(), TimeUnit.HOURS);
			} catch (Exception e) {
				// Redis connection failed, ignore cache operation
				e.printStackTrace();
			}
			return localizeContent;
		}
		return null;
	}

	@Override
	public Map<String, String> getAllMessages(String lang) {
		String cacheKey = ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME, ConstLocalize.MODULE_NAME,
				ConstLocalize.I18N_ALL_CACHE_PREFIX, lang);

		// Try to get from cache first
		try {
			Map<Object, Object> cachedMap = redisTemplate.opsForHash().entries(cacheKey);
			if (!cachedMap.isEmpty()) {
				return cachedMap.entrySet()
						.stream()
						.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
			}
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
	public void clearCache(String lang) {
		try {
			String allCacheKey = ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME, ConstLocalize.MODULE_NAME,
					ConstLocalize.I18N_ALL_CACHE_PREFIX, lang);
			redisTemplate.delete(allCacheKey);

			// Clear all message caches for this language
			String pattern = ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME, ConstLocalize.MODULE_NAME,
					ConstLocalize.I18N_CACHE_PREFIX, lang, "*");
			redisTemplate.delete(redisTemplate.keys(pattern));
		} catch (Exception e) {
			// Redis connection failed, ignore cache operation
			e.printStackTrace();
		}
	}

	/**
	 * Clear all caches
	 */
	@Override
	public void clearAllCache() {
		try {
			redisTemplate.delete(redisTemplate.keys(ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME,
					ConstLocalize.MODULE_NAME, ConstLocalize.I18N_CACHE_PREFIX, "*")));

			redisTemplate.delete(redisTemplate.keys(ConstCache.buildRedisKey(ConstStarter.PROJECT_NAME,
					ConstLocalize.MODULE_NAME, ConstLocalize.I18N_ALL_CACHE_PREFIX, "*")));
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
		String lang = getLang(locale);
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
		String lang = getLang(locale);
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
		String lang = getLang(locale);
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
		String lang = getLang(locale);
		return getAllMessages(lang).entrySet()
				.stream()
				.filter(entry -> localizeCodes.contains(entry.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	/**
	 * Get language code
	 *
	 * @param locale locale
	 * @return language code (e.g., zh_CN, en_US)
	 */
	private String getLang() {
		return Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry();
	}

	/**
	 * Get language code
	 *
	 * @param locale locale
	 * @return language code (e.g., zh_CN, en_US)
	 */
	private String getLang(Locale locale) {
		if (locale == null) {
			locale = Locale.getDefault();
		}
		return locale.getLanguage() + "_" + locale.getCountry();
	}
}