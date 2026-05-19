package dream.flying.flower.autoconfigure.dict.mapper;

import org.apache.ibatis.annotations.Mapper;

import dream.flying.flower.autoconfigure.dict.entity.DictItemEntity;
import dream.flying.flower.autoconfigure.dict.query.DictItemQuery;
import dream.flying.flower.autoconfigure.dict.vo.DictItemVO;
import dream.flying.flower.framework.mybatis.plus.mapper.BaseMappers;

/**
 * Dict item Mapper interface
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Mapper
public interface DictItemMapper extends BaseMappers<DictItemEntity, DictItemVO, DictItemQuery> {
}