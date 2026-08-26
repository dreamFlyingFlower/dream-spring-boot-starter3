package dream.flying.flower.autoconfigure.localize.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import dream.flying.flower.autoconfigure.localize.entity.LocalizeEntity;
import dream.flying.flower.autoconfigure.localize.query.LocalizeQuery;
import dream.flying.flower.autoconfigure.localize.vo.LocalizeVO;
import dream.flying.flower.framework.mybatis.plus.service.BaseServices;

/**
 * I18n service class
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface LocalizeService extends BaseServices<LocalizeEntity, LocalizeVO, LocalizeQuery> {

	String getMessage(String localizeCode);

	String getMessage(String localizeCode, String lang);

	String getMessage(String localizeCode, String lang, String namespace);

	Map<String, String> getAllMessages(String lang);

	void clearCache(String lang);

	void clearAllCache();

	String getDictName(String localizeCode, Locale locale);

	String getDictItemName(String localizeCode, Locale locale);

	Map<String, String> getDictI18nNames(List<String> localizeCodes, Locale locale);

	Map<String, String> getDictItemI18nNames(List<String> localizeCodes, Locale locale);

}