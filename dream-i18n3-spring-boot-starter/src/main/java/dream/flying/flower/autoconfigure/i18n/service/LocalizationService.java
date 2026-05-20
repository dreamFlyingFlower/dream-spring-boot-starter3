package dream.flying.flower.autoconfigure.i18n.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import dream.flying.flower.autoconfigure.i18n.entity.LocalizationEntity;
import dream.flying.flower.autoconfigure.i18n.query.LocalizationQuery;
import dream.flying.flower.autoconfigure.i18n.vo.LocalizationVO;
import dream.flying.flower.framework.mybatis.plus.service.BaseServices;

/**
 * I18n service class
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface LocalizationService extends BaseServices<LocalizationEntity, LocalizationVO, LocalizationQuery> {

	String getMessage(String langCode, String messageCode);

	String getMessage(String messageCode);

	Map<String, String> getAllMessages(String langCode);

	void clearCache(String langCode);

	void clearAllCache();

	String getDictName(String messageCode, Locale locale);

	String getDictItemName(String messageCode, Locale locale);

	Map<String, String> getDictI18nNames(List<String> messageCodes, Locale locale);

	Map<String, String> getDictItemI18nNames(List<String> messageCodes, Locale locale);
}