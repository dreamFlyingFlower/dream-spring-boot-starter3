package dream.flying.flower.autoconfigure.email.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import dream.flying.flower.autoconfigure.email.convert.EmailRecipientConvert;
import dream.flying.flower.autoconfigure.email.entity.EmailRecipientEntity;
import dream.flying.flower.autoconfigure.email.mapper.EmailRecipientMapper;
import dream.flying.flower.autoconfigure.email.query.EmailRecipientQuery;
import dream.flying.flower.autoconfigure.email.service.EmailRecipientService;
import dream.flying.flower.autoconfigure.email.vo.EmailRecipientVO;
import dream.flying.flower.framework.mybatis.plus.service.impl.AbstractServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 邮件发送收件人服务实现
 *
 * @author 飞花梦影
 * @date 2026-05-25 13:25:57
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailRecipientServiceImpl extends AbstractServiceImpl<EmailRecipientEntity, EmailRecipientVO,
		EmailRecipientQuery, EmailRecipientConvert, EmailRecipientMapper> implements EmailRecipientService {

	@Override
	public List<EmailRecipientEntity> findBySendLogId(Long sendLogId) {
		return list(new LambdaQueryWrapper<EmailRecipientEntity>().eq(EmailRecipientEntity::getSendLogId, sendLogId));
	}
}