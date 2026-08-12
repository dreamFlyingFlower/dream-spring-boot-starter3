package dream.flying.flower.autoconfigure.email.convert;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import dream.flying.flower.autoconfigure.email.entity.EmailRecipientEntity;
import dream.flying.flower.autoconfigure.email.vo.EmailRecipientVO;
import dream.flying.flower.framework.web.convert.BaseConvert;

/**
 * 邮件发送收件人转换
 *
 * @author 飞花梦影
 * @date 2026-08-12 14:43:04
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmailRecipientConvert extends BaseConvert<EmailRecipientEntity, EmailRecipientVO> {

	EmailRecipientConvert INSTANCE = Mappers.getMapper(EmailRecipientConvert.class);
}