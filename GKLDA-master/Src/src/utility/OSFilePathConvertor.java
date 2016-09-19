package utility;

/**
 * Convert the file path according to the operating system. In windows, it is
 * "\" while in Unix, it is "/".
 */
public class OSFilePathConvertor {
	/**
	 * Convert '/' to '\' or '\' to '/' based on operating system.
	 * 
	 * @param filePath		original file path
	 * @return				the file path based on the current system
	 */
	public static String convertOSFilePath(String filePath) {
		if (isWindows()) {
			// Windows
			return filePath.replace("/", "\\");
		} else if (isMac() || isUnix()) {
			// Unix
			return filePath.replace("\\", "/");
		} else {
			ExceptionUtility
					.throwAndCatchException("Not recognizable operating system!");
			return null;
		}
	}

	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		// Windows
		return (os.indexOf("win") >= 0);
	}

	public static boolean isMac() {
		String os = System.getProperty("os.name").toLowerCase();
		// Mac
		return (os.indexOf("mac") >= 0);
	}

	public static boolean isUnix() {
		String os = System.getProperty("os.name").toLowerCase();
		// Linux or Unix
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
	}

	public static boolean isSolaris() {
		String os = System.getProperty("os.name").toLowerCase();
		// Solaris
		return (os.indexOf("sunos") >= 0);
	}
}
