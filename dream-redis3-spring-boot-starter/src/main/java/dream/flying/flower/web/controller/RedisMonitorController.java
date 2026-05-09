package dream.flying.flower.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dream.flying.flower.web.dto.RedisDataResponse;
import dream.flying.flower.web.monitor.RedisDbSwitchMonitor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis监控控制器
 *
 * @author 飞花梦影
 * @date 2026-05-09 16:30:00
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Slf4j
@RestController
@RequestMapping("/api/redis/monitor")
public class RedisMonitorController {

	@Autowired(required = false)
	private RedisDbSwitchMonitor monitor;

	/**
	 * 获取监控统计数据
	 *
	 * @return 监控数据
	 */
	@GetMapping("/stats")
	public RedisDataResponse getStats() {
		if (monitor == null) {
			return RedisDataResponse.error("监控功能未启用");
		}

		Map<String, Object> stats = new HashMap<>();
		stats.put("totalRequests", monitor.getTotalRequests().get());
		stats.put("skipSwitchCount", monitor.getSkipSwitchCount().get());
		stats.put("performSwitchCount", monitor.getPerformSwitchCount().get());
		stats.put("noDbIndexCount", monitor.getNoDbIndexCount().get());
		stats.put("skipSwitchRate", String.format("%.2f%%", monitor.getSkipSwitchRate()));
		stats.put("performSwitchRate", String.format("%.2f%%", monitor.getPerformSwitchRate()));
		stats.put("estimatedSavedCommands", monitor.getEstimatedSavedCommands());

		return RedisDataResponse.success(stats);
	}

	/**
	 * 获取监控报告
	 *
	 * @return 监控报告
	 */
	@GetMapping("/report")
	public RedisDataResponse getReport() {
		if (monitor == null) {
			return RedisDataResponse.error("监控功能未启用");
		}

		String report = monitor.generateReport();
		return RedisDataResponse.success(report);
	}

	/**
	 * 重置统计数据
	 *
	 * @return 操作结果
	 */
	@PostMapping("/reset")
	public RedisDataResponse resetStats() {
		if (monitor == null) {
			return RedisDataResponse.error("监控功能未启用");
		}

		monitor.reset();
		log.info("Redis监控统计数据已重置");
		return RedisDataResponse.success("统计数据已重置", null);
	}
}
