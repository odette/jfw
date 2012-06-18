package jp.ddo.trismegistos.jfw.service;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.ddo.trismegistos.jfw.sql.PreparedStatementFactory;
import jp.ddo.trismegistos.jfw.util.ClassUtil;

/**
 * ビズネスロジックを定義するServiceクラスの基底クラス.<br>
 * DB接続を行う際はこのクラスを使用してください.
 * 
 * @author y_sugasawa
 * @since 2012/03/31
 * @version $Rev: 19 $<br>
 *          $Date: 2012-06-09 11:33:27 +0900 (土, 09 6 2012) $<br>
 *          $Author: aigis.developer@gmail.com $
 */
public abstract class Service {

	/** コネクション. */
	private Connection con;

	/**
	 * select文を実行する.<br>
	 * 取得される行数が1行のみの場合に使用する.
	 * 
	 * @param path SQLファイルのパス
	 * @param params パラメータマップ
	 * @param resultClass 取得される行のクラス.
	 * @return 取得された行をjavaのobjectにしたもの.取得出来なかった場合はnullを返す.
	 * @throws SQLException
	 */
	protected <T> T get(final String path, final Map<String, Object> params, final Class<T> resultClass)
			throws SQLException {
		return get(createPreparedStatement(path, params), resultClass);
	}

	/**
	 * select文を実行する.<br>
	 * 取得される行が複数行の場合に使用する.
	 * 
	 * @param path SQLファイルのパス
	 * @param params パラメータマップ
	 * @param resultClass 取得される行のクラス.
	 * @return 取得された行をjavaのobjectにしたもの.取得出来なかった場合はnullを返す.
	 * @throws SQLException
	 */
	protected <T> List<T> getList(final String path, final Map<String, Object> params, final Class<T> resultClass)
			throws SQLException {
		return getList(createPreparedStatement(path, params), resultClass);
	}

	/**
	 * update,insert,delete文を実行する.<br>
	 * 戻り値は対象となった行数.<br>
	 * オートインクリメントの列が含まれる場合はそのIDを返す.
	 * 
	 * @param path SQLファイルのパス
	 * @param params パラメータマップ
	 * @return updateされた行数
	 * @throws SQLException
	 */
	protected int update(final String path, final Map<String, Object> params) throws SQLException {
		return update(createPreparedStatement(path, params));
	}

	/**
	 * PreparedStatementを実行する.<br>
	 * 戻り値がIntの場合用.
	 * 
	 * @param ps PreparedStatement
	 * @return SQLの戻り値(Int)
	 * @throws SQLException
	 */
	protected int getInt(final PreparedStatement ps) throws SQLException {
		final ResultSet rs = ps.executeQuery();
		try {
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return 0;
			}
		} finally {
			rs.close();
			ps.close();
		}
	}

	/**
	 * selectを実行する.<br>
	 * 取得される行数が1行のみの場合に使用する.
	 * 
	 * @param ps PreparedStatement
	 * @param resultClass 取得される行のclass
	 * @return 取得された行をjavaのobjectにしたもの.取得出来なかった場合はnullを返す.
	 * @throws SQLException
	 */
	private <T> T get(final PreparedStatement ps, final Class<T> resultClass) throws SQLException {

		final ResultSet rs = ps.executeQuery();
		try {
			if (rs.next()) {
				return convert(rs, resultClass);
			} else {
				return null;
			}
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (final SQLException e) {
					// TODO ログ出力
				}
			}
			try {
				ps.close();
			} catch (final SQLException e) {
				// TODO ログ出力
			}
		}
	}

	/**
	 * selectを実行する.<br>
	 * 複数行取得できる場合.
	 * 
	 * @param ps PreparedStatement
	 * @param resultClass 取得される行のclass
	 * @return 取得された行をjavaのobjectにしたもののList.取得出来なかった場合は空のListを返す.
	 * @throws SQLException
	 */
	private <T> List<T> getList(final PreparedStatement ps, final Class<T> resultClass) throws SQLException {

		final List<T> list = new ArrayList<T>();
		final ResultSet rs = ps.executeQuery();

		try {
			while (rs.next()) {
				list.add(convert(rs, resultClass));
			}
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (final SQLException e) {
					// TODO ログ出力
				}
			}
			try {
				ps.close();
			} catch (final SQLException e) {
				// TODO ログ出力
			}
		}
		return list;
	}

	/**
	 * updateを実行する.
	 * 
	 * @param ps PreparedStatement
	 * @return 更新件数
	 * @throws SQLException
	 */
	private int update(final PreparedStatement ps) throws SQLException {
		int count = 0;
		ResultSet rs = null;
		try {
			count = ps.executeUpdate();
			if (count != 0) {
				rs = ps.getGeneratedKeys();
				rs.next();
				final int id = rs.getInt(1);
				if (id != 0) {
					count = id;
				}
			}
		} finally {
			try {
				ps.close();
			} catch (final SQLException e) {
				// TODO ログ出力
			}
		}
		return count;
	}

	/**
	 * ResultSetを指定されたクラスのインスタンスに変換する.
	 * 
	 * @param rs ResultSet
	 * @param resultClass 変換先class
	 * @return 変換後クラスのインスタンス
	 */
	@SuppressWarnings("unchecked")
	private <T> T convert(final ResultSet rs, final Class<T> resultClass) {
		final Object clazz = ClassUtil.newInstance(resultClass);
		for (final Field f : clazz.getClass().getDeclaredFields()) {
			f.setAccessible(true);
			final String name = f.getName();
			try {
				f.set(clazz, rs.getObject(fieldToConst(name)));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return (T) clazz;
	}

	/**
	 * フィールド名をDBのカラム名に変換する.
	 * 
	 * @param str フィールド名
	 * @return 変換後のDBカラム名
	 */
	private String fieldToConst(final String str) {
		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < str.length(); i++) {
			final String c = str.substring(i, i + 1);
			final String uc = c.toUpperCase();
			if (c.equals(uc) == false) {
				// 小文字は大文字にしてappend
				sb.append(uc);
			} else {
				sb.append("_");
				sb.append(uc);
			}
		}
		return sb.toString();
	}

	/**
	 * PreparedStatementを作成する.
	 * 
	 * @param path SQLファイルのパス
	 * @param params パラメータマップ
	 * @return PreparedStatement
	 */
	private PreparedStatement createPreparedStatement(final String path, final Map<String, Object> params) {
		final PreparedStatementFactory factory = PreparedStatementFactory.getInstance();
		return factory.createFromFile(path, params, con);
	}

	/**
	 * コネクションを設定する.
	 * 
	 * @param con コネクション
	 */
	public void setConnection(final Connection con) {
		this.con = con;
	}
}
