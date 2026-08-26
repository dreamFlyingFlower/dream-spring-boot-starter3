package dream.flying.flower.autoconfigure.localize.service.impl;

import dream.flying.flower.autoconfigure.localize.convert.LocalizeItemConvert;
import dream.flying.flower.autoconfigure.localize.entity.LocalizeItemEntity;
import dream.flying.flower.autoconfigure.localize.mapper.LocalizeItemMapper;
import dream.flying.flower.autoconfigure.localize.query.LocalizeItemQuery;
import dream.flying.flower.autoconfigure.localize.service.LocalizeItemService;
import dream.flying.flower.autoconfigure.localize.vo.LocalizeItemVO;
import dream.flying.flower.framework.mybatis.plus.service.impl.AbstractServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Localize item service implement
 *
 * @author 飞花梦影
 * @date 2026-05-20 10:43:03
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@RequiredArgsConstructor
public class LocalizeItemServiceImpl extends AbstractServiceImpl<LocalizeItemEntity, LocalizeItemVO, LocalizeItemQuery,
		LocalizeItemConvert, LocalizeItemMapper> implements LocalizeItemService {
}