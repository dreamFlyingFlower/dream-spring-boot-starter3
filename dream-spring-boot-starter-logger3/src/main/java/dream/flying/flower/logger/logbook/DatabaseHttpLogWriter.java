package dream.flying.flower.logger.logbook;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Async;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Precorrelation;

import com.fasterxml.jackson.databind.ObjectMapper;

import dream.flying.flower.logger.entity.HttpRequestLog;
import dream.flying.flower.logger.mapper.HttpRequestLogMapper;
import dream.flying.flower.logger.properties.LoggerProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据库HTTP日志记录器 将HTTP请求响应日志保存到数据库中
 *
 * @author 飞花梦影
 * @date 2024-01-06 15:30:45
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class DatabaseHttpLogWriter implements HttpLogWriter {

	private final HttpRequestLogMapper logMapper;

	private final LoggerProperties properties;

	private final ObjectMapper objectMapper;
    
    // 添加请求信息暂存Map
    private final Map<String, RequestInfo> requestStorage = new ConcurrentHashMap<>();

	@Async("operationLogExecutor")
	@Override
	public void write(final Precorrelation precorrelation, final String request) throws IOException {
		try {
            // 存储请求信息
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setRequestTime(LocalDateTime.now());
            requestInfo.setRequest(request);
            requestStorage.put(precorrelation.getId(), requestInfo);
        } catch (Exception e) {
            log.error("Failed to store request info", e);
        }
	}

	@Override
	public void write(final Correlation correlation, final String response) throws IOException {
		try {
            // 获取之前存储的请求信息
            RequestInfo requestInfo = requestStorage.remove(correlation.getId());
            if (requestInfo == null) {
                log.warn("Request info not found for correlation id: {}", correlation.getId());
                requestInfo = new RequestInfo();
                requestInfo.setRequestTime(LocalDateTime.now());
            }

			HttpRequestLog logEntity = HttpRequestLog.builder()
					.traceId(correlation.getId())
					.appName(properties.getAppName())
					.requestTime(requestInfo.getRequestTime())
					.responseTime(LocalDateTime.now())
					.costTime(correlation.getDuration().toMillis())
					.requestMethod(correlation.getRequest().getMethod())
					.requestUrl(correlation.getRequest().getPath())
					.requestHeaders(objectMapper.writeValueAsString(correlation.getRequest().getHeaders()))
					.requestBody(requestInfo.getRequest())
					.responseStatus(correlation.getResponse().getStatus())
					.responseHeaders(objectMapper.writeValueAsString(correlation.getResponse().getHeaders()))
					.responseBody(correlation.getResponse().getBodyAsString())
					.clientIp(getClientIp(correlation))
					.createdTime(LocalDateTime.now())
					.build();

			logMapper.insert(logEntity);
		} catch (Exception e) {
			log.error("Failed to save HTTP request log for correlation id: {}", correlation.getId(), e);
		}
	}

	private String getClientIp(Correlation correlation) {
		String ip = correlation.getRequest().getHeaders().get("X-Forwarded-For");
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = correlation.getRequest().getHeaders().get("Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = correlation.getRequest().getHeaders().get("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = correlation.getRequest().getRemote();
		}
		return ip;
	}

    // 添加请求信息存储类
    @Data
    private static class RequestInfo {
        private LocalDateTime requestTime;
        private String request;
    }
}