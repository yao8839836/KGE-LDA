package utility;

public class ArrayAllocationAndInitialization {
	public static int[] allocateAndInitialize(int[] array, int n1) {
		array = new int[n1];
		for (int i = 0; i < n1; ++i) {
			array[i] = 0;
		}
		return array;
	}

	public static int[][] allocateAndInitialize(int[][] array, int n1, int n2) {
		array = new int[n1][n2];
		for (int i = 0; i < n1; ++i) {
			for (int j = 0; j < n2; ++j) {
				array[i][j] = 0;
			}
		}
		return array;
	}

	public static int[][][] allocateAndInitialize(int[][][] array, int n1,
			int n2, int n3) {
		array = new int[n1][n2][n3];
		for (int i = 0; i < n1; ++i) {
			for (int j = 0; j < n2; ++j) {
				for (int k = 0; k < n3; ++k) {
					array[i][j][k] = 0;
				}
			}
		}
		return array;
	}

	public static double[] allocateAndInitialize(double[] array, int n1) {
		array = new double[n1];
		for (int i = 0; i < n1; ++i) {
			array[i] = 0.0;
		}
		return array;
	}

	public static double[][] allocateAndInitialize(double[][] array, int n1,
			int n2) {
		array = new double[n1][n2];
		for (int i = 0; i < n1; ++i) {
			for (int j = 0; j < n2; ++j) {
				array[i][j] = 0.0;
			}
		}
		return array;
	}

	public static double[][][] allocateAndInitialize(double[][][] array,
			int n1, int n2, int n3) {
		array = new double[n1][n2][n3];
		for (int i = 0; i < n1; ++i) {
			for (int j = 0; j < n2; ++j) {
				for (int k = 0; k < n3; ++k) {
					array[i][j][k] = 0.0;
				}
			}
		}
		return array;
	}

	/**
	 * Create a new array with size n1 * n2 and copy values from
	 * array[1...m1][1...m2] to the new array.
	 * 
	 */
	public static int[][] allocateAndCopyValues(int[][] array, int n1, int n2,
			int m1, int m2) {
		int[][] new_array = allocateAndInitialize(array, n1, n2);
		for (int i = 0; i < m1; ++i) {
			for (int j = 0; j < m2; ++j) {
				new_array[i][j] = array[i][j];
			}
		}
		return new_array;
	}

	public static double[][] allocateAndCopyValues(double[][] array, int n1,
			int n2, int m1, int m2) {
		double[][] new_array = allocateAndInitialize(array, n1, n2);
		for (int i = 0; i < m1; ++i) {
			for (int j = 0; j < m2; ++j) {
				new_array[i][j] = array[i][j];
			}
		}
		return new_array;
	}
}
