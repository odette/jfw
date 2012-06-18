package jp.ddo.trismegistos.jfw.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jp.ddo.trismegistos.jfw.exception.WrapRuntimeException;
import jp.ddo.trismegistos.jfw.util.FileUtil;
import jp.ddo.trismegistos.jfw.util.RequestLocator;

import org.apache.commons.lang3.StringUtils;

/**
 * PreparedStatementを作成するクラス.<br>
 * 作成されるPreparedStatementはバインド済のもの.<br>
 * SQLファイルを読み込む場合は、読み込むんだ内容をキャッシュする.
 * 
 * @author y_sugasawa
 * @since 2012/05/07
 * @version $Rev: 10 $<br>
 *          $Date: 2012-06-09 11:08:15 +0900 (土, 09 6 2012) $<br>
 *          $Author: aigis.developer@gmail.com $
 */
public class PreparedStatementFactory {

	/** 自身のインスタンス. */
	private static PreparedStatementFactory factory = new PreparedStatementFactory();

	/** SQLをキャッシュするためのMap. */
	private Map<String, String> sqlMap;

	/**
	 * プライベートコンストラクタ.
	 */
	private PreparedStatementFactory() {
		sqlMap = new ConcurrentHashMap<String, String>();
	}

	/**
	 * インスタンスを取得する.
	 * 
	 * @return PreparedStatementFactoryクラスのインスタンス
	 */
	public static PreparedStatementFactory getInstance() {
		return factory;
	}

	/**
	 * 指定した文字列よりPreparedStatementを作成する.
	 * 
	 * @param sqlStr SQL文字列
	 * @param params パラメータ
	 * @param con コネクション
	 * @return PreparedStatement
	 */
	public PreparedStatement createFromString(final String sqlStr, final Map<String, Object> params,
			final Connection con) {
		try {
			final Query query = createQuery(con, sqlStr, params);
			return query.getPs(params);
		} catch (final SQLException e) {
			throw new WrapRuntimeException(e);
		}
	}

	/**
	 * 指定されたSQLファイルを読み込みPreparedStatementを作成する.<br>
	 * SQLファイルの内容をキャッシュされる.
	 * 
	 * @param path SQLファイルのパス
	 * @param params パラメータ
	 * @param con コネクション
	 */
	public PreparedStatement createFromFile(final String path, final Map<String, Object> params, final Connection con) {
		String sqlStr = null;
		if ((sqlStr = sqlMap.get(path)) == null) {
			sqlStr = readSqlFile(path);
		}
		return createFromString(sqlStr, params, con);
	}

	/**
	 * SQLファイルを読み込み、文字列として返す.
	 * 
	 * @param path SQLファイルパス
	 * @return SQL文字列
	 */
	private String readSqlFile(final String path) {
		return FileUtil.read(RequestLocator.get().getServletContext().getRealPath(path));
	}

	/**
	 * Queryクラスを生成する.
	 * 
	 * @param con コネクション
	 * @param sql SQL文字列
	 * @param params バインド値
	 * @return queryインスタンス
	 * @throws SQLException
	 */
	private Query createQuery(final Connection con, final String sql, final Map<String, Object> params)
			throws SQLException {
		final Map<String, List<Integer>> paramMap = new HashMap<String, List<Integer>>();

		String tmpSql = sql;
		int count = 1;
		int start = sql.indexOf("/*");
		int end = sql.indexOf("*/");
		while (start != -1 && end != -1) {
			final String key = tmpSql.substring(start + 3, end - 1);
			final Object value = params.get(key);
			if (value != null) {
				List<Integer> list = paramMap.get(key);
				if (list == null) {
					list = new ArrayList<Integer>();
					paramMap.put(key, list);
				}
				list.add(count);
				tmpSql = StringUtils.replaceOnce(tmpSql, "/* " + key + " */", "?");
				count++;
			}
			start = tmpSql.indexOf("/*");
			end = tmpSql.indexOf("*/");
		}

		return new Query(con.prepareStatement(tmpSql, Statement.RETURN_GENERATED_KEYS), paramMap);
	}

}
