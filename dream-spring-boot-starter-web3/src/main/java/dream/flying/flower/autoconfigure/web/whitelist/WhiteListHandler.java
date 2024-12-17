package dream.flying.flower.autoconfigure.web.whitelist;

import java.util.List;

/**
 * 白名单数据管理
 *
 * @author 飞花梦影
 * @date 2024-12-17 14:07:24
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface WhiteListHandler {

	boolean enabled();

	/**
	 * 白名单列表
	 * 
	 * @return List<String>
	 */
	List<String> whiteLists();

	/**
	 * 黑名单列表
	 * 
	 * @return List<String>
	 */
	List<String> blackLists();

	/**
	 * 是否为白名单
	 * 
	 * @param ip 待判断IP
	 * @return true->是;false->否
	 */
	boolean whiteList(String ip);

	/**
	 * 是否为黑名单
	 * 
	 * @param ip 待判断IP
	 * @return true->是;false->否
	 */
	boolean blackList(String ip);
}