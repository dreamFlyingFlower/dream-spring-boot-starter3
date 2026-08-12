package dream.flying.flower.autoconfigure.otp.factory;

import org.springframework.context.ApplicationContext;

import dream.flying.flower.autoconfigure.otp.transport.EmailOtpTransport;
import dream.flying.flower.framework.otp.OtpTransport;
import dream.flying.flower.framework.otp.enums.TransportType;
import dream.flying.flower.framework.otp.transport.PushOtpTransport;
import dream.flying.flower.framework.otp.transport.SmsOtpTransport;

/**
 * OTP 传输工厂
 *
 * @author 飞花梦影
 * @date 2026-04-28
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public final class OtpTransportFactory {

	private OtpTransportFactory() {
		throw new UnsupportedOperationException("Factory classes are not allowed to be instantiated");
	}

	/**
	 * 根据传输类型创建 OTP 传输实现
	 *
	 * @param applicationContext Spring 应用上下文
	 * @param type 传输类型
	 * @return OTP 传输实现
	 * @throws IllegalArgumentException 如果传输类型不支持
	 */
	public static OtpTransport getInstance(ApplicationContext applicationContext, TransportType type) {
		if (applicationContext == null) {
			throw new IllegalArgumentException("ApplicationContext must not be null");
		}
		if (type == null) {
			throw new IllegalArgumentException("The transmission type cannot be empty");
		}

		// 根据传输类型返回对应的实现
		switch (type) {
		case SMS:
			return new SmsOtpTransport(applicationContext);
		case EMAIL:
			return new EmailOtpTransport(applicationContext);
		case MOBILE_PUSH:
			return new PushOtpTransport(applicationContext);
		default:
			throw new IllegalArgumentException("Unsupported transmission type: " + type);
		}
	}

	/**
	 * 创建默认的 SMS 传输
	 *
	 * @param applicationContext Spring 应用上下文
	 * @return SMS 传输实现
	 */
	public static OtpTransport createDefaultSms(ApplicationContext applicationContext) {
		return getInstance(applicationContext, TransportType.SMS);
	}

	/**
	 * 创建默认的 Email 传输
	 *
	 * @param applicationContext Spring 应用上下文
	 * @return Email 传输实现
	 */
	public static OtpTransport createDefaultEmail(ApplicationContext applicationContext) {
		return getInstance(applicationContext, TransportType.EMAIL);
	}
}