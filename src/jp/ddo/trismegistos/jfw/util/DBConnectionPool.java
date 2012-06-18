package jp.ddo.trismegistos.jfw.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Stack;

/**
 * DBとの接続とコネクションの管理を行うクラス.
 * 
 * @author y_sugasawa
 * @since 2012/03/31
 * @version $Rev: 19 $<br>
 *          $Date: 2012-06-09 11:33:27 +0900 (土, 09 6 2012) $<br>
 *          $Author: aigis.developer@gmail.com $
 */
public class DBConnectionPool {

	/** 自身のインスタンス. */
	private static DBConnectionPool dBConnectionPool = null;

	/** コネクション. */
	private Stack<Connection> connections;

	/** ドライバー. */
	private String driver;

	/** DBのURL. */
	private String url;

	/** 接続ユーザ. */
	private String user;

	/** パスワード. */
	private String password;

	/** 最大コネクション数. */
	private int maxConnection;

	/**
	 * コンストラクタ.
	 * 
	 * @param path DB接続情報のプロパティファイルのパス
	 */
	private DBConnectionPool(final String path) {

		InputStream is = null;

		try {
			is = new FileInputStream(new File(path));
			final Properties p = new Properties();
			p.load(is);
			driver = p.getProperty("driver");
			url = p.getProperty("url");
			user = p.getProperty("user");
			password = p.getProperty("password");
			maxConnection = Integer.parseInt(p.getProperty("maxConnection"));
		} catch (final IOException e) {
			e.printStackTrace();
		}
		connections = new Stack<Connection>();
		try {
			Class.forName(driver);
			for (int i = 0; i < maxConnection; i++) {
				final Connection con = DriverManager.getConnection(url, user, password);
				con.setAutoCommit(false);
				connections.push(con);
			}
		} catch (final Exception e) {
			// TODO 出力方法変更
			System.out.println(e.getMessage());
		}
	}

	/**
	 * インスタンスを取得する.
	 * 
	 * @return DBConnectionPoolインスタンス
	 */
	public static DBConnectionPool getInstance() {
		return getInstance(null);
	}

	/**
	 * インスタンスを取得する.
	 * 
	 * @param path DB接続情報のプロパティファイルのパス
	 * @return DBConnectionPoolインスタンス
	 */
	public static DBConnectionPool getInstance(final String path) {
		if (dBConnectionPool == null) {
			dBConnectionPool = new DBConnectionPool(path);
		}
		return dBConnectionPool;
	}

	/**
	 * コネクションを取得する.<br>
	 * 指定試行回数でコネクションを取得出来なかった場合はnullを返す.
	 * 
	 * @param count 試行回数
	 * @return コネクション
	 */
	public synchronized Connection getConnection(final int count) {
		if (count < 1) {
			return null;
		}
		if (connections.empty() == false) {
			return connections.pop();
		} else {
			try {
				wait(100);
			} catch (final InterruptedException e) {
			}
			return getConnection(count - 1);
		}
	}

	/**
	 * コネクションを解放する.
	 * 
	 * @param connection コネクション
	 */
	public synchronized void releaseConnection(final Connection connection) {
		connections.push(connection);
	}

	/**
	 * すべてのコネクションを閉じる.
	 */
	public synchronized void closeAll() {
		for (final Connection con : connections) {
			try {
				con.close();
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
