package jp.ddo.trismegistos.jfw.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapの内容をbeanにコピーする.
 * 
 * @author y_sugasawa
 * @since 2012/03/31
 * @version $Rev: 19 $<br>
 *          $Date: 2012-06-09 11:33:27 +0900 (土, 09 6 2012) $<br>
 *          $Author: aigis.developer@gmail.com $
 */
public class CopyUtil {

	public static final String ID = "$Author: aigis.developer@gmail.com $";

	/**
	 * プライベートコンストラクタ.
	 */
	private CopyUtil() {
	}

	/**
	 * Mapの内容を指定されたObjectのフィールドにセットする.
	 * 
	 * @param obj セット先Object
	 * @param map map
	 */
	public static void copy(final Object obj, final HashMap<String, Object> map) {

		for (final Map.Entry<String, Object> entry : map.entrySet()) {
			try {
				final Field f = obj.getClass().getDeclaredField(entry.getKey());
				if (f != null) {
					f.setAccessible(true);
					f.set(obj, entry.getValue());
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
