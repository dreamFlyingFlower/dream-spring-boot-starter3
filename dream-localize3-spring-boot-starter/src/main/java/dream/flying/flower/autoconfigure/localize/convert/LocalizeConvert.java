package dream.flying.flower.autoconfigure.localize.convert;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import dream.flying.flower.autoconfigure.localize.entity.LocalizeEntity;
import dream.flying.flower.autoconfigure.localize.vo.LocalizeVO;
import dream.flying.flower.framework.web.convert.BaseConvert;

/**
 * 国际化
 *
 * @author 飞花梦影
 * @date 2026-05-20 10:43:03
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocalizeConvert extends BaseConvert<LocalizeEntity, LocalizeVO> {

	LocalizeConvert INSTANCE = Mappers.getMapper(LocalizeConvert.class);
}