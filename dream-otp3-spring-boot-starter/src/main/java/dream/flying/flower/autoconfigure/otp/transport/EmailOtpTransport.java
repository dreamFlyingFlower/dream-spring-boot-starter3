package dream.flying.flower.autoconfigure.otp.transport;

import java.nio.charset.StandardCharsets;

import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import dream.flying.flower.autoconfigure.email.service.EmailTemplateService;
import dream.flying.flower.autoconfigure.email.vo.EmailTemplateVO;
import dream.flying.flower.autoconfigure.otp.manager.EmailRenderManager;
import dream.flying.flower.enums.RegexEnum;
import dream.flying.flower.framework.otp.OtpTransport;
import dream.flying.flower.framework.otp.enums.TransportType;
import dream.flying.flower.framework.otp.retry.RetryHelper;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 邮件 OTP 传输
 *
 * @author 飞花梦影
 * @date 2026-04-28
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class EmailOtpTransport implements OtpTransport {

	private final ApplicationContext applicationContext;

	@Override
	public boolean send(String receiver, String otp) {
		if (!supports(receiver)) {
			log.warn("不支持的邮箱格式: {}", receiver);
			return false;
		}

		JavaMailSender mailSender = applicationContext.getBean(JavaMailSender.class);
		EmailTemplateService emailTemplateService = applicationContext.getBean(EmailTemplateService.class);
		EmailRenderManager emailRenderManager = applicationContext.getBean(EmailRenderManager.class);

		try {
			// 使用重试机制发送邮件
			RetryHelper.builder().maxAttempts(3).initialDelayMs(1000).multiplier(2.0).maxDelayMs(10000).execute(() -> {

				EmailTemplateVO emailTemplateVo = emailTemplateService.getByCode("otp");
				// 渲染邮件模板
				String htmlContent = emailRenderManager.renderOtpEmail(otp);

				// 创建 MIME 邮件
				MimeMessage message = mailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

				helper.setFrom(emailTemplateVo.getFromEmail());
				helper.setSubject(emailTemplateVo.getSubject());
				helper.setTo(receiver);
				// true 表示 HTML 内容
				helper.setText(htmlContent, true);

				// 发送邮件
				mailSender.send(message);

				log.info("邮件 OTP 发送成功: {}", receiver);
				return true;
			}, "发送邮件 OTP");

			return true;
		} catch (Exception e) {
			log.error("发送邮件 OTP 失败: {}", receiver, e);
			return false;
		}
	}

	@Override
	public TransportType getType() {
		return TransportType.EMAIL;
	}

	@Override
	public boolean supports(String receiver) {
		if (receiver == null || receiver.isEmpty()) {
			return false;
		}
		return RegexEnum.REGEX_EMAIL.getValue().matcher(receiver).matches();
	}
}