package dream.flying.flower.autoconfigure.email.convert;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import dream.flying.flower.autoconfigure.email.entity.EmailTemplateEntity;
import dream.flying.flower.autoconfigure.email.vo.EmailTemplateVO;
import dream.flying.flower.framework.web.convert.BaseConvert;

/**
 * Email template convert
 *
 * @author 飞花梦影
 * @date 2026-08-12 14:43:04
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmailTemplateConvert extends BaseConvert<EmailTemplateEntity, EmailTemplateVO> {

	EmailTemplateConvert INSTANCE = Mappers.getMapper(EmailTemplateConvert.class);
}