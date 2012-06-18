package jp.ddo.trismegistos.jfw.controller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.ddo.trismegistos.jfw.annotation.ServiceClass;
import jp.ddo.trismegistos.jfw.service.Service;
import jp.ddo.trismegistos.jfw.util.ClassUtil;
import jp.ddo.trismegistos.jfw.util.DBConnectionPool;
import jp.ddo.trismegistos.jfw.util.RequestLocator;
import jp.ddo.trismegistos.jfw.util.RequestUtil;
import jp.ddo.trismegistos.jfw.util.ResponseLocator;

import org.apache.commons.lang3.StringUtils;

/**
 * リクエストURLに対して実行するControllerを生成し、実行する.<br>
 * すべてのリクエストの入り口.
 * 
 * @author y_sugasawa
 * @since 2012/03/31
 * @version $Rev: 19 $<br>
 *          $Date: 2012-06-09 11:33:27 +0900 (土, 09 6 2012) $<br>
 *          $Author: aigis.developer@gmail.com $
 */
public class FrontController implements Filter {

	/** コンテキスト. */
	protected ServletContext servletContext;

	/** 文字コード. */
	protected String charset;

	/** ルートパッケージ名. */
	protected String rootPackageName;

	/** コントローラーパッケージ名. */
	protected String controllerPackageName;

	/** ベース表示用ファイルパス. */
	protected String baseViewPath;

	/**
	 * 初期化処理.
	 * 
	 * @param config
	 * @throws ServletException
	 */
	@Override
	public void init(final FilterConfig config) throws ServletException {
		initServletContext(config);
		initCharset();
		initRootPackageName();
		initControllerPackageName();
		initBaseJspPath();
		ControllerFactory.getInstance().init(rootPackageName, controllerPackageName);
	}

	/**
	 * ServletContextを設定する.
	 * 
	 * @param config
	 */
	protected void initServletContext(final FilterConfig config) {
		servletContext = config.getServletContext();
	}

	/**
	 * 文字コードを設定する.
	 */
	protected void initCharset() {
		charset = servletContext.getInitParameter(ControllerConstants.CHARSET_KEY);
		if (charset == null) {
			charset = ControllerConstants.DEFAULT_CHARSET;
		}
	}

	/**
	 * ルートパッケージ名を設定する.
	 */
	protected void initRootPackageName() {
		rootPackageName = servletContext.getInitParameter(ControllerConstants.ROOT_PACKAGE_KEY);
		if (rootPackageName == null) {
			throw new IllegalStateException(ControllerConstants.ROOT_PACKAGE_KEY + " does not exist in web.xml.");
		}
	}

	/**
	 * コントローラーパッケージ名を設定する.
	 */
	protected void initControllerPackageName() {
		controllerPackageName = servletContext.getInitParameter(ControllerConstants.CONTROLLER_PACKAGE_KEY);
		if (controllerPackageName == null) {
			controllerPackageName = ControllerConstants.DEFAULT_CONTROLLER_PACKAGE_NAME;
		}
	}

