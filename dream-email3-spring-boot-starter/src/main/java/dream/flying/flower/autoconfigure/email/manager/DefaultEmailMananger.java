package dream.flying.flower.autoconfigure.email.manager;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import dream.flying.flower.autoconfigure.email.EmailManager;
import dream.flying.flower.autoconfigure.email.entity.EmailRecipientEntity;
import dream.flying.flower.autoconfigure.email.entity.EmailSendLogEntity;
import dream.flying.flower.autoconfigure.email.enums.EmailSendStatus;
import dream.flying.flower.autoconfigure.email.enums.RecipientType;
import dream.flying.flower.autoconfigure.email.properties.DreamEmailProperties;
import dream.flying.flower.autoconfigure.email.service.EmailRecipientService;
import dream.flying.flower.autoconfigure.email.service.EmailSendLogService;
import dream.flying.flower.autoconfigure.email.service.EmailTemplateService;
import dream.flying.flower.autoconfigure.email.vo.EmailTemplateVO;
import dream.flying.flower.collection.CollectionHelper;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 邮件发送处理类
 *
 * @author 飞花梦影
 * @date 2026-08-13 10:18:43
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultEmailMananger implements EmailManager {

	private final JavaMailSender mailSender;

	private final TemplateEngine templateEngine;

	private final DreamEmailProperties dreamEmailProperties;

	private final EmailTemplateService emailTemplateService;

	private final EmailSendLogService emailSendLogService;

	private final EmailRecipientService emailRecipientService;

	@Override
	public void send(String to, String templateCode, Map<String, Object> variables) {
		send(Collections.singletonList(to), null, null, templateCode, variables);
	}

	@Override
	public void send(List<String> tos, List<String> ccs, List<String> bccs, String templateCode,
			Map<String, Object> variables) {
		sendRecipients(tos, ccs, bccs, templateCode, variables, null);
	}

	private void sendRecipients(List<String> tos, List<String> ccs, List<String> bccs, String templateCode,
			Map<String, Object> variables, FileSystemResource[] attachments) {
		EmailTemplateVO templateVo = emailTemplateService.getByCode(templateCode);
		if (templateVo == null) {
			throw new IllegalArgumentException("Email template not found: " + templateCode);
		}

		int attachmentCount = attachments != null ? attachments.length : 0;

		EmailSendLogEntity sendLog = EmailSendLogEntity.builder()
				.templateCode(templateVo.getTemplateCode())
				.subject(templateVo.getSubject())
				.fromEmail(templateVo.getFromEmail())
				.fromName(templateVo.getFromName())
				.sendStatus(EmailSendStatus.PENDING.getValue())
				.attachmentCount(attachmentCount)
				.build();
		emailSendLogService.save(sendLog);

		// Save recipients
		List<EmailRecipientEntity> recipients = new ArrayList<>();
		if (tos != null) {
			recipients.addAll(tos.stream()
					.map(t -> createRecipient(sendLog.getId(), t, RecipientType.TO))
					.collect(Collectors.toList()));
		}
		if (ccs != null) {
			recipients.addAll(ccs.stream()
					.map(t -> createRecipient(sendLog.getId(), t, RecipientType.CC))
					.collect(Collectors.toList()));
		}
		if (bccs != null) {
			recipients.addAll(bccs.stream()
					.map(t -> createRecipient(sendLog.getId(), t, RecipientType.BCC))
					.collect(Collectors.toList()));
		}
		if (!recipients.isEmpty()) {
			emailRecipientService.saveBatch(recipients);
		}

		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

			// Set recipients
			if (CollectionHelper.isNotEmpty(tos)) {
				helper.setTo(tos.toArray(new String[tos.size()]));
			}
			if (CollectionHelper.isNotEmpty(ccs)) {
				helper.setCc(ccs.toArray(new String[ccs.size()]));
			}
			if (CollectionHelper.isNotEmpty(bccs)) {
				helper.setBcc(bccs.toArray(new String[bccs.size()]));
			}

			helper.setSubject(templateVo.getSubject());
			helper.setFrom(
					templateVo.getFromEmail() != null
							? templateVo.getFromEmail() : dreamEmailProperties.getDefaultFromEmail(),
					templateVo.getFromName() != null
							? templateVo.getFromName() : dreamEmailProperties.getDefaultFromName());

			String htmlContent = processTemplate(templateVo.getTemplatePath(), variables);
			helper.setText(htmlContent, true);

			if (attachments != null) {
				for (FileSystemResource attachment : attachments) {
					helper.addAttachment(attachment.getFilename(), attachment);
				}
			}

			mailSender.send(mimeMessage);

			// Update send log status to success
			sendLog.setSendStatus(EmailSendStatus.SUCCESS.getValue());
			sendLog.setSendTime(LocalDateTime.now());
			emailSendLogService.updateLogStatus(sendLog.getId(), EmailSendStatus.SUCCESS.getValue(), null);

			log.info("Email sent successfully");
		} catch (Exception e) {
			// Update send log status to failed
			sendLog.setSendStatus(EmailSendStatus.FAILED.getValue());
			sendLog.setErrorMessage(e.getMessage());
			sendLog.setSendTime(LocalDateTime.now());
			emailSendLogService.updateLogStatus(sendLog.getId(), EmailSendStatus.FAILED.getValue(), e.getMessage());

			log.error("Failed to send email, error: {}", e.getMessage());
			throw new RuntimeException("Failed to send email", e);
		}
	}

	private EmailRecipientEntity createRecipient(Long sendLogId, String email, RecipientType type) {
		return EmailRecipientEntity.builder().sendLogId(sendLogId).email(email).recipientType(type.getValue()).build();
	}

	private String processTemplate(String templatePath, Map<String, Object> variables) {
		Context context = new Context();
		context.setVariables(variables);

		String fullPath;
		if (dreamEmailProperties.getTemplateDir() != null && !dreamEmailProperties.getTemplateDir().isEmpty()) {
			fullPath = dreamEmailProperties.getTemplateDir() + "/" + templatePath;
		} else {
			fullPath = templatePath;
		}

		return templateEngine.process(fullPath, context);
	}

	@Override
	public void sendAttachments(String to, String templateCode, Map<String, Object> variables,
			FileSystemResource... attachments) {
		List<String> toEmails = Collections.singletonList(to);
		sendRecipients(toEmails, null, null, templateCode, variables, attachments);
	}
}