package dream.flying.flower.autoconfigure.mybatis.plus.handler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

import dream.flying.flower.ConstChar;
import dream.flying.flower.ConstDigit;
import dream.flying.flower.autoconfigure.mybatis.plus.properties.DreamMybatisPlusProperties;
import dream.flying.flower.collection.MapHelper;
import dream.flying.flower.lang.ObjectHelper;
import dream.flying.flower.lang.StrHelper;
import lombok.RequiredArgsConstructor;

/**
 * 当使用MyBatisPlus的注解{@link TableField#fill()}且值为{@link FieldFill#INSERT/UPDATE}时会调用该方法
 * 
 * <pre>
 * 1.需要配置插入或更新的字段Java属性名
 * 2.插入/更新时,若字段符合配置,根据以下规则设置值:
 * 	->若该字段未指定默认值,则数字类型设置为0,时间类型设置为当前时间,布尔类型设置为false,其他类型为null
 * 	->若该字段指定默认值,则使用默认值
 * </pre>
 *
 * @author 飞花梦影
 * @date 2023-01-11 15:33:13
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@RequiredArgsConstructor
public abstract class AbstractMetaObjectHandler implements MetaObjectHandler {

	protected final DreamMybatisPlusProperties dreamMybatisPlusProperties;

	/**
	 * 插入时的填充策略
	 * 
	 * @param metaObject 元数据
	 */
	@Override
	public void insertFill(MetaObject metaObject) {
		Map<String, Object> insertFields = dreamMybatisPlusProperties.getInsertFields();
		if (MapHelper.isEmpty(insertFields)) {
			return;
		}
		for (Map.Entry<String, Object> entry : insertFields.entrySet()) {
			handlerField(metaObject, entry.getKey(), entry.getValue());
		}
	}

	/**
	 * 更新时的填充策略
	 * 
	 * @param metaObject 元数据
	 */
	@Override
	public void updateFill(MetaObject metaObject) {
		Map<String, Object> insertFields = dreamMybatisPlusProperties.getInsertFields();
		if (MapHelper.isEmpty(insertFields)) {
			return;
		}
		for (Map.Entry<String, Object> entry : insertFields.entrySet()) {
			handlerField(metaObject, entry.getKey(), entry.getValue());
		}
	}

	protected void handlerField(MetaObject metaObject, String fieldName, Object defaultValue) {
		String property = metaObject.findProperty(fieldName, false);
		if (StrHelper.isBlank(property)) {
			return;
		}
		// 处理字段
		Class<?> fieldTypeClass = metaObject.getGetterType(fieldName);
		Object fieldValue = this.getFieldValByName(fieldName, metaObject);
		if (Objects.isNull(fieldValue)) {
			if (long.class == fieldTypeClass || Long.class == fieldTypeClass || int.class == fieldTypeClass
					|| Integer.class == fieldTypeClass || short.class == fieldTypeClass || Short.class == fieldTypeClass
					|| byte.class == fieldTypeClass || Byte.class == fieldTypeClass || double.class == fieldTypeClass
					|| Double.class == fieldTypeClass || float.class == fieldTypeClass
					|| Float.class == fieldTypeClass) {
				this.setFieldValByName(fieldName, ObjectHelper.defaultIfNull(defaultValue, ConstDigit.ZERO),
						metaObject);
				// strictInsertFill(metaObject, fieldName, Long.class, defaultValue);
			} else if (char.class == fieldTypeClass || Character.class == fieldTypeClass) {
				this.setFieldValByName(fieldName, ObjectHelper.defaultIfNull(defaultValue, ConstChar.ZERO), metaObject);
			} else if (boolean.class == fieldTypeClass || Boolean.class == fieldTypeClass) {
				this.setFieldValByName(fieldName, ObjectHelper.defaultIfNull(defaultValue, false), metaObject);
			} else if (BigDecimal.class == fieldTypeClass) {
				this.setFieldValByName(fieldName, ObjectHelper.defaultIfNull(defaultValue, BigDecimal.ZERO),
						metaObject);
			} else if (Date.class == fieldTypeClass) {
				this.setFieldValByName(fieldName, ObjectHelper.defaultIfNull(defaultValue, new Date()), metaObject);
			} else if (LocalDate.class == fieldTypeClass) {
				this.setFieldValByName(fieldName, ObjectHelper.defaultIfNull(defaultValue, LocalDate.now()),
						metaObject);
			} else if (LocalDateTime.class == fieldTypeClass) {
				this.setFieldValByName(fieldName, ObjectHelper.defaultIfNull(defaultValue, LocalDateTime.now()),
						metaObject);
			} else {
				this.setFieldValByName(fieldName, ObjectHelper.defaultIfNull(defaultValue, null), metaObject);
			}
		}
	}
}