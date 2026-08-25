package dream.flying.flower.autoconfigure.localize.handler;

import java.util.Locale;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import dream.flying.flower.autoconfigure.localize.service.LocalizeService;
import lombok.RequiredArgsConstructor;

/**
 * 自定义MessageSource,先从缓存获取,再从资源文件获取
 *
 * @author 飞花梦影
 * @date 2026-08-25 11:23:23
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RequiredArgsConstructor
public class LocalizeMessageSource extends ReloadableResourceBundleMessageSource {

	private final LocalizeService localizeService;

	@Override
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		// 1.优先从数据库读取国际化文案
		String message = localizeService.getMessage(code, locale.toString());
		if (message != null) {
			return message;
		}
		// 2.数据库没有,则读取properties文件兜底
		return super.resolveCodeWithoutArguments(code, locale);
	}
}