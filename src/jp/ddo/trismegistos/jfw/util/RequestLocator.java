package jp.ddo.trismegistos.jfw.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 各スレッドのHttpServletRequestを保存するためのクラス.
 * 
 * @author y_sugasawa
 * @since 2012/05/13
 * @version $Rev: 19 $<br>
 *          $Date: 2012-06-09 11:33:27 +0900 (土, 09 6 2012) $<br>
 *          $Author: aigis.developer@gmail.com $
 */
public final class RequestLocator {

	private static ThreadLocal<HttpServletRequest> requests = new ThreadLocal<HttpServletRequest>();

	/**
	 * プライベートコンストラクタ.
	 */
	private RequestLocator() {
	}

	/**
	 * HttpServletRequestを取得する.
	 * 
	 * @return HttpServletRequest
	 */
	public static HttpServletRequest get() {
		return requests.get();
	}

	/**
	 * HttpServletRequestを保存する.
	 * 
	 * @param request HttpServletRequest
	 */
	public static void set(final HttpServletRequest request) {
		requests.set(request);
	}

	/**
	 * HttpServletRequestを削除する.
	 */
	public static void remove() {
		requests.remove();
	}
}
