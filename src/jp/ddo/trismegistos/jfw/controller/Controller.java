package jp.ddo.trismegistos.jfw.controller;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * アプリケーションの実行クラスの基底クラス.
 * 
 * @author y_sugasawa
 * @since 2011/12/30
 * @version $Rev: 19 $<br>
 *          $Date: 2012-06-09 11:33:27 +0900 (土, 09 6 2012) $<br>
 *          $Author: aigis.developer@gmail.com $
 */
public abstract class Controller {

	protected ServletContext servletContext;

	protected HttpServletRequest request;

	protected HttpServletResponse response;

	protected String basePath;

	protected HashMap<String, Object> requestMap;

	public Navigation runBase() throws Exception {
		requestMap = createRequestMap();
		if (doubleSubmitCheck()) {
			if (isDoubleSubmitCheck() == false) {
				System.out.println("ダブルサブミット!!");
				return null;
			}
		}
		saveToken();
		return run();
	}

	protected void saveToken() {
		sessionScope("token", String.valueOf(System.currentTimeMillis()));
	}

	protected boolean doubleSubmitCheck() {
		return false;
	}

	protected boolean isDoubleSubmitCheck() {
		final String before = remoeSessionScope("token");
		if (before != null) {
			return before.equals(requestMap.get("token"));
		}

		return true;
	}

	protected HashMap<String, Object> createRequestMap() {
		final HashMap<String, Object> map = new HashMap<String, Object>();

		for (final Enumeration<String> e = request.getAttributeNames(); e.hasMoreElements();) {
			final String name = e.nextElement();
			map.put(name, request.getAttribute(name));
		}
		return map;
	}

	protected abstract Navigation run() throws Exception;

	/**
	 * forward用のNavigationを作成する。
	 * 
	 * @param path
	 * @return
	 */
	protected Navigation forward(final String path) {
		return new Navigation(path, false);
	}

	/**
	 * redirect用のNavigationを作成する。
	 * 
	 * @param path
	 * @return
	 */
	protected Navigation redirect(final String path) {
		return new Navigation(path, true);
	}

	/**
	 * Applicationスコープから値を取得する。
	 * 
	 * @param <T>
	 * @param name
	 * @return
	 * @throws NullPointerException
	 */
	@SuppressWarnings("unchecked")
	protected <T> T applicationScope(final String name) throws NullPointerException {
		if (name == null) {
			throw new NullPointerException("parameter name is null.");
		}

		return (T) servletContext.getAttribute(name.toString());
	}

	/**
	 * Applicationスコープに値を設定する。
	 * 
	 * @param name
	 * @param value
	 * @throws NullPointerException
	 */
	protected void applicationScope(final String name, final Object value) throws NullPointerException {
		if (name == null) {
			throw new NullPointerException("parameter name is null.");
		}

		servletContext.setAttribute(name.toString(), value);
	}

	/**
	 * Applicationスコープから値を削除する。
	 * 
	 * @param <T>
	 * @param name
	 * @return
	 * @throws NullPointerException
	 */
	@SuppressWarnings("unchecked")
	protected <T> T removeApplicationScope(final String name) throws NullPointerException {
		if (name == null) {
			throw new NullPointerException("parameter name is null.");
		}

		final T value = (T) servletContext.getAttribute(name.toString());
		servletContext.removeAttribute(name.toString());
		return value;
	}

	/**
	 * Sessionスコープから値を取得する。
	 * 
	 * @param <T>
	 * @param name
	 * @return
	 * @throws NullPointerException
	 */
	@SuppressWarnings("unchecked")
	protected <T> T sessionScope(final String name) throws NullPointerException {
		if (name == null) {
			throw new NullPointerException("parameter name is null.");
		}

		final HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}

		final String n = name.toString();
		final T value = (T) session.getAttribute(n);
		if (value != null) {
			session.setAttribute(n, value);
		}
		return value;
	}

	/**
	 * Sessionスコープに値を設定する。
	 * 
	 * @param name
	 * @param value
	 * @throws NullPointerException
	 */
	protected void sessionScope(final String name, final Object value) throws NullPointerException {
		if (name == null) {
			throw new NullPointerException("parameter name is null.");
		}

		request.getSession().setAttribute(name.toString(), value);
	}

	/**
	 * Sessionスコープから値を削除する。
	 * 
	 * @param <T>
	 * @param name
	 * @return
	 * @throws NullPointerException
	 */
	@SuppressWarnings("unchecked")
	protected <T> T remoeSessionScope(final String name) throws NullPointerException {
		if (name == null) {
			throw new NullPointerException("parameter name is null.");
		}

		final HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}

		final T value = (T) session.getAttribute(name.toString());
		session.removeAttribute(name.toString());
		return value;
	}

	/**
	 * Requestスコープに値を設定する.
	 * 
	 * @param name
	 * @param value
	 * @throws NullPointerException
	 */
	protected void requestScope(final String name, final Object value) throws NullPointerException {
		if (name == null) {
			throw new NullPointerException("parameter name is null.");
		}

		request.setAttribute(name.toString(), value);
	}

	/**
	 * 文字列を出力する.
	 * 
	 * @param str 出力文字列.
	 * @return 常にnull
	 * @throws IOException
	 */
	protected Navigation writePlainText(final String str) throws IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		Writer w = null;
		try {
			w = response.getWriter();
			w.write(str);
		} finally {
			if (w != null) {
				try {
					w.close();
				} catch (final IOException e) {
				}
			}
		}

		return null;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	protected RequestHandler createRequestHandler(final HttpServletRequest request) {
		return new RequestHandler(request);
	}
}
