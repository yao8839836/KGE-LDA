package utility;

import global.Constant;

import java.util.HashSet;
import java.util.Map;

/**
 * Kullback-Leibler divergence (KL Divergence).
 */
public class DistributionDivergenceUtil {
	/**
	 * Distribution1 and distribution2 should share the same indexes.
	 */
	public static double getKLDivergence(double[] distribution1,
			double[] distribution2) {
		assert (distribution1.length == distribution2.length) : "Distribution1 and distribution2 should share the same indexes.";

		double divergence = 0.0;
		for (int i = 0; i < distribution1.length; ++i) {
			double prob1 = distribution1[i];
			double prob2 = distribution2[i];
			divergence += prob1 * (Math.log(prob1) - Math.log(prob2));
		}
		return divergence;
	}

	/**
	 * Distribution1 and distribution2 should share the same indexes.
	 */
	public static double getSymmetricKLDivergence(double[] distribution1,
			double[] distribution2) {
		return 0.5 * (getKLDivergence(distribution1, distribution2) + getKLDivergence(
				distribution2, distribution1));
	}

	/**
	 * Calculate the KL-Divergence given the IDs with non-smooth probability.
	 */
	public static double getKLDivergence(Map<Integer, Double> map1,
			Map<Integer, Double> map2) {
		HashSet<Integer> hsUniqueIds = new HashSet<Integer>();
		double divergence = 0.0;
		for (Map.Entry<Integer, Double> entry : map1.entrySet()) {
			int id = entry.getKey();
			double prob1 = entry.getValue();
			if (!hsUniqueIds.contains(id)) {
				hsUniqueIds.add(id);

				double prob2 = Constant.SMOOTH_PROBABILITY;
				Double probObject2 = map2.get(id);
				if (probObject2 != null) {
					prob2 = probObject2;
				}
				divergence += prob1 * (Math.log(prob1) - Math.log(prob2));
			}
		}
		for (Map.Entry<Integer, Double> entry : map2.entrySet()) {
			int id = entry.getKey();
			double prob2 = entry.getValue();
			if (!hsUniqueIds.contains(id)) {
				hsUniqueIds.add(id);

				double prob1 = Constant.SMOOTH_PROBABILITY;
				Double probObject1 = map1.get(id);
				if (probObject1 != null) {
					prob1 = probObject1;
				}
				divergence += prob1 * (Math.log(prob1) - Math.log(prob2));
			}
		}
		return divergence;
	}

	public static double getSymmetricKLDivergence(Map<Integer, Double> map1,
			Map<Integer, Double> map2) {
		return 0.5 * (getKLDivergence(map1, map2) + getKLDivergence(map2, map1));
	}
}
