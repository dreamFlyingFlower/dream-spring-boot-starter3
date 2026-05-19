package dream.flying.flower.web.monitor;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * Redis数据库切换监控器
 * 
 * 统计数据库切换的性能指标，用于优化和监控
 *
 * @author 飞花梦影
 * @date 2026-05-09 16:30:00
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
public class RedisDbSwitchMonitor {

	/**
	 * 总请求数
	 */
	@Getter
	private final AtomicLong totalRequests = new AtomicLong(0);

	/**
	 * 跳过切换的次数（dbIndex与当前数据库相同）
	 */
	@Getter
	private final AtomicLong skipSwitchCount = new AtomicLong(0);

	/**
	 * 执行切换的次数（dbIndex与当前数据库不同）
	 */
	@Getter
	private final AtomicLong performSwitchCount = new AtomicLong(0);

	/**
	 * 未指定dbIndex的次数
	 */
	@Getter
	private final AtomicLong noDbIndexCount = new AtomicLong(0);

	/**
	 * 记录请求
	 *
	 * @param dbIndex 数据库索引，null表示未指定
	 * @param switched 是否执行了切换
	 */
	public void recordRequest(Integer dbIndex, boolean switched) {
		totalRequests.incrementAndGet();

		if (dbIndex == null) {
			noDbIndexCount.incrementAndGet();
		} else if (switched) {
			performSwitchCount.incrementAndGet();
		} else {
			skipSwitchCount.incrementAndGet();
		}
	}

	/**
	 * 获取跳过切换的比例
	 *
	 * @return 跳过切换的百分比
	 */
	public double getSkipSwitchRate() {
		long total = totalRequests.get();
		if (total == 0) {
			return 0.0;
		}
		return (double) skipSwitchCount.get() / total * 100;
	}

	/**
	 * 获取执行切换的比例
	 *
	 * @return 执行切换的百分比
	 */
	public double getPerformSwitchRate() {
		long total = totalRequests.get();
		if (total == 0) {
			return 0.0;
		}
		return (double) performSwitchCount.get() / total * 100;
	}

	/**
	 * 获取优化收益估算
	 * <p>
	 * 计算因优化而节省的Redis命令数
	 * </p>
	 *
	 * @return 节省的命令数
	 */
	public long getEstimatedSavedCommands() {
		// 每次跳过切换节省2个SELECT命令
		return skipSwitchCount.get() * 2;
	}

	/**
	 * 重置统计数据
	 */
	public void reset() {
		totalRequests.set(0);
		skipSwitchCount.set(0);
		performSwitchCount.set(0);
		noDbIndexCount.set(0);
	}

	/**
	 * 生成监控报告
	 *
	 * @return 监控报告字符串
	 */
	public String generateReport() {
		StringBuilder sb = new StringBuilder();
		sb.append("=== Redis数据库切换监控报告 ===\n");
		sb.append("总请求数: ").append(totalRequests.get()).append("\n");
		sb.append("跳过切换次数: ").append(skipSwitchCount.get())
				.append(" (").append(String.format("%.2f", getSkipSwitchRate())).append("%)\n");
		sb.append("执行切换次数: ").append(performSwitchCount.get())
				.append(" (").append(String.format("%.2f", getPerformSwitchRate())).append("%)\n");
		sb.append("未指定dbIndex次数: ").append(noDbIndexCount.get()).append("\n");
		sb.append("估计节省命令数: ").append(getEstimatedSavedCommands()).append("\n");
		sb.append("===============================");
		return sb.toString();
	}
}