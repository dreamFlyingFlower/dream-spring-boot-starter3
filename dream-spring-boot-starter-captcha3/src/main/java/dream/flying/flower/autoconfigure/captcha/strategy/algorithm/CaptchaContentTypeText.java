package dream.flying.flower.autoconfigure.captcha.strategy.algorithm;

import com.google.code.kaptcha.Producer;

import dream.flying.flower.autoconfigure.captcha.strategy.CaptchaContentTypeStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 验证码文本策略
 *
 * @author 飞花梦影
 * @date 2024-12-09 13:09:31
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@AllArgsConstructor
public class CaptchaContentTypeText implements CaptchaContentTypeStrategy {

	private final Producer producer;

	@Override
	public String captchaContent(Producer producer) {
		return producer.createText();
	}

	@Override
	public String captchaValue(Producer producer, String captchaContent) {
		return captchaContent;
	}
}