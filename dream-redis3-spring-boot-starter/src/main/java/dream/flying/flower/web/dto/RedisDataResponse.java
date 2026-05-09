package dream.flying.flower.web.dto;

import java.util.List;

import lombok.Data;

/**
 * Redis数据响应DTO
 *
 * @author 飞花梦影
 * @date 2026-05-09 15:36:00
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Data
public class RedisDataResponse {

	/**
	 * 操作是否成功
	 */
	private Boolean success;

	/**
	 * 消息
	 */
	private String message;

	/**
	 * 数据结果
	 */
	private Object data;

	/**
	 * Key列表(用于keys命令)
	 */
	private List<String> keys;

	/**
	 * 当前数据库索引
	 */
	private Integer currentDbIndex;

	public static RedisDataResponse success(Object data) {
		RedisDataResponse response = new RedisDataResponse();
		response.setSuccess(true);
		response.setMessage("操作成功");
		response.setData(data);
		return response;
	}

	public static RedisDataResponse success(String message, Object data) {
		RedisDataResponse response = new RedisDataResponse();
		response.setSuccess(true);
		response.setMessage(message);
		response.setData(data);
		return response;
	}

	public static RedisDataResponse error(String message) {
		RedisDataResponse response = new RedisDataResponse();
		response.setSuccess(false);
		response.setMessage(message);
		return response;
	}
}
