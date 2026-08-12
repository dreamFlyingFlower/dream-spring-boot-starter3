package dream.flying.flower.autoconfigure.email.enums;

import java.util.stream.Stream;

import dream.flying.flower.common.CodeMsg;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Email send status enum
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum EmailSendStatus implements CodeMsg {

	/** 与字典对应 */
	EMAIL_SEND_STATUS("邮件发送状态"),

	/** Pending status */
	PENDING("待发送"),

	/** Success status */
	SUCCESS("成功"),

	/** Failed status */
	FAILED("失败");

	private final String msg;

	public static EmailSendStatus get(int value) {
		return Stream.of(values())
				.filter(t -> t.ordinal() == value)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Invalid enum value: " + value));
	}

	public static EmailSendStatus get(String value) {
		return Stream.of(values())
				.filter(t -> t.name().equalsIgnoreCase(value))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Invalid enum value: " + value));
	}
}