package jp.ddo.trismegistos.jfw.controller;

/**
 * フレームワーク定数定義クラス。
 * 
 * @author y_sugasawa
 * @since 2011/12/30
 */
public final class ControllerConstants {

	/** ルートパッケージKey. */
	public static final String ROOT_PACKAGE_KEY = "rootPackage";

	/** 文字コードKey. */
	public static final String CHARSET_KEY = "charset";

	/** デフォルト文字コード. */
	public static final String DEFAULT_CHARSET = "UTF-8";

	/** コントローラーパッケージKey. */
	public static final String CONTROLLER_PACKAGE_KEY = "controllerPackage";

	/** デフォルトコントローラーパッケージ名. */
	public static final String DEFAULT_CONTROLLER_PACKAGE_NAME = "controller";

	/** コントローラー接尾語. */
	public static final String CONTROLLER_SUFFIX = "Controller";

	/** インデックスコントローラー名. */
	public static final String INDEX_CONTROLLER = "Index" + CONTROLLER_SUFFIX;

	/** 表示ファイルのベースパス. */
	public static final String BASE_VIEW_PATH = "/WEB-INF/view/";
}
