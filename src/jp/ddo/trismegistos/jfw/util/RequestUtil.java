package jp.ddo.trismegistos.jfw.util;

import javax.servlet.http.HttpServletRequest;

/**
 * リクエストURLを操作するUtilクラス.
 * 
 * @author y_sugasawa
 * @since 2011/12/31
 * @author $Author: aigis.developer@gmail.com $
 * @version $Rev: 3 <br>
 *          $ $Date: 2012-06-09 11:33:27 +0900 (土, 09 6 2012) $<br>
 *          $Author: aigis.developer@gmail.com $
 */
public class RequestUtil {

	/**
	 * プライベートコンストラクタ.
	 */
	private RequestUtil() {
	}

	/**
	 * リクエストからパスを取得する.
	 * 
	 * @param request リクエスト
	 * @return パス
	 * @throws NullPointerException
	 */
	public static String getPath(final HttpServletRequest request) throws NullPointerException {
		if (request == null) {
			throw new NullPointerException("request parameter is null.");
		}

		return request.getServletPath();
	}

	/**
	 * 拡張子を取得する.
	 * 
	 * @param path リクエストURL
	 * @return 拡張子
	 * @throws NullPointerException
	 */
	public static String getExtension(final String path) throws NullPointerException {
		if (path == null) {
			throw new NullPointerException("path parameter is null.");
		}

		final int dotIndex = path.lastIndexOf(".");
		if (dotIndex < 0) {
			return null;
		}

		final int sIndex = path.lastIndexOf("/");
		if (sIndex < 0 || dotIndex > sIndex) {
			return path.substring(dotIndex + 1);
		}

		return null;
	}
}
