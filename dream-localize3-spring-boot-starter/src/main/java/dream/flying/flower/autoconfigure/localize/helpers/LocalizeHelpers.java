package dream.flying.flower.autoconfigure.localize.helpers;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import dream.flying.flower.lang.StrHelper;

/**
 * 本地化工具类
 *
 * @author 飞花梦影
 * @date 2026-08-26 14:13:02
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class LocalizeHelpers {

	/**
	 * 获取本地国际标准Locale字符串
	 *
	 * @return language code (e.g., zh-CN, en-US)
	 */
	public static String getLang() {
		return getLang(Locale.getDefault());
	}

	/**
	 * 获取国际标准Locale字符串
	 *
	 * @param locale locale
	 * @return language code (e.g., zh-CN, en-US)
	 */
	public static String getLang(Locale locale) {
		if (locale == null) {
			locale = Locale.getDefault();
		}
		return locale.toLanguageTag();
	}

	/**
	 * 构建标准Locale对象
	 * 
	 * @param language 语言
	 * @param script 区域脚本代码
	 * @param country 国家/区域
	 * @param variant 区域变体代码
	 * @return Locale
	 */
	public static Locale of(String language, String script, String country, String variant) {
		Locale.Builder builder = new Locale.Builder();
		if (StrHelper.isNotBlank(language)) {
			builder.setLanguage(language.toLowerCase());
		}
		if (StrHelper.isNotBlank(script)) {
			builder.setScript(StrHelper.firstUpper(script.toLowerCase()));
		}
		if (StrHelper.isNotBlank(country)) {
			builder.setRegion(country.toUpperCase());
		}
		if (StrHelper.isNotBlank(variant)) {
			builder.setVariant(variant);
		}
		return builder.build();
	}

	/**
	 * 构建国际格式的语言字符串
	 * 
	 * @param language 语言
	 * @param script 区域脚本代码
	 * @param country 国家/区域
	 * @param variant 区域变体代码
	 * @return 国际格式的语言字符串,语言和国家之间用-,而不是_
	 */
	public static String toStandard(String language, String script, String country, String variant) {
		Locale locale = of(language, script, country, variant);
		return locale.toLanguageTag();
	}

	/**
	 * 构建Java格式的语言字符串
	 * 
	 * @param language 语言
	 * @param script 区域脚本代码
	 * @param country 国家/区域
	 * @param variant 区域变体代码
	 * @return Java格式的语言字符串,语言和国家之间用_,而不是-
	 */
	public static String toJavaFormat(String language, String script, String country, String variant) {
		Locale locale = of(language, script, country, variant);
		return locale.toString();
	}

	/**
	 * 将Java格式的语言字符串转换为国际格式的字符串
	 * 
	 * @param language Java格式语言字符串,如zh_CN -> zh-CN
	 * @return 国际格式语言字符串
	 */
	public static Locale parse(String language) {
		String normalized = language.replace('_', '-');
		normalized = normalized.replaceAll("-+", "-");
		normalized = normalized.replaceAll("^-|-$", "");
		return Locale.forLanguageTag(normalized);
	}

	/**
	 * 语言降级
	 * 
	 * @param language 语言
	 * @return 从所有参数都存在到只有语言和国家/区域参数
	 */
	public static List<String> buildFallback(String language) {
		Set<String> chain = new LinkedHashSet<>();
		Locale locale = parse(language);
		String lang = locale.getLanguage();
		String script = locale.getScript();
		String country = locale.getCountry();
		String variant = locale.getVariant();

		chain.add(toStandard(lang, script, country, variant));
		chain.add(toJavaFormat(lang, script, country, variant));

		if (!variant.isEmpty()) {
			chain.add(toStandard(lang, script, country, ""));
			chain.add(toJavaFormat(lang, script, country, ""));
		}
		if (!country.isEmpty()) {
			if (!script.isEmpty()) {
				chain.add(toStandard(lang, script, "", ""));
				chain.add(toJavaFormat(lang, script, "", ""));
			}
			chain.add(toStandard(lang, "", country, ""));
			chain.add(toJavaFormat(lang, "", country, ""));
		}
		if (!script.isEmpty()) {
			chain.add(toStandard(lang, "", "", ""));
			chain.add(toJavaFormat(lang, "", "", ""));
		}
		chain.add(lang);

		return new ArrayList<>(chain);
	}
}