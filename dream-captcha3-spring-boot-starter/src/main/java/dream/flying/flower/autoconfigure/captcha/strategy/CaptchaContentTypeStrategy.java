package dream.flying.flower.autoconfigure.captcha.strategy;

import com.google.code.kaptcha.Producer;

/**
 * 验证码类型策略
 *
 * @author 飞花梦影
 * @date 2024-11-16 21:40:14
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface CaptchaContentTypeStrategy {

	/**
	 * 生成验证码内容
	 * 
	 * @param producer 验证码生成器
	 * @return 验证码key
	 */
	String captchaContent(Producer producer);

	/**
	 * 生成验证码校验值
	 * 
	 * @param producer 验证码生成器
	 * @param captchaContent 验证码内容
	 * @return 验证码value
	 */
	String captchaValue(Producer producer, String captchaContent);
}