package dream.flying.flower.web.dto;

import lombok.Data;

/**
 * Redis数据请求DTO
 *
 * @author 飞花梦影
 * @date 2026-05-09 15:36:00
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
public class RedisDataRequest {

	/**
	 * Redis键
	 */
	private String key;

	/**
	 * Redis值
	 */
	private Object value;

	/**
	 * 过期时间(秒)
	 */
	private Long expireTime;

	/**
	 * Hash字段键(用于Hash操作)
	 */
	private String hashKey;

	/**
	 * 数据库索引
	 */
	private Integer dbIndex;

	/**
	 * 数据类型: string, hash, list, set, zset
	 */
	private String dataType;
}