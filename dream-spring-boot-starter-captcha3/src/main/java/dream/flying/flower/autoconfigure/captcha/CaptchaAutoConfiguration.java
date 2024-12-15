package dream.flying.flower.autoconfigure.captcha;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

import dream.flying.flower.autoconfigure.captcha.properties.DreamCaptchaProperties;
import dream.flying.flower.framework.core.constant.ConstConfigPreix;
import dream.flying.flower.helper.ConvertHepler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(DreamCaptchaProperties.class)
@ConditionalOnProperty(prefix = ConstConfigPreix.CAPTCHA, value = ConstConfigPreix.ENABLED, matchIfMissing = true)
public class CaptchaAutoConfiguration {

	@Bean
	Producer captchaProducer(DreamCaptchaProperties dreamCaptchaProperties) {
		Properties properties = null;
		if (null != dreamCaptchaProperties.getResourceUrl()) {
			try {
				properties = loadProperties(dreamCaptchaProperties.getResourceUrl());
			} catch (IOException e) {
				e.printStackTrace();
				log.error("读取captcha资源文件失败!");
			}
		}

		if (null == properties) {
			properties = convert(dreamCaptchaProperties);
		} else {
			Properties dreamProperties = convert(dreamCaptchaProperties);
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				properties.merge(entry.getKey(), entry.getValue(), (o, n) -> dreamProperties.get(entry.getKey()));
			}
		}

		Config config = new Config(properties);
		DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
		defaultKaptcha.setConfig(config);
		return defaultKaptcha;
	}

	private Properties loadProperties(Resource resource) throws IOException {
		log.debug("captcha config file " + resource.getURL());
		Properties properties = new Properties();
		properties.load(resource.getInputStream());
		return properties;
	}

	private Properties convert(DreamCaptchaProperties dreamCaptchaProperties) {
		Properties properties = new Properties();
		setProperty(properties, Constants.KAPTCHA_BORDER, dreamCaptchaProperties.getBorder());
		setProperty(properties, Constants.KAPTCHA_BORDER_COLOR, dreamCaptchaProperties.getBorderColor());
		setProperty(properties, Constants.KAPTCHA_BORDER_THICKNESS, dreamCaptchaProperties.getBorderThickness());
		setProperty(properties, Constants.KAPTCHA_NOISE_COLOR, dreamCaptchaProperties.getNoiseColor());
		setProperty(properties, Constants.KAPTCHA_NOISE_IMPL, dreamCaptchaProperties.getNoiseImpl());

		setProperty(properties, Constants.KAPTCHA_OBSCURIFICATOR_IMPL, dreamCaptchaProperties.getObscurificatorImpl());

		setProperty(properties, Constants.KAPTCHA_PRODUCER_IMPL, dreamCaptchaProperties.getProducerImpl());
		setProperty(properties, Constants.KAPTCHA_WORDRENDERER_IMPL, dreamCaptchaProperties.getWordImpl());

		setProperty(properties, Constants.KAPTCHA_TEXTPRODUCER_IMPL, dreamCaptchaProperties.getTextproducerImpl());
		setProperty(properties, Constants.KAPTCHA_TEXTPRODUCER_CHAR_STRING,
				dreamCaptchaProperties.getTextproducerCharString());
		setProperty(properties, Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH,
				dreamCaptchaProperties.getTextproducerCharLength());
		setProperty(properties, Constants.KAPTCHA_TEXTPRODUCER_FONT_NAMES,
				dreamCaptchaProperties.getTextproducerFontNames());
		setProperty(properties, Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR,
				dreamCaptchaProperties.getTextproducerFontColor());
		setProperty(properties, Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE,
				dreamCaptchaProperties.getTextproducerFontSize());
		setProperty(properties, Constants.KAPTCHA_TEXTPRODUCER_CHAR_SPACE,
				dreamCaptchaProperties.getTextproducerCharSpace());

		setProperty(properties, Constants.KAPTCHA_BACKGROUND_IMPL, dreamCaptchaProperties.getBackgroundImpl());
		setProperty(properties, Constants.KAPTCHA_BACKGROUND_CLR_FROM, dreamCaptchaProperties.getBackgroundClearFrom());
		setProperty(properties, Constants.KAPTCHA_BACKGROUND_CLR_TO, dreamCaptchaProperties.getBackgroundClearTo());

		setProperty(properties, Constants.KAPTCHA_IMAGE_WIDTH, dreamCaptchaProperties.getImageWidth());
		setProperty(properties, Constants.KAPTCHA_IMAGE_HEIGHT, dreamCaptchaProperties.getImageHeight());

		return properties;
	}

	private void setProperty(Properties properties, String key, Object value) {
		if (null != value) {
			properties.setProperty(key, ConvertHepler.toStr(value));
		}
	}

	private void setProperty(Properties properties, String key, Class<?> value) {
		if (null != value) {
			properties.setProperty(key, value.getName());
		}
	}
}