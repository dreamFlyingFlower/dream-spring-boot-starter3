package dream.flying.flower.autoconfigure.email.enums;

/**
 * Recipient type enumeration
 *
 * @author 飞花梦影
 * @date 2026-05-25
 */
public enum RecipientType {

	TO(1, "接收人"),
	CC(2, "抄送人"),
	BCC(3, "密送人");

	private final int code;
	private final String desc;

	RecipientType(int code, String desc) {
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
	 * Get recipient type by code
	 *
	 * @param code recipient type code
	 * @return recipient type
	 */
	public static RecipientType fromCode(int code) {
		for (RecipientType type : values()) {
			if (type.getCode() == code) {
				return type;
			}
		}
		throw new IllegalArgumentException("Invalid recipient type code: " + code);
	}
}
