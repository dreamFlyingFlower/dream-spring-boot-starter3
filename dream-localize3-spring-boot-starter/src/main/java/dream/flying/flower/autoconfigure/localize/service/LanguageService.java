package dream.flying.flower.autoconfigure.localize.service;

import dream.flying.flower.autoconfigure.localize.entity.LanguageEntity;
import dream.flying.flower.autoconfigure.localize.query.LanguageQuery;
import dream.flying.flower.autoconfigure.localize.vo.LanguageVO;
import dream.flying.flower.framework.mybatis.plus.service.BaseServices;

/**
 * Language service class
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface LanguageService extends BaseServices<LanguageEntity, LanguageVO, LanguageQuery> {

}