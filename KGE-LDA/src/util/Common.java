package util;

import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.special.Gamma;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import Jama.Matrix;
import jdistlib.math.Bessel;

import java.util.List;

public class Common {

	/**
	 * 返回数组中最大元素的下标
	 * 
	 * @param array
	 *            输入数组
	 * @return 最大元素的下标
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
	 * 返回数组中最小元素的下标
	 * 
	 * @param array
	 *            输入数组
	 * @return 最小元素的下标
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
	 * 返回数组中的最小值
	 * 
	 * @param array
	 *            输入数组
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
	 * 复制矩阵
	 * 
	 * @param array
	 *            矩阵
	 * @return
	 */
	public static double[][] makeCopy(double[][] array) {

		double[][] copy = new double[array.length][];

		for (int i = 0; i < copy.length; i++) {

			copy[i] = new double[array[i].length];

			for (int j = 0; j < copy[i].length; j++) {
				copy[i][j] = array[i][j];
			}
		}

		return copy;
	}

	/**
	 * 
	 * 多元t分布的概率密度函数
	 * 
	 * @param vector
	 * @param freedom
	 * @param mean
	 * @param sigma
	 * @return
	 */
	public static double MultivariateTdistribution(double[] vector, double freedom, double[] mean, double[][] sigma) {

		double dimension = vector.length;

		Matrix s = new Matrix(sigma);

		Matrix m = new Matrix(mean, 1);

		Matrix v = new Matrix(vector, 1);

		Matrix c = v.minus(m);

		Matrix inverse = s.inverse();

		Matrix temp = c.times(inverse).times(c.transpose());

		double det = s.det();

		double prob = Gamma.gamma((freedom + dimension) / 2)
				/ (Gamma.gamma(freedom / 2) * Math.pow(freedom, dimension / 2) * Math.pow(Math.PI, dimension / 2))
				* Math.pow(det, -0.5) * Math.pow(1 + temp.det() / freedom, -(dimension + freedom) / 2);

		return prob;

	}

	/**
	 * This function computes the lower triangular cholesky decomposition L' of
	 * matrix A' from L (the cholesky decomp of A) where A' = A - x*x^T. Based
	 * on the pseudocode in the wiki page
	 * https://en.wikipedia.org/wiki/Cholesky_decomposition#Rank-one_update
	 */
	public static void cholRank1Downdate(DenseMatrix64F L, DenseMatrix64F x, int dimension) {
		// L should be a square lower triangular matrix (although not checking
		// for triangularity here explicitly)
		// Data.D = 2;
		assert L.numCols == dimension;
		assert L.numRows == dimension;
		// x should be a vector
		assert x.numCols == 1;
		assert x.numRows == dimension;

		for (int k = 0; k < dimension; k++) {
			double r = Math.sqrt(L.get(k, k) * L.get(k, k) - x.get(k, 0) * x.get(k, 0));
			double c = r / (double) L.get(k, k);
			double s = x.get(k, 0) / L.get(k, k);
			L.set(k, k, r);
			for (int l = k + 1; l < dimension; l++) {
				double val = (L.get(l, k) - s * x.get(l, 0)) / (double) c;
				L.set(l, k, val);
				val = c * x.get(l, 0) - s * L.get(l, k);
				x.set(l, 0, val);
			}
		}
	}

	/**
	 * This function computes the lower triangular cholesky decomposition L' of
	 * matrix A' from L (the cholesky decomp of A) where A' = A + x*x^T. Based
	 * on the pseudocode in the wiki page
	 * https://en.wikipedia.org/wiki/Cholesky_decomposition#Rank-one_update
	 */
	public static void cholRank1Update(DenseMatrix64F L, DenseMatrix64F x, int dimension) {
		// L should be a square lower triangular matrix (although not checking
		// for triangularity here explicitly)
		// Data.D = 2;
		assert L.numCols == dimension;
		assert L.numRows == dimension;
		// x should be a vector
		assert x.numCols == 1;
		assert x.numRows == dimension;

		for (int k = 0; k < dimension; k++) {
			double r = Math.sqrt(Math.pow(L.get(k, k), 2) + Math.pow(x.get(k, 0), 2));
			double c = r / (double) L.get(k, k);
			double s = x.get(k, 0) / L.get(k, k);
			L.set(k, k, r);
			for (int l = k + 1; l < dimension; l++) {
				double val = (L.get(l, k) + s * x.get(l, 0)) / (double) c;
				L.set(l, k, val);
				val = c * x.get(l, 0) - s * val;
				x.set(l, 0, val);
			}
		}
	}

