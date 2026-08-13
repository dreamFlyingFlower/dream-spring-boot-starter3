package dream.flying.flower.autoconfigure.email.enums;

import java.util.stream.Stream;

import dream.flying.flower.common.CodeMsg;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Recipient type enumeration
 *
 * @author 飞花梦影
 * @date 2026-05-25 13:25:57
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RecipientType implements CodeMsg {

	RECIPIENT_TYPE("收件人类型"),

	/** 接收人 */
	TO("接收人"),

	/** 抄送人 */
	CC("抄送人"),

	/** 密送人 */
	BCC("密送人");

	private final String msg;

	public static RecipientType get(int value) {
		return Stream.of(values())
				.filter(t -> t.ordinal() == value)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Invalid enum value: " + value));
	}

	public static RecipientType get(String value) {
		return Stream.of(values())
				.filter(t -> t.name().equalsIgnoreCase(value))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Invalid enum value: " + value));
	}
}