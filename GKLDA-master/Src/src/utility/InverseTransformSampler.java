package utility;

/**
 * Perform inverse transform sampling.
 */
public class InverseTransformSampler {
	/**
	 * It works for unnormalized distribution p.
	 */
	public static int sample(double[] p, double randSeed) {
		int length = p.length;
		// Cumulative multinomial parameters.
		double[] cdf = new double[length];
		for (int x = 0; x < length; ++x) {
			cdf[x] = p[x];
		}
		for (int x = 1; x < length; ++x) {
			cdf[x] += cdf[x - 1];
		}

		// Scaled sample because of unnormalized p[].
		double u = randSeed * cdf[length - 1];

		for (int x = 0; x < length; ++x) {
			if (cdf[x] > u) {
				// Sample topic w.r.t distribution p.
				return x;
			}
		}

		// The program should not run to this line.
		ExceptionUtility
				.throwAndCatchException("Wrong sampling process in inverse transform sampler!");
		return -1;
	}
}
