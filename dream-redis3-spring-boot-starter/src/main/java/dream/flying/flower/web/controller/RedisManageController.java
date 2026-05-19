package dream.flying.flower.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dream.flying.flower.web.dto.RedisDataRequest;
import dream.flying.flower.web.dto.RedisDataResponse;
import dream.flying.flower.web.service.RedisManageService;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis管理控制器
 *
 * @author 飞花梦影
 * @date 2026-05-09 15:36:00
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@RestController
@RequestMapping("/api/redis")
public class RedisManageController {

	@Autowired
	private RedisManageService redisManageService;

	/**
	 * 获取所有Key
	 *
	 * @param pattern 匹配模式,默认*
	 * @param useScan 是否使用SCAN命令,默认false
	 * @param dbIndex 数据库索引,可选
	 * @return Key列表
	 */
	@GetMapping("/keys")
	public RedisDataResponse getKeys(@RequestParam(required = false, defaultValue = "*") String pattern,
			@RequestParam(required = false, defaultValue = "false") Boolean useScan,
			@RequestParam(required = false) Integer dbIndex) {
		try {
			List<String> keys;
			if (useScan) {
				keys = redisManageService.scanKeys(pattern, 100, dbIndex);
			} else {
				keys = redisManageService.keys(pattern, dbIndex);
			}
			RedisDataResponse response = RedisDataResponse.success(keys);
			response.setKeys(keys);
			return response;
		} catch (Exception e) {
			log.error("获取Keys失败", e);
			return RedisDataResponse.error("获取Keys失败: " + e.getMessage());
		}
	}

	/**
	 * 获取数据
	 *
	 * @param key 键
	 * @param dataType 数据类型(string/hash/list/set/zset)
	 * @param hashKey Hash字段键(仅Hash类型需要)
	 * @param dbIndex 数据库索引,可选
	 * @return 数据值
	 */
	@GetMapping("/get")
	public RedisDataResponse getData(@RequestParam String key, @RequestParam(required = false) String dataType,
			@RequestParam(required = false) String hashKey, @RequestParam(required = false) Integer dbIndex) {
		try {
			RedisDataRequest request = new RedisDataRequest();
			request.setKey(key);
			request.setDataType(dataType);
			request.setHashKey(hashKey);
			request.setDbIndex(dbIndex);

			Object data = redisManageService.executeByDataType(request);
			return RedisDataResponse.success(data);
		} catch (Exception e) {
			log.error("获取数据失败, key: {}", key, e);
			return RedisDataResponse.error("获取数据失败: " + e.getMessage());
		}
	}

	/**
	 * 设置字符串数据
	 *
	 * @param request 请求参数
	 * @return 操作结果
	 */
	@PostMapping("/set")
	public RedisDataResponse setData(@RequestBody RedisDataRequest request) {
		try {
			Boolean result = redisManageService.setString(request.getKey(), request.getValue(), request.getExpireTime(),
					request.getDbIndex());
			if (result) {
				return RedisDataResponse.success("设置成功", null);
			} else {
				return RedisDataResponse.error("设置失败");
			}
		} catch (Exception e) {
			log.error("设置数据失败", e);
			return RedisDataResponse.error("设置数据失败: " + e.getMessage());
		}
	}

	/**
	 * 设置Hash字段值
	 *
	 * @param request 请求参数
	 * @return 操作结果
	 */
	@PostMapping("/hash/set")
	public RedisDataResponse setHashField(@RequestBody RedisDataRequest request) {
		try {
			Boolean result = redisManageService.setHashField(request.getKey(), request.getHashKey(), request.getValue(),
					request.getDbIndex());
			if (result) {
				return RedisDataResponse.success("设置Hash字段成功", null);
			} else {
				return RedisDataResponse.error("设置Hash字段失败");
			}
		} catch (Exception e) {
			log.error("设置Hash字段失败", e);
			return RedisDataResponse.error("设置Hash字段失败: " + e.getMessage());
		}
	}

	/**
	 * 向List左侧添加元素
	 *
	 * @param request 请求参数
	 * @return 操作结果
	 */
	@PostMapping("/list/left-push")
	public RedisDataResponse leftPushList(@RequestBody RedisDataRequest request) {
		try {
			Long result = redisManageService.leftPushList(request.getKey(), request.getValue(), request.getDbIndex());
			return RedisDataResponse.success("添加成功", result);
		} catch (Exception e) {
			log.error("左侧添加List元素失败", e);
			return RedisDataResponse.error("左侧添加List元素失败: " + e.getMessage());
		}
	}

