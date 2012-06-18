package jp.ddo.trismegistos.jfw.exception;

/**
 * フレームワーク用RuntimeException.
 * 
 * @author y_sugasawa
 * @since 2012/03/31
 * @version $Rev: 19 $<br>
 *          $Date: 2012-06-09 11:33:27 +0900 (土, 09 6 2012) $<br>
 *          $Author: aigis.developer@gmail.com $
 */
public class WrapRuntimeException extends RuntimeException {

	/** シリアルID. */
	private static final long serialVersionUID = 1L;

	/**
	 * コンストラクタ.
	 * 
	 * @param cause
	 */
	public WrapRuntimeException(final Throwable cause) {
		super(cause);
	}

	/**
	 * コンストラクタ.
	 * 
	 * @param message
	 * @param cause
	 */
	public WrapRuntimeException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * コンストラクタ.
	 * 
	 * @param message
	 */
	public WrapRuntimeException(final String message) {
		super(message);
	}
}
