package dream.flying.flower.autoconfigure.email.convert;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import dream.flying.flower.autoconfigure.email.entity.EmailSendLogEntity;
import dream.flying.flower.autoconfigure.email.vo.EmailSendLogVO;
import dream.flying.flower.framework.web.convert.BaseConvert;

/**
 * 邮件发送日志转换
 *
 * @author 飞花梦影
 * @date 2026-08-12 14:43:04
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmailSendLogConvert extends BaseConvert<EmailSendLogEntity, EmailSendLogVO> {

	EmailSendLogConvert INSTANCE = Mappers.getMapper(EmailSendLogConvert.class);
}