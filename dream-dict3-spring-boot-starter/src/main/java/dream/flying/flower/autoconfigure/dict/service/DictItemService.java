package dream.flying.flower.autoconfigure.dict.service;

import java.util.List;

import dream.flying.flower.autoconfigure.dict.entity.DictItemEntity;
import dream.flying.flower.autoconfigure.dict.query.DictItemQuery;
import dream.flying.flower.autoconfigure.dict.vo.DictItemVO;
import dream.flying.flower.framework.mybatis.plus.service.BaseServices;

/**
 * Dict item service class
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public interface DictItemService extends BaseServices<DictItemEntity, DictItemVO, DictItemQuery> {

	List<DictItemEntity> listByDictId(Long dictId);

	List<DictItemEntity> listByDictIdAndStatus(Long dictId, Integer status);
}