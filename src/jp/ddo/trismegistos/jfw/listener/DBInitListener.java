package jp.ddo.trismegistos.jfw.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import jp.ddo.trismegistos.jfw.util.DBConnectionPool;

/**
 * DB接続の初期化を行うリスナークラス.
 * 
 * @author y_sugasawa
 * @since 2012/03/31
 * @version $Rev: 19 $<br>
 *          $Date: 2012-06-09 11:33:27 +0900 (土, 09 6 2012) $<br>
 *          $Author: aigis.developer@gmail.com $
 */
public class DBInitListener implements ServletContextListener {

	/**
	 * 起動時処理.<br>
	 * DBとの接続を行う.
	 * 
	 * @param event コンテキスト
	 */
	@Override
	public void contextInitialized(final ServletContextEvent event) {
		// インスタンスの生成のみ行なっておく
		DBConnectionPool.getInstance(event.getServletContext().getRealPath("/WEB-INF/conf/db.properties"));
	}

	/**
	 * 停止時処理.<br>
	 * コネクションをすべて閉じる.
	 * 
	 * @param event コンテキスト
	 */
	@Override
	public void contextDestroyed(final ServletContextEvent event) {
		DBConnectionPool.getInstance().closeAll();
	}

}
