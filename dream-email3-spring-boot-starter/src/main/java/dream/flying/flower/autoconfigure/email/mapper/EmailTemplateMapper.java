package dream.flying.flower.autoconfigure.email.mapper;

import org.apache.ibatis.annotations.Mapper;

import dream.flying.flower.autoconfigure.email.entity.EmailTemplateEntity;
import dream.flying.flower.autoconfigure.email.query.EmailTemplateQuery;
import dream.flying.flower.autoconfigure.email.vo.EmailTemplateVO;
import dream.flying.flower.framework.mybatis.plus.mapper.BaseMappers;

/**
 * Email template Mapper interface
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
@Mapper
public interface EmailTemplateMapper extends BaseMappers<EmailTemplateEntity, EmailTemplateVO, EmailTemplateQuery> {
}