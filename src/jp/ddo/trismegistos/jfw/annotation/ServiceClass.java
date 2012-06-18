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
 * @version $Rev: 18 $<br>
 *          $Date: 2012-06-09 11:23:49 +0900 (土, 09 6 2012) $<br>
 *          $Author: aigis.developer@gmail.com $
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceClass {

	String name() default "";
}
