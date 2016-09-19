package utility;

import java.io.File;

public class Common {

	/**
	 * 杩斿洖鏁扮粍涓渶澶у厓绱犵殑涓嬫爣
	 * 
	 * @param array
	 *            杈撳叆鏁扮粍
	 * @return 鏈?澶у厓绱犵殑涓嬫爣
	 */
	public static int maxIndex(double[] array) {
		double max = array[0];
		int maxIndex = 0;
		for (int i = 1; i < array.length; i++) {
			if (array[i] > max) {
				max = array[i];
				maxIndex = i;
			}

		}
		return maxIndex;

	}

	/**
	 * 杩斿洖鏁扮粍涓渶灏忓厓绱犵殑涓嬫爣
	 * 
	 * @param array
	 *            杈撳叆鏁扮粍
	 * @return 鏈?灏忓厓绱犵殑涓嬫爣
	 */
	public static int minIndex(double[] array) {
		double min = array[0];
		int minIndex = 0;
		for (int i = 1; i < array.length; i++) {
			if (array[i] < min) {
				min = array[i];
				minIndex = i;
			}

		}
		return minIndex;

	}

	/**
	 * 杩斿洖鏁扮粍涓殑鏈?灏忓??
	 * 
	 * @param array
	 *            杈撳叆鏁扮粍
	 * @return
	 */
	public static double min(double[] array) {

		double min = array[0];

		for (int i = 0; i < array.length; i++) {
			if (array[i] < min)
				min = array[i];
		}

		return min;

	}

	/**
	 * 递归删除目录下的所有文件及子目录下所有文件
	 * 
	 * @param dir
	 *            将要删除的文件目录
	 * @return boolean Returns "true" if all deletions were successful. If a
	 *         deletion fails, the method stops attempting to delete and returns
	 *         "false".
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

}