	/**
	 * mean of the data
	 * 
	 * @param data
	 * @param dimension
	 * @return
	 */
	public static DenseMatrix64F getSampleMean(DenseMatrix64F[] data, int dimension) {
		DenseMatrix64F mean = new DenseMatrix64F(dimension, 1);
		// initialized to 0

		for (DenseMatrix64F vec : data)
			CommonOps.addEquals(mean, vec);

		CommonOps.divide(data.length, mean);

		return mean;
	}

	/**
	 * binSearchArrayList
	 * 
	 * @param cumProb
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public static int binSearchArrayList(List<Double> cumProb, double key, int start, int end) {
		if (start > end)
			return start;

		int mid = (start + end) / 2;
		if (key == cumProb.get(mid))
			return mid + 1;
		if (key < cumProb.get(mid))
			return binSearchArrayList(cumProb, key, start, mid - 1);
		if (key > cumProb.get(mid))
			return binSearchArrayList(cumProb, key, mid + 1, end);
		return -1;
	}

	/**
	 * C function
	 * 
	 * @param kappa_k
	 * @param M
	 * @return
	 */
	public static double C(double kappa_k, int M) {

		double result = Math.pow(kappa_k, (double) M / 2 - 1)
				/ (Math.pow(2 * Math.PI, (double) M / 2) * Bessel.i(kappa_k, (double) M / 2 - 1, false));

		return result;
	}

	/**
	 * l2 norm of a vector
	 * 
	 * @param vector
	 * @return
	 */
	public static double l2norm(double[] vector) {

		double norm = 0;

		for (int i = 0; i < vector.length; i++) {
			norm += vector[i] * vector[i];
		}

		norm = Math.sqrt(norm);

		return norm;
	}

	/**
	 * normalize a vector
	 * 
	 * @param vector
	 * @return
	 */
	public static double[] l2normalize(double[] vector) {

		double norm = 0;

		for (int i = 0; i < vector.length; i++) {
			norm += vector[i] * vector[i];
		}

		norm = Math.sqrt(norm);

		double[] result = new double[vector.length];

		for (int i = 0; i < vector.length; i++) {
			result[i] = vector[i] / norm;
		}

		return result;
	}

	/**
	 * 返回向量加（减）的结果
	 * 
	 * @param x_1
	 * @param x_2
	 * @param flag
	 * @return
	 */
	public static double[] vector_add(double[] x_1, double[] x_2, int flag) {

		double[] result = new double[x_1.length];

		for (int i = 0; i < result.length; i++) {
			result[i] = x_1[i] + flag * x_2[i];
		}

		return result;

	}

	/**
	 * 随机从log-Normal分布生成一个数
	 * 
	 * @param mean
	 * @param shape
	 * @return
	 */
	public static double random_kappa_log_normal(double mean, double shape) {

		LogNormalDistribution lnd = new LogNormalDistribution(mean, shape);

		double y = lnd.sample();

		return y;
	}

	/**
	 * 随机从log-Normal分布生成一组数
	 * 
	 * @param mean
	 * @param shape
	 * @param size
	 * @return
	 */
	public static double[] randoms_kappa_log_normal(double mean, double shape, int size) {

		LogNormalDistribution lnd = new LogNormalDistribution(mean, shape);

		double z[] = lnd.sample(size);

		return z;
	}

	/**
	 * 返回log-norm在x点的概率值
	 * 
	 * @param mean
	 * @param shape
	 * @param x
	 * @return
	 */
	public static double probability(double mean, double shape, double x) {

		LogNormalDistribution lnd = new LogNormalDistribution(mean, shape);

		return lnd.density(x);

	}

	/**
	 * 向量乘以因子
	 * 
	 * @param vector
	 * @param factor
	 * @return
	 */
	public static double[] vector_times(double[] vector, double factor) {

		double[] result = new double[vector.length];

		for (int i = 0; i < result.length; i++) {
			result[i] = factor * vector[i];
		}

		return result;

	}

}
