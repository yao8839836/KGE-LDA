package utility;

public class ExceptionUtility {
	public static void throwAndCatchException(String message) {
		try {
			throw new Exception(message);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Implement a customized assertion that can pop up as an exception.
	 */
	public static void assertAsException(boolean statement) {
		if (!statement) {
			ExceptionUtility
					.throwAndCatchException("The assertion statement is not true!");
		}
	}

	public static void assertAsException(boolean statement, String message) {
		if (!statement) {
			ExceptionUtility.throwAndCatchException(message);
		}

	}
}
