package dream.flying.flower.autoconfigure.dict.mapper;

import org.apache.ibatis.annotations.Mapper;

import dream.flying.flower.autoconfigure.dict.entity.DictEntity;
import dream.flying.flower.autoconfigure.dict.query.DictQuery;
import dream.flying.flower.autoconfigure.dict.vo.DictVO;
import dream.flying.flower.framework.mybatis.plus.mapper.BaseMappers;

/**
 * Dict Mapper interface
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Mapper
public interface DictMapper extends BaseMappers<DictEntity, DictVO, DictQuery> {
}