package dream.flying.flower.autoconfigure.otp.manager;

import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import lombok.extern.slf4j.Slf4j;

/**
 * 邮件模板引擎服务
 *
 * @author 飞花梦影
 * @date 2026-04-28
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
public class EmailRenderManager {

	private TemplateEngine templateEngine;

	/**
	 * 渲染邮件模板
	 *
	 * @param templateName 模板名称,不含后缀
	 * @param variables 模板变量
	 * @return 渲染后的 HTML 内容
	 */
	public String render(String templateName, Map<String, Object> variables) {
		try {
			Context context = new Context();
			if (variables != null) {
				context.setVariables(variables);
			}

			String content = templateEngine.process(templateName, context);
			log.debug("邮件模板渲染成功: {}", templateName);
			return content;
		} catch (Exception e) {
			log.error("邮件模板渲染失败: {}", templateName, e);
			throw new RuntimeException("邮件模板渲染失败: " + templateName, e);
		}
	}

	/**
	 * 渲染 OTP 验证码邮件模板
	 *
	 * @param otp 验证码
	 * @param expiresIn 过期时间,单位分钟
	 * @return 渲染后的 HTML 内容
	 */
	public String renderOtpEmail(String otp, int expiresIn) {
		Map<String, Object> variables =
				Map.of("otp", otp, "expiresIn", expiresIn, "year", String.valueOf(java.time.Year.now().getValue()));

		return render("otp-verification", variables);
	}

	/**
	 * 渲染 OTP 验证码邮件模板,默认30分钟过期
	 *
	 * @param otp 验证码
	 * @return 渲染后的 HTML 内容
	 */
	public String renderOtpEmail(String otp) {
		return renderOtpEmail(otp, 30);
	}

	/**
	 * 渲染密码重置邮件模板
	 *
	 * @param resetLink 重置链接
	 * @param expiresIn 过期时间,单位分钟
	 * @return 渲染后的 HTML 内容
	 */
	public String renderPasswordResetEmail(String resetLink, int expiresIn) {
		Map<String, Object> variables = Map.of("resetLink", resetLink, "expiresIn", expiresIn, "year",
				String.valueOf(java.time.Year.now().getValue()));

		return render("password-reset", variables);
	}

	/**
	 * 渲染密码重置邮件模板,默认30分钟过期
	 *
	 * @param resetLink 重置链接
	 * @return 渲染后的 HTML 内容
	 */
	public String renderPasswordResetEmail(String resetLink) {
		return renderPasswordResetEmail(resetLink, 30);
	}

	/**
	 * 渲染账户激活邮件模板
	 *
	 * @param activationLink 激活链接
	 * @param expiresIn 过期时间,单位小时
	 * @return 渲染后的 HTML 内容
	 */
	public String renderAccountActivationEmail(String activationLink, int expiresIn) {
		Map<String, Object> variables = Map.of("activationLink", activationLink, "expiresIn", expiresIn, "year",
				String.valueOf(java.time.Year.now().getValue()));

		return render("account-activation", variables);
	}

	/**
	 * 渲染账户激活邮件模板,默认24小时过期
	 *
	 * @param activationLink 激活链接
	 * @return 渲染后的 HTML 内容
	 */
	public String renderAccountActivationEmail(String activationLink) {
		return renderAccountActivationEmail(activationLink, 24);
	}

	/**
	 * 渲染安全警告邮件模板
	 *
	 * @param warningMessage 警告消息
	 * @param eventTime 事件时间
	 * @param ipAddress IP 地址
	 * @param deviceInfo 设备信息
	 * @return 渲染后的 HTML 内容
	 */
	public String renderSecurityWarningEmail(String warningMessage, String eventTime, String ipAddress,
			String deviceInfo) {
		Map<String, Object> variables = Map.of("warningMessage", warningMessage, "eventTime", eventTime, "ipAddress",
				ipAddress, "deviceInfo", deviceInfo, "year", String.valueOf(java.time.Year.now().getValue()));

		return render("security-warning", variables);
	}
}