package dream.flying.flower.autoconfigure.dict.convert;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import dream.flying.flower.autoconfigure.dict.entity.DictEntity;
import dream.flying.flower.autoconfigure.dict.vo.DictVO;
import dream.flying.flower.framework.web.convert.BaseConvert;

/**
 * 字典表数据库实体与DTO互转
 * 
 * @author 飞花梦影
 * @date 2022-09-01 16:40:21
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DictConvert extends BaseConvert<DictEntity, DictVO> {

	DictConvert INSTANCE = Mappers.getMapper(DictConvert.class);

}