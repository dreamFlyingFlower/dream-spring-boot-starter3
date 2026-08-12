package dream.flying.flower.autoconfigure.otp;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import dream.flying.flower.framework.constant.ConstConfig;
import dream.flying.flower.framework.otp.properties.DreamOtpProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(DreamOtpProperties.class)
@ConditionalOnProperty(prefix = ConstConfig.Auto.OTP, value = ConstConfig.ENABLED, matchIfMissing = true)
public class OtpAutoConfiguration {

}