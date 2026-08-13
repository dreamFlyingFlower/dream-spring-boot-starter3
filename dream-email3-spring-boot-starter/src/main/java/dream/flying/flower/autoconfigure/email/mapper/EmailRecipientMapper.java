package dream.flying.flower.autoconfigure.email.mapper;

import org.apache.ibatis.annotations.Mapper;

import dream.flying.flower.autoconfigure.email.entity.EmailRecipientEntity;
import dream.flying.flower.autoconfigure.email.query.EmailRecipientQuery;
import dream.flying.flower.autoconfigure.email.vo.EmailRecipientVO;
import dream.flying.flower.framework.mybatis.plus.mapper.BaseMappers;

/**
 * 邮件发送收件人Mapper接口
 *
 * @author 飞花梦影
 * @date 2026-05-25 13:25:57
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper
public interface EmailRecipientMapper extends BaseMappers<EmailRecipientEntity, EmailRecipientVO, EmailRecipientQuery> {
}