	/**
	 * 向List右侧添加元素
	 *
	 * @param request 请求参数
	 * @return 操作结果
	 */
	@PostMapping("/list/right-push")
	public RedisDataResponse rightPushList(@RequestBody RedisDataRequest request) {
		try {
			Long result = redisManageService.rightPushList(request.getKey(), request.getValue(), request.getDbIndex());
			return RedisDataResponse.success("添加成功", result);
		} catch (Exception e) {
			log.error("右侧添加List元素失败", e);
			return RedisDataResponse.error("右侧添加List元素失败: " + e.getMessage());
		}
	}

	/**
	 * 向Set添加成员
	 *
	 * @param request 请求参数
	 * @return 操作结果
	 */
	@PostMapping("/set/add")
	public RedisDataResponse addSetMembers(@RequestBody RedisDataRequest request) {
		try {
			@SuppressWarnings("unchecked")
			List<Object> values = (List<Object>) request.getValue();
			Long result = redisManageService.addSetMembers(request.getKey(), values, request.getDbIndex());
			return RedisDataResponse.success("添加成功", result);
		} catch (Exception e) {
			log.error("添加Set成员失败", e);
			return RedisDataResponse.error("添加Set成员失败: " + e.getMessage());
		}
	}

	/**
	 * 向ZSet添加成员
	 *
	 * @param request 请求参数
	 * @return 操作结果
	 */
	@PostMapping("/zset/add")
	public RedisDataResponse addZSetMember(@RequestBody RedisDataRequest request) {
		try {
			Double score = request.getValue() instanceof Number ? ((Number) request.getValue()).doubleValue() : 0.0;
			Boolean result =
					redisManageService.addZSetMember(request.getKey(), request.getKey(), score, request.getDbIndex());
			if (result) {
				return RedisDataResponse.success("添加成功", null);
			} else {
				return RedisDataResponse.error("添加失败");
			}
		} catch (Exception e) {
			log.error("添加ZSet成员失败", e);
			return RedisDataResponse.error("添加ZSet成员失败: " + e.getMessage());
		}
	}

	/**
	 * 删除Key
	 *
	 * @param key 键
	 * @param dbIndex 数据库索引,可选
	 * @return 操作结果
	 */
	@DeleteMapping("/delete")
	public RedisDataResponse deleteKey(@RequestParam String key, @RequestParam(required = false) Integer dbIndex) {
		try {
			Boolean result = redisManageService.delete(key, dbIndex);
			if (result) {
				return RedisDataResponse.success("删除成功", null);
			} else {
				return RedisDataResponse.error("删除失败或Key不存在");
			}
		} catch (Exception e) {
			log.error("删除Key失败, key: {}", key, e);
			return RedisDataResponse.error("删除Key失败: " + e.getMessage());
		}
	}

	/**
	 * 批量删除Key
	 *
	 * @param keys 键列表
	 * @param dbIndex 数据库索引,可选
	 * @return 操作结果
	 */
	@DeleteMapping("/delete-batch")
	public RedisDataResponse deleteBatchKeys(@RequestBody List<String> keys,
			@RequestParam(required = false) Integer dbIndex) {
		try {
			Long count = redisManageService.deleteBatch(keys, dbIndex);
			return RedisDataResponse.success("删除成功", count);
		} catch (Exception e) {
			log.error("批量删除Key失败", e);
			return RedisDataResponse.error("批量删除Key失败: " + e.getMessage());
		}
	}

	/**
	 * 删除Hash字段
	 *
	 * @param request 请求参数
	 * @return 操作结果
	 */
	@DeleteMapping("/hash/delete")
	public RedisDataResponse deleteHashFields(@RequestBody RedisDataRequest request) {
		try {
			@SuppressWarnings("unchecked")
			List<String> hashKeys = (List<String>) request.getValue();
			Long count = redisManageService.deleteHashFields(request.getKey(), hashKeys, request.getDbIndex());
			return RedisDataResponse.success("删除成功", count);
		} catch (Exception e) {
			log.error("删除Hash字段失败", e);
			return RedisDataResponse.error("删除Hash字段失败: " + e.getMessage());
		}
	}

	/**
	 * 从Set移除成员
	 *
	 * @param request 请求参数
	 * @return 操作结果
	 */
	@DeleteMapping("/set/remove")
	public RedisDataResponse removeSetMembers(@RequestBody RedisDataRequest request) {
		try {
			@SuppressWarnings("unchecked")
			List<Object> values = (List<Object>) request.getValue();
			Long count = redisManageService.removeSetMembers(request.getKey(), values, request.getDbIndex());
			return RedisDataResponse.success("移除成功", count);
		} catch (Exception e) {
			log.error("移除Set成员失败", e);
			return RedisDataResponse.error("移除Set成员失败: " + e.getMessage());
		}
	}

