package jp.ddo.trismegistos.jfw.util;

import java.util.Calendar;
import java.util.Date;

/**
 * 日付関連のUtilクラス.
 * 
 * @author y_sugasawa
 * @since 2012/03/31
 * @version $Rev: 19 $<br>
 *          $Date: 2012-06-09 11:33:27 +0900 (土, 09 6 2012) $<br>
 *          $Author: aigis.developer@gmail.com $
 */
public class DateUtil {

	public static final String ID = "$Author: aigis.developer@gmail.com $";

	/**
	 * プライベートコンストラクタ.
	 */
	private DateUtil() {
	}

	/**
	 * 現在の日付を取得する.
	 * 
	 * @return 現在日付
	 */
	public static Date getNow() {
		return new Date();
	}

	/**
	 * java.util.Dateをjava.sql.Dateに変換する.
	 * 
	 * @param nowDate java.util.Date
	 * @return 引数をjava.sql.Dateに変換したもの
	 */
	public static java.sql.Date convertSqlDate(final Date nowDate) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(nowDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return new java.sql.Date(cal.getTimeInMillis());
	}

}
