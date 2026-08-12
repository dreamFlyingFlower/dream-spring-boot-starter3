package dream.flying.flower.autoconfigure.otp.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import dream.flying.flower.autoconfigure.otp.manager.EmailRenderManager;

/**
 * EmailTemplateService 测试类
 */
class EmailRenderManagerTest {

	private EmailRenderManager emailRenderManager;

	@Test
	void testRenderOtpEmail() {
		// 测试 OTP 验证邮件渲染
		String html = emailRenderManager.renderOtpEmail("123456", 30);

		assertNotNull(html);
		assertTrue(html.contains("123456"));
		assertTrue(html.contains("30"));
		assertTrue(html.contains("OTP 验证码"));
	}

	@Test
	void testRenderOtpEmailDefaultExpiry() {
		// 测试默认过期时间的 OTP 邮件
		String html = emailRenderManager.renderOtpEmail("654321");

		assertNotNull(html);
		assertTrue(html.contains("654321"));
		assertTrue(html.contains("30")); // 默认30分钟
	}

	@Test
	void testRenderPasswordResetEmail() {
		// 测试密码重置邮件渲染
		String html = emailRenderManager.renderPasswordResetEmail("https://example.com/reset?token=abc123", 60);

		assertNotNull(html);
		assertTrue(html.contains("https://example.com/reset?token=abc123"));
		assertTrue(html.contains("60"));
		assertTrue(html.contains("密码重置"));
	}

	@Test
	void testRenderPasswordResetEmailDefaultExpiry() {
		// 测试默认过期时间的密码重置邮件
		String html = emailRenderManager.renderPasswordResetEmail("https://example.com/reset?token=xyz789");

		assertNotNull(html);
		assertTrue(html.contains("https://example.com/reset?token=xyz789"));
		assertTrue(html.contains("30")); // 默认30分钟
	}

	@Test
	void testRenderAccountActivationEmail() {
		// 测试账户激活邮件渲染
		String html = emailRenderManager.renderAccountActivationEmail("https://example.com/activate?token=def456", 24);

		assertNotNull(html);
		assertTrue(html.contains("https://example.com/activate?token=def456"));
		assertTrue(html.contains("24"));
		assertTrue(html.contains("欢迎加入"));
	}

	@Test
	void testRenderAccountActivationEmailDefaultExpiry() {
		// 测试默认过期时间的账户激活邮件
		String html = emailRenderManager.renderAccountActivationEmail("https://example.com/activate?token=ghi789");

		assertNotNull(html);
		assertTrue(html.contains("https://example.com/activate?token=ghi789"));
		assertTrue(html.contains("24")); // 默认24小时
	}

	@Test
	void testRenderSecurityWarningEmail() {
		// 测试安全警告邮件渲染
		String html = emailRenderManager.renderSecurityWarningEmail("多次登录失败", "2026-04-28 10:30", "192.168.1.100",
				"Windows Chrome");

		assertNotNull(html);
		assertTrue(html.contains("多次登录失败"));
		assertTrue(html.contains("2026-04-28 10:30"));
		assertTrue(html.contains("192.168.1.100"));
		assertTrue(html.contains("Windows Chrome"));
		assertTrue(html.contains("安全警告"));
	}

	@Test
	void testHtmlStructure() {
		// 测试 HTML 结构完整性
		String html = emailRenderManager.renderOtpEmail("123456");

		assertTrue(html.startsWith("<!DOCTYPE html>"));
		assertTrue(html.contains("</html>"));
		assertTrue(html.contains("<head>"));
		assertTrue(html.contains("</head>"));
		assertTrue(html.contains("<body>"));
		assertTrue(html.contains("</body>"));
	}

	@Test
	void testDynamicYear() {
		// 测试动态年份
		String html = emailRenderManager.renderOtpEmail("123456");

		String currentYear = String.valueOf(java.time.Year.now().getValue());
		assertTrue(html.contains(currentYear));
	}

	@Test
	void testNullOtp() {
		// 测试 null 验证码（应该能正常渲染）
		String html = emailRenderManager.renderOtpEmail(null);

		assertNotNull(html);
	}

	@Test
	void testEmptyOtp() {
		// 测试空字符串验证码
		String html = emailRenderManager.renderOtpEmail("");

		assertNotNull(html);
	}
}
