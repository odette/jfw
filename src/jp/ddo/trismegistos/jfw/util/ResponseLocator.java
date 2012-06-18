package jp.ddo.trismegistos.jfw.util;

import javax.servlet.http.HttpServletResponse;

/**
 * 各スレッドのHttpServletResponseを保存するためのクラス.
 * 
 * @author y_sugasawa
 * @since 2012/05/13
 * @version $Rev: 19 $<br>
 *          $Date: 2012-06-09 11:33:27 +0900 (土, 09 6 2012) $<br>
 *          $Author: aigis.developer@gmail.com $
 */
public final class ResponseLocator {

	private static ThreadLocal<HttpServletResponse> responses = new ThreadLocal<HttpServletResponse>();

	/**
	 * プライベートコンストラクタ.
	 */
	private ResponseLocator() {
	}

	/**
	 * HttpServletResponseを取得する.
	 * 
	 * @return HttpServletResponse
	 */
	public static HttpServletResponse get() {
		return responses.get();
	}

	/**
	 * HttpServletResponseを保存する.
	 * 
	 * @param response HttpServletResponse
	 */
	public static void set(final HttpServletResponse response) {
		responses.set(response);
	}

	/**
	 * HttpServletResponseを削除する.
	 */
	public static void remove() {
		responses.remove();
	}
}
