package dream.flying.flower.autoconfigure.localize.handler;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import dream.flying.flower.autoconfigure.localize.helpers.LocalizeHelpers;
import dream.flying.flower.autoconfigure.localize.service.LocalizeService;
import lombok.RequiredArgsConstructor;

/**
 * Custom MessageSource that resolves messages from the LocalizeService first,
 * and falls back to the provided default message/code.
 *
 * <p>
 * Language tag is always produced in BCP-47 format via
 * {@link LocalizeHelpers#getLang(Locale)} to keep a single format throughout
 * the service layer cache keys and database fullLang column.
 * </p>
 *
 * @author 飞花梦影
 * @date 2026-08-25 11:23:23
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RequiredArgsConstructor
public class LocalizeMessageSource implements MessageSource {

	private final LocalizeService localizeService;

	@Override
	public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		String lang = LocalizeHelpers.getLang(locale);
		String message = localizeService.getMessage(code, lang);
		if (message != null && !code.equals(message)) {
			return message;
		}
		return defaultMessage != null ? defaultMessage : code;
	}

	@Override
	public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
		return getMessage(code, args, null, locale);
	}

	@Override
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		String[] codes = resolvable.getCodes();
		if (codes != null) {
			for (String code : codes) {
				String message = getMessage(code, null, locale);
				if (message != null && !code.equals(message)) {
					return message;
				}
			}
		}
		return resolvable.getDefaultMessage();
	}
}