	/**
	 * 从ZSet移除成员
	 *
	 * @param request 请求参数
	 * @return 操作结果
	 */
	@DeleteMapping("/zset/remove")
	public RedisDataResponse removeZSetMembers(@RequestBody RedisDataRequest request) {
		try {
			@SuppressWarnings("unchecked")
			List<Object> values = (List<Object>) request.getValue();
			Long count = redisManageService.removeZSetMembers(request.getKey(), values, request.getDbIndex());
			return RedisDataResponse.success("移除成功", count);
		} catch (Exception e) {
			log.error("移除ZSet成员失败", e);
			return RedisDataResponse.error("移除ZSet成员失败: " + e.getMessage());
		}
	}

	/**
	 * 判断Key是否存在
	 *
	 * @param key 键
	 * @param dbIndex 数据库索引,可选
	 * @return 是否存在
	 */
	@GetMapping("/exists")
	public RedisDataResponse exists(@RequestParam String key, @RequestParam(required = false) Integer dbIndex) {
		try {
			Boolean exists = redisManageService.exists(key, dbIndex);
			return RedisDataResponse.success(exists);
		} catch (Exception e) {
			log.error("判断Key存在性失败, key: {}", key, e);
			return RedisDataResponse.error("判断Key存在性失败: " + e.getMessage());
		}
	}

	/**
	 * 设置Key过期时间
	 *
	 * @param key 键
	 * @param expireTime 过期时间(秒)
	 * @param dbIndex 数据库索引,可选
	 * @return 操作结果
	 */
	@PutMapping("/expire")
	public RedisDataResponse setExpire(@RequestParam String key, @RequestParam Long expireTime,
			@RequestParam(required = false) Integer dbIndex) {
		try {
			Boolean result = redisManageService.expire(key, expireTime, dbIndex);
			if (result) {
				return RedisDataResponse.success("设置过期时间成功", null);
			} else {
				return RedisDataResponse.error("设置过期时间失败");
			}
		} catch (Exception e) {
			log.error("设置过期时间失败, key: {}", key, e);
			return RedisDataResponse.error("设置过期时间失败: " + e.getMessage());
		}
	}

	/**
	 * 获取Key剩余过期时间
	 *
	 * @param key 键
	 * @param dbIndex 数据库索引,可选
	 * @return 剩余时间(秒)
	 */
	@GetMapping("/ttl")
	public RedisDataResponse getTTL(@RequestParam String key, @RequestParam(required = false) Integer dbIndex) {
		try {
			Long ttl = redisManageService.getExpire(key, dbIndex);
			return RedisDataResponse.success(ttl);
		} catch (Exception e) {
			log.error("获取TTL失败, key: {}", key, e);
			return RedisDataResponse.error("获取TTL失败: " + e.getMessage());
		}
	}

	/**
	 * 获取当前数据库索引
	 *
	 * @return 当前数据库索引
	 */
	@GetMapping("/current-db")
	public RedisDataResponse getCurrentDatabase() {
		try {
			Integer dbIndex = redisManageService.getCurrentDatabase();
			Map<String, Object> data = new HashMap<>();
			data.put("dbIndex", dbIndex);
			return RedisDataResponse.success(data);
		} catch (Exception e) {
			log.error("获取当前数据库索引失败", e);
			return RedisDataResponse.error("获取当前数据库索引失败: " + e.getMessage());
		}
	}

	/**
	 * 清空数据库
	 *
	 * @return 操作结果
	 */
	@DeleteMapping("/flush-db")
	public RedisDataResponse flushDatabase(@RequestParam(required = false) Integer dbIndex) {
		try {
			Boolean result = redisManageService.flushDb(dbIndex);
			if (result) {
				return RedisDataResponse.success("清空当前数据库成功", null);
			} else {
				return RedisDataResponse.error("清空当前数据库失败");
			}
		} catch (Exception e) {
			log.error("清空当前数据库失败", e);
			return RedisDataResponse.error("清空当前数据库失败: " + e.getMessage());
		}
	}

	/**
	 * 清空所有数据库
	 *
	 * @return 操作结果
	 */
	@DeleteMapping("/flush-all")
	public RedisDataResponse flushAll() {
		try {
			Boolean result = redisManageService.flushAll();
			if (result) {
				return RedisDataResponse.success("清空所有数据库成功", null);
			} else {
				return RedisDataResponse.error("清空所有数据库失败");
			}
		} catch (Exception e) {
			log.error("清空所有数据库失败", e);
			return RedisDataResponse.error("清空所有数据库失败: " + e.getMessage());
		}
	}
}
