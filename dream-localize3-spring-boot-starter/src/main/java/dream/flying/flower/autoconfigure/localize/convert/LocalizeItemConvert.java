package dream.flying.flower.autoconfigure.localize.convert;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import dream.flying.flower.autoconfigure.localize.entity.LocalizeItemEntity;
import dream.flying.flower.autoconfigure.localize.vo.LocalizeItemVO;
import dream.flying.flower.framework.web.convert.BaseConvert;

/**
 * 国际化资源明细
 *
 * @author 飞花梦影
 * @date 2026-05-20 10:43:03
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocalizeItemConvert extends BaseConvert<LocalizeItemEntity, LocalizeItemVO> {

	LocalizeItemConvert INSTANCE = Mappers.getMapper(LocalizeItemConvert.class);
}