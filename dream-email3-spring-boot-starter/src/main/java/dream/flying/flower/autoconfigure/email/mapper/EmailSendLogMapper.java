package dream.flying.flower.autoconfigure.email.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import dream.flying.flower.autoconfigure.email.entity.EmailSendLogEntity;

/**
 * Email send log Mapper interface
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
@Mapper
public interface EmailSendLogMapper extends BaseMapper<EmailSendLogEntity> {
}
