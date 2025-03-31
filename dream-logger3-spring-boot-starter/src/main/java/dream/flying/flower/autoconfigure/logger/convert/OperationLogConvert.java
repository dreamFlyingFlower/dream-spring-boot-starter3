package dream.flying.flower.autoconfigure.logger.convert;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import dream.flying.flower.autoconfigure.logger.entity.OperationLogEntity;
import dream.flying.flower.autoconfigure.logger.vo.OperationLogVO;
import dream.flying.flower.framework.web.convert.BaseConvert;

/**
 * 日志转换类
 *
 * @author 飞花梦影
 * @date 2025-03-29 11:40:53
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OperationLogConvert extends BaseConvert<OperationLogEntity, OperationLogVO> {

	OperationLogConvert INSTANCE = Mappers.getMapper(OperationLogConvert.class);
}