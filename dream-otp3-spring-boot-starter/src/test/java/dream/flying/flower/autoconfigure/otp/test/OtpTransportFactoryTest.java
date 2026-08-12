package dream.flying.flower.autoconfigure.otp.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import dream.flying.flower.autoconfigure.otp.factory.OtpTransportFactory;
import dream.flying.flower.autoconfigure.otp.manager.EmailRenderManager;
import dream.flying.flower.framework.otp.OtpTransport;
import dream.flying.flower.framework.otp.enums.TransportType;

/**
 * OtpTransportFactory 测试类
 */
class OtpTransportFactoryTest {

	@Test
	void testCreateSmsTransport() {
		ApplicationContext context = createMockApplicationContext();
		OtpTransport transport = OtpTransportFactory.getInstance(context, TransportType.SMS);
		assertNotNull(transport);
		assertEquals(TransportType.SMS, transport.getType());

		// 测试手机号验证
		assertTrue(transport.supports("13812345678"));
		assertTrue(transport.supports("19912345678"));
		assertFalse(transport.supports("123456"));
		assertFalse(transport.supports("abc@example.com"));
	}

	@Test
	void testCreateEmailTransport() {
		ApplicationContext context = createMockApplicationContext();
		OtpTransport transport = OtpTransportFactory.getInstance(context, TransportType.EMAIL);
		assertNotNull(transport);
		assertEquals(TransportType.EMAIL, transport.getType());

		// 测试邮箱验证
		assertTrue(transport.supports("test@example.com"));
		assertTrue(transport.supports("user.name@domain.org"));
		assertFalse(transport.supports("invalid-email"));
		assertFalse(transport.supports("13812345678"));
	}

	@Test
	void testCreateMobilePushTransport() {
		ApplicationContext context = createMockApplicationContext();
		OtpTransport transport = OtpTransportFactory.getInstance(context, TransportType.MOBILE_PUSH);
		assertNotNull(transport);
		assertEquals(TransportType.MOBILE_PUSH, transport.getType());

		// 测试设备ID验证
		assertTrue(transport.supports("device_id_12345"));
		assertFalse(transport.supports("short"));
		assertFalse(transport.supports(""));
	}

	@Test
	void testSendSmsOtp() {
		ApplicationContext context = createMockApplicationContext();
		OtpTransport transport = OtpTransportFactory.createDefaultSms(context);
		// 注意：此测试不会真正发送短信，因为缺少 SmsTemplate bean
		// 这里只验证传输对象创建成功
		assertNotNull(transport);
		assertEquals(TransportType.SMS, transport.getType());
	}

	@Test
	void testSendEmailOtp() {
		ApplicationContext context = createMockApplicationContext();
		OtpTransport transport = OtpTransportFactory.createDefaultEmail(context);
		// 注意：此测试不会真正发送邮件，因为缺少 JavaMailSender bean
		// 这里只验证传输对象创建成功
		assertNotNull(transport);
		assertEquals(TransportType.EMAIL, transport.getType());
	}

	@Test
	void testInvalidPhoneNumber() {
		ApplicationContext context = createMockApplicationContext();
		OtpTransport transport = OtpTransportFactory.createDefaultSms(context);
		// 无效的手机号应该返回 false
		boolean result = transport.send("123456", "123456");
		assertFalse(result);
	}

	@Test
	void testInvalidEmail() {
		ApplicationContext context = createMockApplicationContext();
		OtpTransport transport = OtpTransportFactory.createDefaultEmail(context);
		// 无效的邮箱应该返回 false
		boolean result = transport.send("invalid-email", "123456");
		assertFalse(result);
	}

	/**
	 * 创建 Mock ApplicationContext（仅用于测试）
	 */
	private ApplicationContext createMockApplicationContext() {
		return new org.springframework.context.support.GenericApplicationContext() {

			@Override
			public <T> T getBean(Class<T> requiredType) {
				if (requiredType == EmailRenderManager.class) {
					return requiredType.cast(new EmailRenderManager());
				}
				return super.getBean(requiredType);
			}
		};
	}
}