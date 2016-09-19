package knowledge;

public class NormalInverseWishart {

	/**
	 * Hyperparam mean vector.
	 */
	public double[] mu_0;

	/**
	 * mean fraction
	 */
	public double k_0;

	/**
	 * Hyperparam covariance matrix
	 */
	public double[][] sigma_0;

	/**
	 * initial degrees of freedom
	 */
	public double nu_0;

	/**
	 * NIW先验， 多维高斯分布的先验
	 * 
	 * @param mu_0
	 * @param nu_0
	 * @param sigma_0
	 * @param k_0
	 */
	public NormalInverseWishart(double[] mu_0, double nu_0, double[][] sigma_0, double k_0) {

		this.mu_0 = mu_0;
		this.nu_0 = nu_0;
		this.sigma_0 = sigma_0;
		this.k_0 = k_0;

	}

}
