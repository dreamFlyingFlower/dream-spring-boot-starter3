package dream.flying.flower.autoconfigure.web.properties;

import java.util.List;

import lombok.Data;

/**
 * 白名单配置
 *
 * @author 飞花梦影
 * @date 2022-12-09 17:47:47
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Data
public class WhiteListProperties {

	private Boolean enabled;

	/**
	 * 白名单列表
	 * 
	 * @return List<String>
	 */
	private List<String> whiteLists;

	/**
	 * 黑名单列表
	 * 
	 * @return List<String>
	 */
	private List<String> blackLists;
}