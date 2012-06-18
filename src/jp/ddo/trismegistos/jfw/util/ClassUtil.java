package jp.ddo.trismegistos.jfw.util;

import jp.ddo.trismegistos.jfw.exception.WrapRuntimeException;

/**
 * インスタンス生成用のUtilクラス.
 * 
 * @author y_sugasawa
 * @since 2012/03/31
 * @version $Rev: 19 $<br>
 *          $Date: 2012-06-09 11:33:27 +0900 (土, 09 6 2012) $<br>
 *          $Author: aigis.developer@gmail.com $
 */
public class ClassUtil {

	/**
	 * プライベートコンストラクタ.
	 */
	private ClassUtil() {
	}

	/**
	 * 指定されたクラスのインスタンスを生成する.
	 * 
	 * @param className クラス名
	 * @return インスタンス
	 */
	public static Object newInstance(final String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 指定されたクラスのインスタンスを生成する.
	 * 
	 * @param clazz クラス
	 * @return インスタンス
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(final Class<?> clazz) {

		try {
			return (T) clazz.newInstance();
		} catch (final Throwable t) {
			throw new WrapRuntimeException("An error creating a new instance. ClassName is " + clazz.getName()
					+ ". Cause is " + t.getCause() + ".", t);
		}
	}
}
