package dream.flying.flower.autoconfigure.captcha.strategy.algorithm;

import java.security.SecureRandom;

import com.google.code.kaptcha.Producer;

import dream.flying.flower.autoconfigure.captcha.strategy.CaptchaContentTypeStrategy;
import dream.flying.flower.enums.ArithmeticType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 验证码算术策略
 *
 * @author 飞花梦影
 * @date 2024-12-09 13:09:31
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@AllArgsConstructor
public class CaptchaContentTypeArithmetic implements CaptchaContentTypeStrategy {

	private final Producer captchaProducer;

	@Override
	public String captchaContent(Producer producer) {
		SecureRandom secureRandom = new SecureRandom();
		int begin = secureRandom.nextInt(100);
		ArithmeticType[] values = ArithmeticType.values();
		return begin + (values[begin % 4].getArithmeticSymbol()) + secureRandom.nextInt(100) + "=?";
	}

	@Override
	public String captchaValue(Producer producer, String captchaContent) {
		Integer begin = Integer.parseInt(captchaContent.substring(0, 1));
		Integer end = Integer.parseInt(captchaContent.substring(2, 3));
		ArithmeticType arithmeticType = ArithmeticType.getByArithmeticType(captchaContent.substring(1, 2));
		return ArithmeticType.calculate(arithmeticType, begin, end) + "";
	}
}