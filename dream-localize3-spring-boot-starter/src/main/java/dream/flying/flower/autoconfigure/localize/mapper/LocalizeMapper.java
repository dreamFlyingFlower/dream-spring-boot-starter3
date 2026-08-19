package dream.flying.flower.autoconfigure.localize.mapper;

import org.apache.ibatis.annotations.Mapper;

import dream.flying.flower.autoconfigure.localize.entity.LocalizeEntity;
import dream.flying.flower.autoconfigure.localize.query.LocalizeQuery;
import dream.flying.flower.autoconfigure.localize.vo.LocalizeVO;
import dream.flying.flower.framework.mybatis.plus.mapper.BaseMappers;

/**
 * Localization Mapper interface
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper
public interface LocalizeMapper extends BaseMappers<LocalizeEntity, LocalizeVO, LocalizeQuery> {
}