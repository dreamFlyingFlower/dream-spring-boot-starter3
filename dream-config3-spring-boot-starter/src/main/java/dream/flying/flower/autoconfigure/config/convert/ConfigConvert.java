package dream.flying.flower.autoconfigure.config.convert;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import dream.flying.flower.autoconfigure.config.entity.ConfigEntity;
import dream.flying.flower.autoconfigure.config.vo.ConfigVO;
import dream.flying.flower.framework.web.convert.BaseConvert;

/**
 * Config entity and VO converter
 *
 * @author 飞花梦影
 * @date 2026-08-13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ConfigConvert extends BaseConvert<ConfigEntity, ConfigVO> {

	ConfigConvert INSTANCE = Mappers.getMapper(ConfigConvert.class);
}