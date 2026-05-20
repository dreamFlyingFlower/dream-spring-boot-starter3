package dream.flying.flower.autoconfigure.dict.service;

import java.util.List;

import dream.flying.flower.autoconfigure.dict.entity.DictEntity;
import dream.flying.flower.autoconfigure.dict.query.DictQuery;
import dream.flying.flower.autoconfigure.dict.vo.DictVO;
import dream.flying.flower.framework.mybatis.plus.service.BaseServices;

/**
 * Dict service class
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public interface DictService extends BaseServices<DictEntity, DictVO, DictQuery> {

	List<DictEntity> listByStatus(Integer status);

	DictEntity getByDictCode(String dictCode);
}