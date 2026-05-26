package dream.flying.flower.autoconfigure.i18n.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import dream.flying.flower.framework.mybatis.plus.entity.AbstractTenantEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Localization entity class
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_localization")
public class LocalizationEntity extends AbstractTenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 国际化编码
	 */
	private String messageCode;

	/**
	 * 国际化信息内容
	 */
	private String messageContent;

	/**
	 * 语言: zh_CN, en_US
	 */
	private String lang;

	/**
	 * 国家/地区
	 */
	private String country;

	/**
	 * 区域脚本
	 */
	private String script;

	/**
	 * 区域变体代码
	 */
	private String variant;
}