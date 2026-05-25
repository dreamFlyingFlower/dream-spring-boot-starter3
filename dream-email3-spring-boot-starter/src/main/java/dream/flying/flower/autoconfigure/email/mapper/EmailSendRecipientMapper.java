package dream.flying.flower.autoconfigure.email.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import dream.flying.flower.autoconfigure.email.entity.EmailSendRecipientEntity;

/**
 * Email send recipient Mapper interface
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
@Mapper
public interface EmailSendRecipientMapper extends BaseMapper<EmailSendRecipientEntity> {
}
