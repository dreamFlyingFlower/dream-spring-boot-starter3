package dream.flying.flower.autoconfigure.captcha.enums;

import java.util.stream.Stream;

import dream.flying.flower.common.CodeMsg;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 验证码内容类型
 * 
 * @author 飞花梦影
 * @date 2024-07-30 21:34:56
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CaptchaContentType implements CodeMsg {

	/** 文本 */
	TEXT,

	/** 数字计算 */
	ARITHMETIC;

	public static CaptchaContentType get(int code) {
		return Stream.of(values()).filter(t -> t.ordinal() == code).findFirst().orElse(null);
	}

	public static CaptchaContentType get(String code) {
		return Stream.of(values()).filter(t -> t.name().equalsIgnoreCase(code)).findFirst().orElse(null);
	}
}