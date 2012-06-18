package jp.ddo.trismegistos.jfw.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jp.ddo.trismegistos.jfw.util.DateUtil;

/**
 * PreparedStatementとパラメータ情報を用いて、<br>
 * 値をバインドしたPreparedStatementを作成するクラス.
 * 
 * @author y_sugasawa
 * @since 2012/05/07
 * @version $Rev: 10 $<br>
 *          $Date: 2012-06-09 11:08:15 +0900 (土, 09 6 2012) $<br>
 *          $Author: aigis.developer@gmail.com $
 */
class Query {

	/** String. */
	private static final int STRING_CASE = 1;

	/** Integer. */
	private static final int INTEGER_CASE = 2;

	/** Double. */
	private static final int DOUBLE_CASE = 3;

	/** Long. */
	private static final int LONG_CASE = 4;

	/** Date. */
	private static final int DATE_CASE = 5;

	/** Null. */
	private static final int NULL_CASE = 0;

	/** PreparedStatement. */
	private PreparedStatement ps;

	/**
	 * パラメータマップ.<br>
	 * KEY:SQLのバインドのKEY値, VALUE:?の位置
	 */
	private Map<String, List<Integer>> paramMap;

	/**
	 * コンストラクタ.
	 * 
	 * @param ps PreparedStatement
	 * @param paramMap パラメータマップ
	 */
	public Query(final PreparedStatement ps, final Map<String, List<Integer>> paramMap) {
		this.ps = ps;
		this.paramMap = paramMap;
	}

	/**
	 * PreparedStatementを取得する.
	 * 
	 * @param params バインド値
	 * @return PreparedStatement
	 * @throws SQLException
	 */
	public PreparedStatement getPs(final Map<String, Object> params) throws SQLException {
		// TODO nullチェック

		for (final Map.Entry<String, List<Integer>> entry : paramMap.entrySet()) {
			final String key = entry.getKey();
			final Object value = params.get(key);
			final int type = checkType(value);
			for (final Integer position : entry.getValue()) {
				switch (type) {
				case STRING_CASE:
					ps.setString(position, (String) value);
					break;
				case INTEGER_CASE:
					ps.setInt(position, (Integer) value);
					break;
				case DOUBLE_CASE:
					ps.setDouble(position, (Double) value);
					break;
				case LONG_CASE:
					ps.setLong(position, (Long) value);
					break;
				case DATE_CASE:
					ps.setDate(position, DateUtil.convertSqlDate((Date) value));
					break;
				case NULL_CASE:
					ps.setNull(position, Types.NULL);
					break;
				default:
					throw new SQLException("unsupperted Type. " + key + "[" + value.getClass().getName() + "].");
				}
			}
		}
		return ps;
	}

	/**
	 * 指定されたObjectの型を判断して、対応する整数値を返す.<br>
	 * サポートしていない型の場合は-1を返す.
	 * 
	 * @param obj チェック対象のオブジェクト.
	 * @return 型に応じた整数値.
	 */
	private int checkType(final Object obj) {

		if (obj == null) {
			return NULL_CASE;
		}
		if (obj instanceof String) {
			return STRING_CASE;
		} else if (obj instanceof Integer) {
			return INTEGER_CASE;
		} else if (obj instanceof Double) {
			return DOUBLE_CASE;
		} else if (obj instanceof Long) {
			return LONG_CASE;
		} else if (obj instanceof Date) {
			return DATE_CASE;
		}

		return -1;
	}
}
