package dream.flying.flower.autoconfigure.captcha.strategy.context;

import com.google.code.kaptcha.Producer;

import dream.flying.flower.autoconfigure.captcha.enums.CaptchaContentType;
import dream.flying.flower.autoconfigure.captcha.strategy.CaptchaContentTypeStrategy;
import dream.flying.flower.autoconfigure.captcha.strategy.algorithm.CaptchaContentTypeArithmetic;
import dream.flying.flower.autoconfigure.captcha.strategy.algorithm.CaptchaContentTypeText;

/**
 * 验证码内容类型上下文
 *
 * @author 飞花梦影
 * @date 2024-12-09 13:03:13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class CaptchaContentContext {

	public static CaptchaContentTypeStrategy getInstance(String captchaContentType, Producer producer) {
		CaptchaContentType type = CaptchaContentType.get(captchaContentType);
		return getInstance(type, producer);
	}

	public static CaptchaContentTypeStrategy getInstance(CaptchaContentType captchaContentType, Producer producer) {
		if (CaptchaContentType.ARITHMETIC == captchaContentType) {
			return new CaptchaContentTypeArithmetic(producer);
		}
		return new CaptchaContentTypeText(producer);
	}
}