package jp.ddo.trismegistos.jfw.controller;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/**
 * HTTPリクエストハンドラ.
 * 
 * @author y_sugasawa
 * @since 2012/01/02
 * @version $Rev: 19 $<br>
 *          $Date: 2012-06-09 11:33:27 +0900 (土, 09 6 2012) $<br>
 *          $Author: aigis.developer@gmail.com $
 */
public class RequestHandler {

	/** 配列の接尾語. */
	private static final String ARRAY_SUFFIX = "Array";

	/** HTTPリクエスト. */
	protected HttpServletRequest request;

	/**
	 * コンストラクタ.
	 * 
	 * @param request
	 */
	public RequestHandler(final HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * リクエストに対する前処理を行う.
	 */
	public void handle() {
		for (final Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
			final String name = e.nextElement();
			if (request.getAttribute(name) != null) {
				continue;
			}

			if (name.endsWith(ARRAY_SUFFIX)) {
				request.setAttribute(name, normalizeValues(request.getParameterValues(name)));
			} else {
				request.setAttribute(name, normalizeValue(request.getParameter(name)));
			}
		}
	}

	/**
	 * パラメータのエスケープを行う.
	 * 
	 * @param value パラメータ
	 * @return エスケープされたパラメータ
	 */
	protected String normalizeValue(final String value) {
		return value.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;")
				.replaceAll("\"", "&quot;").replaceAll("\n", "<br>");
	}

	/**
	 * normalizeValueメソッドの配列版.
	 * 
	 * @param values パラメータ配列
	 * @return
	 */
	protected String[] normalizeValues(final String[] values) {
		if (values == null) {
			return new String[0];
		}

		final String[] ret = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			ret[i] = normalizeValue(values[i]);
		}
		return ret;
	}
}
