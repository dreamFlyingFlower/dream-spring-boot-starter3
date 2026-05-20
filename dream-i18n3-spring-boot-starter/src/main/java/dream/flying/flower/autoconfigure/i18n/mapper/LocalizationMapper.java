package dream.flying.flower.autoconfigure.i18n.mapper;

import org.apache.ibatis.annotations.Mapper;

import dream.flying.flower.autoconfigure.i18n.entity.LocalizationEntity;
import dream.flying.flower.autoconfigure.i18n.query.LocalizationQuery;
import dream.flying.flower.autoconfigure.i18n.vo.LocalizationVO;
import dream.flying.flower.framework.mybatis.plus.mapper.BaseMappers;

/**
 * Localization Mapper interface
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper
public interface LocalizationMapper extends BaseMappers<LocalizationEntity, LocalizationVO, LocalizationQuery> {
}