	/**
	 * JSPファイルのベースパスを設定する.
	 */
	protected void initBaseJspPath() {
		baseViewPath = ControllerConstants.BASE_VIEW_PATH;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {

		doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	protected void doFilter(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain chain) throws IOException, ServletException {

		final String path = RequestUtil.getPath(request);
		if (request.getCharacterEncoding() == null) {
			request.setCharacterEncoding(charset);
		}
		doFilter(request, response, chain, path);
	}

	protected void doFilter(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain chain, final String path) throws IOException, ServletException {

		final HttpServletRequest previousRequest = RequestLocator.get();
		RequestLocator.set(request);
		final HttpServletResponse previousResponse = ResponseLocator.get();
		ResponseLocator.set(response);

		try {
			final Controller controller = ControllerFactory.getInstance().create(path);
			if (controller != null) {
				processController(request, response, controller, path);
			} else {
				chain.doFilter(request, response);
			}
		} finally {
			ResponseLocator.set(previousResponse);
			RequestLocator.set(previousRequest);
		}
	}

	/**
	 * コントラーラーの実行を行う.
	 * 
	 * @param request
	 * @param response
	 * @param controller
	 * @throws IOException
	 * @throws ServletException
	 */
	protected void processController(final HttpServletRequest request, final HttpServletResponse response,
			final Controller controller, final String path) throws IOException, ServletException {
		final RequestHandler requestHandler = controller.createRequestHandler(request);
		requestHandler.handle();

		final Connection con = DBConnectionPool.getInstance().getConnection(5);
		decorationController(controller, request, response, path, con);
		try {
			final Navigation navigation = controller.runBase();
			handleNavigation(request, response, controller, navigation);
			commit(con, controller.getClass().getName());
		} catch (final Exception t) {
			t.printStackTrace();
			rollback(con, controller.getClass().getName());
			if (t instanceof IOException) {
				throw (IOException) t;
			}
			if (t instanceof ServletException) {
				throw (ServletException) t;
			}
			// TODO
		} finally {
			release(con);
		}
	}

	/**
	 * コミットを行う.
	 * 
	 * @param con コネクション
	 * @param controllerName コントローラー名
	 * @throws SQLException
	 */
	private void commit(final Connection con, final String controllerName) throws SQLException {
		if (con != null) {
			con.commit();
			System.out.println(controllerName + " commit!");
		}
	}

	/**
	 * ロールバックを行う.
	 * 
	 * @param con コネクション
	 * @param controllerName コントローラー名
	 */
	private void rollback(final Connection con, final String controllerName) {
		if (con != null) {
			try {
				con.rollback();
				System.out.println(controllerName + " rollback!");
			} catch (final SQLException e) {
				System.out.println(controllerName + " rollback fail.");
			}
		}
	}

	/**
	 * コネクションをコネクションプールに返す.
	 * 
	 * @param con コネクション
	 */
	private void release(final Connection con) {
		if (con != null) {
			DBConnectionPool.getInstance().releaseConnection(con);
		}
	}

	/**
	 * 遷移先の設定を行う.
	 * 
	 * @param request
	 * @param response
	 * @param controller
	 * @param navigation
	 * @throws IOException
	 * @throws ServletException
	 */
	protected void handleNavigation(final HttpServletRequest request, final HttpServletResponse response,
			final Controller controller, final Navigation navigation) throws IOException, ServletException {

		if (navigation == null) {
			return;
		}
		if (navigation.isRedirect()) {
			doRedirect(request, response, controller, navigation.getPath());
		} else {
			doForward(request, response, controller, navigation.getPath());
		}
	}

	/**
	 * リダイレクト処理を行う.
	 * 
	 * @param request
	 * @param response
	 * @param controller
	 * @param path
	 * @throws IOException
	 * @throws ServletException
	 */
	protected void doRedirect(final HttpServletRequest request, final HttpServletResponse response,
			final Controller controller, final String path) throws IOException, ServletException {
		String redirectPath = null;
		if (path.startsWith("/")) {
			redirectPath = request.getContextPath() + path;
		} else {
			redirectPath = path;
		}
		response.sendRedirect(response.encodeRedirectURL(redirectPath));
	}

	/**
	 * フォワード処理を行う.
	 * 
	 * @param request
	 * @param response
	 * @param controller
	 * @param path
	 * @throws IOException
	 * @throws ServletException
	 */
	protected void doForward(final HttpServletRequest request, final HttpServletResponse response,
			final Controller controller, final String path) throws IOException, ServletException {

		String forwardPath = baseViewPath;
		forwardPath += controller.basePath + path;

		final RequestDispatcher rd = servletContext.getRequestDispatcher(forwardPath);
		rd.forward(request, response);
	}

	/**
	 * コントローラーに各種値を設定する.
	 * 
	 * @param controller
	 * @param request
	 * @param response
	 * @param path
	 * @param con
	 */
	protected void decorationController(final Controller controller, final HttpServletRequest request,
			final HttpServletResponse response, final String path, final Connection con) {

		controller.servletContext = servletContext;
		controller.request = request;
		controller.response = response;
		final int pos = path.lastIndexOf('/');
		controller.basePath = path.substring(0, pos + 1);

		setService(controller, con);
	}

	protected Object setService(final Object obj, final Connection con) {
		for (final Field field : obj.getClass().getDeclaredFields()) {
			final ServiceClass s = field.getAnnotation(ServiceClass.class);
			if (s != null) {
				String className = s.name();
				if (StringUtils.isEmpty(className)) {
					className = field.getType().getName();
				}
				Service service = null;
				try {
					service = (Service) ClassUtil.newInstance(Class.forName(className));

					if (service == null) {
						continue;
					}

					service.setConnection(con);
					field.setAccessible(true);
					field.set(obj, service);
					setService(service, con);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
		return obj;
	}

	/**
	 * 終了処理.
	 */
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

}
