package dream.flying.flower.autoconfigure.i18n.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import dream.flying.flower.autoconfigure.i18n.entity.LocalizationEntity;
import dream.flying.flower.autoconfigure.i18n.mapper.LocalizationMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * I18n service class
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
public class LocalizationService {

	@Autowired
	private LocalizationMapper localizationMapper;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	private static final String I18N_CACHE_PREFIX = "i18n:message:";

	private static final String I18N_ALL_CACHE_PREFIX = "i18n:all:";

	private long cacheExpireHours = 24;

	public void setCacheExpireHours(long cacheExpireHours) {
		this.cacheExpireHours = cacheExpireHours;
	}

	public String getMessage(String langCode, String messageCode) {
		String cacheKey = I18N_CACHE_PREFIX + langCode + ":" + messageCode;

		// Try to get from cache first
		try {
			String cachedMessage = redisTemplate.opsForValue().get(cacheKey);
			if (cachedMessage != null) {
				return cachedMessage;
			}
		} catch (Exception e) {
			// Redis connection failed, query directly from database
		}

		// Query from database
		List<LocalizationEntity> messages = localizationMapper
				.selectList(new LambdaQueryWrapper<LocalizationEntity>().eq(LocalizationEntity::getLang, langCode)
						.eq(LocalizationEntity::getMessageCode, messageCode)
						.eq(LocalizationEntity::getDeleted, 0));

		if (!messages.isEmpty()) {
			String messageContent = messages.get(0).getMessageContent();
			// Put into cache
			try {
				redisTemplate.opsForValue().set(cacheKey, messageContent, cacheExpireHours, TimeUnit.HOURS);
			} catch (Exception e) {
				// Redis connection failed, ignore cache operation
			}
			return messageContent;
		}
		return null;
	}

	public String getMessage(String messageCode) {
		return getMessage("zh_CN", messageCode);
	}

	public Map<String, String> getAllMessages(String langCode) {
		String cacheKey = I18N_ALL_CACHE_PREFIX + langCode;

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
		}

		// Query from database
		List<LocalizationEntity> messages = localizationMapper
				.selectList(new LambdaQueryWrapper<LocalizationEntity>().eq(LocalizationEntity::getLang, langCode)
						.eq(LocalizationEntity::getDeleted, 0));

		Map<String, String> messageMap = messages.stream()
				.collect(Collectors.toMap(LocalizationEntity::getMessageCode, LocalizationEntity::getMessageContent));

		// Put into cache
		try {
			if (!messageMap.isEmpty()) {
				redisTemplate.opsForHash().putAll(cacheKey, messageMap);
				redisTemplate.expire(cacheKey, cacheExpireHours, TimeUnit.HOURS);
			}
		} catch (Exception e) {
			// Redis connection failed, ignore cache operation
		}

		return messageMap;
	}

	/**
	 * Clear cache for specified language
	 */
	public void clearCache(String langCode) {
		try {
			String allCacheKey = I18N_ALL_CACHE_PREFIX + langCode;
			redisTemplate.delete(allCacheKey);

			// Clear all message caches for this language
			String pattern = I18N_CACHE_PREFIX + langCode + ":*";
			redisTemplate.delete(redisTemplate.keys(pattern));
		} catch (Exception e) {
			// Redis connection failed, ignore cache operation
		}
	}

	/**
	 * Clear all caches
	 */
	public void clearAllCache() {
		try {
			redisTemplate.delete(redisTemplate.keys(I18N_CACHE_PREFIX + "*"));
			redisTemplate.delete(redisTemplate.keys(I18N_ALL_CACHE_PREFIX + "*"));
		} catch (Exception e) {
			// Redis connection failed, ignore cache operation
		}
	}

	/**
	 * Get internationalized dict name
	 *
	 * @param messageCode message code
	 * @param locale locale
	 * @return internationalized dict name, or null if not exists
	 */
	public String getDictName(String messageCode, Locale locale) {
		if (messageCode == null || messageCode.isEmpty()) {
			return null;
		}
		String langCode = getLangCode(locale);
		return getMessage(langCode, messageCode);
	}

	/**
	 * Get internationalized dict item name
	 *
	 * @param messageCode message code
	 * @param locale locale
	 * @return internationalized dict item name, or null if not exists
	 */
	public String getDictItemName(String messageCode, Locale locale) {
		if (messageCode == null || messageCode.isEmpty()) {
			return null;
		}
		String langCode = getLangCode(locale);
		return getMessage(langCode, messageCode);
	}

	/**
	 * Batch get dict internationalized content
	 *
	 * @param messageCodes message codes list
	 * @param locale locale
	 * @return Map<messageCode, internationalized content>
	 */
	public Map<String, String> getDictI18nNames(List<String> messageCodes, Locale locale) {
		if (messageCodes == null || messageCodes.isEmpty()) {
			return Map.of();
		}
		String langCode = getLangCode(locale);
		return getAllMessages(langCode).entrySet()
				.stream()
				.filter(entry -> messageCodes.contains(entry.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	/**
	 * Batch get dict item internationalized content
	 *
	 * @param messageCodes message codes list
	 * @param locale locale
	 * @return Map<messageCode, internationalized content>
	 */
	public Map<String, String> getDictItemI18nNames(List<String> messageCodes, Locale locale) {
		if (messageCodes == null || messageCodes.isEmpty()) {
			return Map.of();
		}
		String langCode = getLangCode(locale);
		return getAllMessages(langCode).entrySet()
				.stream()
				.filter(entry -> messageCodes.contains(entry.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	/**
	 * Get language code
	 *
	 * @param locale locale
	 * @return language code (e.g., zh_CN, en_US)
	 */
	private String getLangCode(Locale locale) {
		if (locale == null) {
			return "zh_CN";
		}
		return locale.getLanguage() + "_" + locale.getCountry();
	}
}
