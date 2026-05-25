package dream.flying.flower.autoconfigure.email.service.impl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import dream.flying.flower.autoconfigure.email.entity.EmailSendLogEntity;
import dream.flying.flower.autoconfigure.email.entity.EmailSendRecipientEntity;
import dream.flying.flower.autoconfigure.email.entity.EmailTemplateEntity;
import dream.flying.flower.autoconfigure.email.enums.RecipientType;
import dream.flying.flower.autoconfigure.email.enums.SendStatus;
import dream.flying.flower.autoconfigure.email.mapper.EmailTemplateMapper;
import dream.flying.flower.autoconfigure.email.properties.EmailProperties;
import dream.flying.flower.autoconfigure.email.service.EmailSendLogService;
import dream.flying.flower.autoconfigure.email.service.EmailSendRecipientService;
import dream.flying.flower.autoconfigure.email.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Email service implementation
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private TemplateEngine templateEngine;

	@Autowired
	private EmailTemplateMapper templateMapper;

	@Autowired
	private EmailProperties emailProperties;

	@Autowired
	private EmailSendLogService emailSendLogService;

	@Autowired
	private EmailSendRecipientService emailSendRecipientService;

	@Override
	public void sendEmail(String toEmail, String templateCode, Map<String, Object> variables) {
		sendEmail(Collections.singletonList(toEmail), null, null, templateCode, variables);
	}

	@Override
	public void sendEmailWithAttachments(String toEmail, String templateCode, Map<String, Object> variables,
			FileSystemResource... attachments) {
		List<String> toEmails = Collections.singletonList(toEmail);
		sendEmailWithRecipients(toEmails, null, null, templateCode, variables, attachments);
	}

	@Override
	public void sendEmail(List<String> toEmails, List<String> ccEmails, List<String> bccEmails, String templateCode,
			Map<String, Object> variables) {
		sendEmailWithRecipients(toEmails, ccEmails, bccEmails, templateCode, variables, null);
	}

	private void sendEmailWithRecipients(List<String> toEmails, List<String> ccEmails, List<String> bccEmails,
			String templateCode, Map<String, Object> variables, FileSystemResource[] attachments) {
		EmailTemplateEntity template = getTemplateByCode(templateCode);
		if (template == null) {
			throw new IllegalArgumentException("Email template not found: " + templateCode);
		}

		int attachmentCount = attachments != null ? attachments.length : 0;

		// Create send log
		EmailSendLogEntity sendLog = EmailSendLogEntity.builder()
				.templateCode(template.getTemplateCode())
				.subject(template.getSubject())
				.fromEmail(template.getFromEmail())
				.fromName(template.getFromName())
				.sendStatus(SendStatus.PENDING.getCode())
				.attachmentCount(attachmentCount)
				.build();
		emailSendLogService.saveLog(sendLog);

		// Save recipients
		List<EmailSendRecipientEntity> recipients = new ArrayList<>();
		if (toEmails != null) {
			for (String email : toEmails) {
				recipients.add(createRecipient(sendLog.getId(), email, RecipientType.TO));
			}
		}
		if (ccEmails != null) {
			for (String email : ccEmails) {
				recipients.add(createRecipient(sendLog.getId(), email, RecipientType.CC));
			}
		}
		if (bccEmails != null) {
			for (String email : bccEmails) {
				recipients.add(createRecipient(sendLog.getId(), email, RecipientType.BCC));
			}
		}
		if (!recipients.isEmpty()) {
			emailSendRecipientService.batchSave(recipients);
		}

		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

			// Set recipients
			if (toEmails != null && !toEmails.isEmpty()) {
				helper.setTo(toEmails.toArray(new String[0]));
			}
			if (ccEmails != null && !ccEmails.isEmpty()) {
				helper.setCc(ccEmails.toArray(new String[0]));
			}
			if (bccEmails != null && !bccEmails.isEmpty()) {
				helper.setBcc(bccEmails.toArray(new String[0]));
			}

			helper.setSubject(template.getSubject());
			helper.setFrom(
					template.getFromEmail() != null ? template.getFromEmail() : emailProperties.getDefaultFromEmail(),
					template.getFromName() != null ? template.getFromName() : emailProperties.getDefaultFromName());

			String htmlContent = processTemplate(template.getTemplatePath(), variables);
			helper.setText(htmlContent, true);

			if (attachments != null) {
				for (FileSystemResource attachment : attachments) {
					helper.addAttachment(attachment.getFilename(), attachment);
				}
			}

			mailSender.send(mimeMessage);

			// Update send log status to success
			sendLog.setSendStatus(SendStatus.SUCCESS.getCode());
			sendLog.setSendTime(LocalDateTime.now());
			emailSendLogService.updateLogStatus(sendLog.getId(), SendStatus.SUCCESS.getCode(), null);

			log.info("Email sent successfully");
		} catch (Exception e) {
			// Update send log status to failed
			sendLog.setSendStatus(SendStatus.FAILED.getCode());
			sendLog.setErrorMessage(e.getMessage());
			sendLog.setSendTime(LocalDateTime.now());
			emailSendLogService.updateLogStatus(sendLog.getId(), SendStatus.FAILED.getCode(), e.getMessage());

			log.error("Failed to send email, error: {}", e.getMessage());
			throw new RuntimeException("Failed to send email", e);
		}
	}

	private EmailTemplateEntity getTemplateByCode(String templateCode) {
		return templateMapper.selectOne(new LambdaQueryWrapper<EmailTemplateEntity>()
				.eq(EmailTemplateEntity::getTemplateCode, templateCode)
				.eq(EmailTemplateEntity::getStatus, 1)
				.eq(EmailTemplateEntity::getDeleted, 0));
	}

	private EmailSendRecipientEntity createRecipient(Long sendLogId, String email, RecipientType type) {
		return EmailSendRecipientEntity.builder()
				.sendLogId(sendLogId)
				.email(email)
				.recipientType(type.getCode())
				.build();
	}

	private String processTemplate(String templatePath, Map<String, Object> variables) {
		Context context = new Context();
		context.setVariables(variables);

		String fullPath;
		if (emailProperties.getTemplateDir() != null && !emailProperties.getTemplateDir().isEmpty()) {
			// Use configured template directory
			fullPath = emailProperties.getTemplateDir() + "/" + templatePath;
		} else {
			// Use Thymeleaf default template path
			fullPath = templatePath;
		}

		return templateEngine.process(fullPath, context);
	}
}
