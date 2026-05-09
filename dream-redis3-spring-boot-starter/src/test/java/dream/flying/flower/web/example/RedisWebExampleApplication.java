package dream.flying.flower.web.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Redis Web管理功能使用示例
 * 
 * 启动应用后，可以通过以下API访问Redis：
 * 
 * 1. 获取所有Key: GET http://localhost:8080/api/redis/keys
 * 2. 设置String值: POST http://localhost:8080/api/redis/set
 *    Body: {"key":"test","value":"hello","expireTime":300}
 * 3. 获取String值: GET http://localhost:8080/api/redis/get?key=test&dataType=string
 * 4. 切换数据库: POST http://localhost:8080/api/redis/select-db?dbIndex=1
 * 5. 删除Key: DELETE http://localhost:8080/api/redis/delete?key=test
 * 
 * @author 飞花梦影
 * @date 2026-05-09 15:41:00
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@SpringBootApplication
public class RedisWebExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisWebExampleApplication.class, args);
	}
}
