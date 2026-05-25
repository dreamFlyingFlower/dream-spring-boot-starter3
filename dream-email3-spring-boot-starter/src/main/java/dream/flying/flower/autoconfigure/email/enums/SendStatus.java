package dream.flying.flower.autoconfigure.email.enums;

/**
 * Email send status enum
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
public enum SendStatus {

	/** Pending status */
	PENDING(1, "待发送"),

	/** Success status */
	SUCCESS(2, "成功"),

	/** Failed status */
	FAILED(3, "失败");

	private final int code;
	private final String desc;

	SendStatus(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	/**
	 * Get status by code
	 *
	 * @param code status code
	 * @return SendStatus enum
	 */
	public static SendStatus fromCode(int code) {
		for (SendStatus status : values()) {
			if (status.getCode() == code) {
				return status;
			}
		}
		throw new IllegalArgumentException("Invalid send status code: " + code);
	}
}
