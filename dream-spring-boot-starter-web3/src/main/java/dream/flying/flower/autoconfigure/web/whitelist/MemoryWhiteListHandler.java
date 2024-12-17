package dream.flying.flower.autoconfigure.web.whitelist;

import java.util.List;

import org.springframework.util.CollectionUtils;

import dream.flying.flower.autoconfigure.web.properties.WhiteListProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 默认内存白名单
 *
 * @author 飞花梦影
 * @date 2024-12-17 22:22:26
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@AllArgsConstructor
public class MemoryWhiteListHandler implements WhiteListHandler {

	private WhiteListProperties whiteListProperties;

	@Override
	public boolean enabled() {
		return whiteListProperties.getEnabled();
	}

	@Override
	public List<String> whiteLists() {
		return whiteListProperties.getWhiteLists();
	}

	@Override
	public List<String> blackLists() {
		return whiteListProperties.getBlackLists();
	}

	@Override
	public boolean whiteList(String ip) {
		return CollectionUtils.isEmpty(whiteListProperties.getWhiteLists()) ? false
				: whiteListProperties.getWhiteLists().contains(ip);
	}

	@Override
	public boolean blackList(String ip) {
		return CollectionUtils.isEmpty(whiteListProperties.getBlackLists()) ? false
				: whiteListProperties.getBlackLists().contains(ip);
	}
}