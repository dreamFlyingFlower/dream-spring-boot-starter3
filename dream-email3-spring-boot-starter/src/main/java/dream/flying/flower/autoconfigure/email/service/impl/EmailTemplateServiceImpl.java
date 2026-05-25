package dream.flying.flower.autoconfigure.email.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import dream.flying.flower.autoconfigure.email.entity.EmailTemplateEntity;
import dream.flying.flower.autoconfigure.email.mapper.EmailTemplateMapper;
import dream.flying.flower.autoconfigure.email.service.EmailTemplateService;
import lombok.extern.slf4j.Slf4j;

/**
 * Email template service implementation
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
@Slf4j
@Service
public class EmailTemplateServiceImpl extends ServiceImpl<EmailTemplateMapper, EmailTemplateEntity>
		implements EmailTemplateService {

	@Autowired
	private EmailTemplateMapper emailTemplateMapper;

	@Override
	public void saveTemplate(EmailTemplateEntity template) {
		emailTemplateMapper.insert(template);
		log.debug("Email template saved: code={}", template.getTemplateCode());
	}

	@Override
	public void updateTemplate(EmailTemplateEntity template) {
		emailTemplateMapper.updateById(template);
		log.debug("Email template updated: code={}", template.getTemplateCode());
	}

	@Override
	public void deleteTemplate(Long id) {
		emailTemplateMapper.deleteById(id);
		log.debug("Email template deleted: id={}", id);
	}

	@Override
	public EmailTemplateEntity getTemplateById(Long id) {
		return emailTemplateMapper.selectById(id);
	}

	@Override
	public EmailTemplateEntity getTemplateByCode(String templateCode) {
		return emailTemplateMapper.selectOne(new LambdaQueryWrapper<EmailTemplateEntity>()
				.eq(EmailTemplateEntity::getTemplateCode, templateCode)
				.eq(EmailTemplateEntity::getDeleted, 0));
	}

	@Override
	public List<EmailTemplateEntity> listEnabledTemplates() {
		return emailTemplateMapper.selectList(new LambdaQueryWrapper<EmailTemplateEntity>()
				.eq(EmailTemplateEntity::getStatus, 1)
				.eq(EmailTemplateEntity::getDeleted, 0)
				.orderByDesc(EmailTemplateEntity::getCreatedAt));
	}

	@Override
	public List<EmailTemplateEntity> listAllTemplates() {
		return emailTemplateMapper.selectList(new LambdaQueryWrapper<EmailTemplateEntity>()
				.eq(EmailTemplateEntity::getDeleted, 0)
				.orderByDesc(EmailTemplateEntity::getCreatedAt));
	}

	@Override
	public void enableTemplate(Long id) {
		EmailTemplateEntity template = emailTemplateMapper.selectById(id);
		if (template != null) {
			template.setStatus(1);
			emailTemplateMapper.updateById(template);
			log.debug("Email template enabled: id={}", id);
		}
	}

	@Override
	public void disableTemplate(Long id) {
		EmailTemplateEntity template = emailTemplateMapper.selectById(id);
		if (template != null) {
			template.setStatus(0);
			emailTemplateMapper.updateById(template);
			log.debug("Email template disabled: id={}", id);
		}
	}
}
