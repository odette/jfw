package jp.ddo.trismegistos.jfw.controller;

import java.lang.reflect.Modifier;

import jp.ddo.trismegistos.jfw.util.ClassUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * Controllerを作成するFactoryクラス.
 * 
 * @author y_sugasawa
 * @since 2012/06/01
 * @version $Rev: 9 $<br>
 *          $Date: 2012-06-09 11:07:49 +0900 (土, 09 6 2012) $<br>
 *          $Author: aigis.developer@gmail.com $
 * 
 */
public class ControllerFactory {

	/** 自身のインスタンス. */
	private static ControllerFactory factory = new ControllerFactory();

	/** ルートパッケージ名. */
	private String rootPackageName;

	/** コントローラパッケージ名. */
	private String controllerPackageName;

	/**
	 * プライベートコンストラクタ.
	 */
	private ControllerFactory() {
	}

	/**
	 * 初期化処理.
	 * 
	 * @param rootPackageName ルートパッケージ名
	 * @param controllerPackageName コントローラパッケージ名
	 */
	public void init(final String rootPackageName, final String controllerPackageName) {
		this.rootPackageName = rootPackageName;
		this.controllerPackageName = controllerPackageName;
	}

	/**
	 * インスタンスを取得する.
	 * 
	 * @return ControllerFactoryクラスのインスタンス
	 */
	public static ControllerFactory getInstance() {
		return factory;
	}

	/**
	 * コントローラーを作成する.
	 * 
	 * @param path パス
	 * @return コントローラ.存在しないコントローラの場合はnullを返す
	 */
	public Controller create(final String path) {
		if (path.indexOf(".") >= 0) {
			return null;
		}

		return createController(path);
	}

	/**
	 * コントローラーのインスタンスを生成する.
	 * 
	 * @param path パス
	 * @return
	 */
	private Controller createController(final String path) {

		final String className = toControllerClassName(path);
		if (className == null) {
			return null;
		}

		Class<?> clazz = null;
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			clazz = Class.forName(className, true, loader);
		} catch (final Throwable t) {
			return null;
		}
		if (Controller.class.isAssignableFrom(clazz) == false) {
			return null;
		}
		if (Modifier.isAbstract(clazz.getModifiers())) {
			return null;
		}

		return ClassUtil.newInstance(clazz);
	}

	/**
	 * コントローラー名を取得する.
	 * 
	 * @param path
	 * @return コントラーラ名
	 */
	private String toControllerClassName(final String path) {
		String className = rootPackageName + "." + controllerPackageName + path.replace("/", ".");
		if (className.endsWith(".")) {
			className += ControllerConstants.INDEX_CONTROLLER;
		} else {
			final int pos = className.lastIndexOf(".");
			className = className.substring(0, pos + 1) + StringUtils.capitalize(className.substring(pos + 1))
					+ ControllerConstants.CONTROLLER_SUFFIX;
		}

		return className;
	}
}
