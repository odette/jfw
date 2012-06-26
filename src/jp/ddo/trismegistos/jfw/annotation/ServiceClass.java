package jp.ddo.trismegistos.jfw.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Serviceクラス判別用アノテーション.
 * 
 * @author y_sugasawa
 * @since 2012/03/31
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceClass {

	String name() default "";
}
