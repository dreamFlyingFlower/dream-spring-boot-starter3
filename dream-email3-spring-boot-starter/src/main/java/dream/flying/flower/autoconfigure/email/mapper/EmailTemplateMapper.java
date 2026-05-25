package dream.flying.flower.autoconfigure.email.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import dream.flying.flower.autoconfigure.email.entity.EmailTemplateEntity;

/**
 * Email template Mapper interface
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
@Mapper
public interface EmailTemplateMapper extends BaseMapper<EmailTemplateEntity> {
}
