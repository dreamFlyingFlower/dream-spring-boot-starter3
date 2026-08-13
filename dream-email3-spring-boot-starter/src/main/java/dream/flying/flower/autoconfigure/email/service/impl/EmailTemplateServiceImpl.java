package dream.flying.flower.autoconfigure.email.service.impl;

import org.springframework.stereotype.Service;

import dream.flying.flower.autoconfigure.email.convert.EmailTemplateConvert;
import dream.flying.flower.autoconfigure.email.entity.EmailTemplateEntity;
import dream.flying.flower.autoconfigure.email.mapper.EmailTemplateMapper;
import dream.flying.flower.autoconfigure.email.query.EmailTemplateQuery;
import dream.flying.flower.autoconfigure.email.service.EmailTemplateService;
import dream.flying.flower.autoconfigure.email.vo.EmailTemplateVO;
import dream.flying.flower.framework.mybatis.plus.service.impl.AbstractServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 邮件模板服务实现
 *
 * @author 飞花梦影
 * @date 2026-05-25 13:25:57
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailTemplateServiceImpl extends AbstractServiceImpl<EmailTemplateEntity, EmailTemplateVO,
		EmailTemplateQuery, EmailTemplateConvert, EmailTemplateMapper> implements EmailTemplateService {

	@Override
	public EmailTemplateVO getByCode(String templateCode) {
		EmailTemplateEntity entity = listOne(wrapper -> wrapper.eq(EmailTemplateEntity::getTemplateCode, templateCode));
		return baseConvert.convertt(entity);
	}

	@Override
	public boolean toggleEnable(Long id, Integer status) {
		return updateById(EmailTemplateEntity.builder().id(id).status(status).build());
	}
}