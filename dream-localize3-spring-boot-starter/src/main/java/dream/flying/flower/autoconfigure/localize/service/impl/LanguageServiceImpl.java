package dream.flying.flower.autoconfigure.localize.service.impl;

import dream.flying.flower.autoconfigure.localize.convert.LanguageConvert;
import dream.flying.flower.autoconfigure.localize.entity.LanguageEntity;
import dream.flying.flower.autoconfigure.localize.mapper.LanguageMapper;
import dream.flying.flower.autoconfigure.localize.query.LanguageQuery;
import dream.flying.flower.autoconfigure.localize.service.LanguageService;
import dream.flying.flower.autoconfigure.localize.vo.LanguageVO;
import dream.flying.flower.framework.mybatis.plus.service.impl.AbstractServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Language service implement
 *
 * @author 飞花梦影
 * @date 2026-05-20 10:43:03
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@RequiredArgsConstructor
public class LanguageServiceImpl
		extends AbstractServiceImpl<LanguageEntity, LanguageVO, LanguageQuery, LanguageConvert, LanguageMapper>
		implements LanguageService {

}