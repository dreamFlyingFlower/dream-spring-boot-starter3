package dream.flying.flower.autoconfigure.localize.service;

import dream.flying.flower.autoconfigure.localize.entity.LocalizeItemEntity;
import dream.flying.flower.autoconfigure.localize.query.LocalizeItemQuery;
import dream.flying.flower.autoconfigure.localize.vo.LocalizeItemVO;
import dream.flying.flower.framework.mybatis.plus.service.BaseServices;

/**
 * Localize item service class
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface LocalizeItemService extends BaseServices<LocalizeItemEntity, LocalizeItemVO, LocalizeItemQuery> {
}