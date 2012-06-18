package jp.ddo.trismegistos.jfw.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import jp.ddo.trismegistos.jfw.exception.WrapRuntimeException;

/**
 * ファイル操作のUtilクラス.
 * 
 * @author y_sugasawa
 * @since 2012/04/21
 * @version $Rev: 19 $<br>
 *          $Date: 2012-06-09 11:33:27 +0900 (土, 09 6 2012) $<br>
 *          $Author: aigis.developer@gmail.com $
 */
public class FileUtil {

	/**
	 * ファイル読み込みを行う.
	 * 
	 * @param path 読み込みファイルのパス
	 * @return 読み込んだ文字列
	 */
	public static String read(final String path) {

		BufferedReader br = null;
		final StringBuilder sb = new StringBuilder();
		try {
			br = new BufferedReader(new FileReader(path));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append(System.getProperty("line.separator"));
			}
		} catch (final IOException e) {
			throw new WrapRuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (final IOException e) {
				}
			}
		}
		return sb.toString();
	}
}
