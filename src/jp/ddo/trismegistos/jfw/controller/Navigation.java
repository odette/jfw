package jp.ddo.trismegistos.jfw.controller;

/**
 * アプリケーションの遷移情報を格納するクラス.
 * 
 * @author y_sugasawa
 * @since 2011/12/30
 * @version $Rev: 19 $<br>
 *          $Date: 2012-06-09 11:33:27 +0900 (土, 09 6 2012) $<br>
 *          $Author: aigis.developer@gmail.com $
 */
public class Navigation {

	/** 遷移先パス. */
	private String path;

	/** リダイレクトフラグ. */
	private boolean redirect = false;

	/**
	 * コンストラクタ.
	 * 
	 * @param path 遷移先パス
	 * @param redirect リダイレクトフラグ
	 */
	public Navigation(final String path, final boolean redirect) {
		this.path = path;
		this.redirect = redirect;
	}

	/**
	 * pathのゲッター.
	 * 
	 * @return 遷移先パス
	 */
	public String getPath() {
		return path;
	}

	/**
	 * redirectのゲッター
	 * 
	 * @return リダイレクトフラグ
	 */
	public boolean isRedirect() {
		return redirect;
	}
}
