package model;

import nlp.Corpus;
import utility.ArrayAllocationAndInitialization;
import utility.InverseTransformSampler;

/**
 * This implements the LDA model (Blei et al., 2003).
 */
public class LDA extends TopicModel {
	/******************* Hyperparameters *********************/
	// The hyperparameter for the document-topic distribution.
	// alpha is in the variable param in TopicModel.
	private double tAlpha = 0;
	// The hyperparameter for the topic-word distribution.
	// beta is in the variable param in TopicModel.
	private double vBeta = 0;

	/******************* Posterior distributions *********************/
	private double[][] theta = null; // Document-topic distribution, size D * T.
	private double[][] thetasum = null; // Cumulative document-topic
										// distribution, size
										// D * T.
	private double[][] phi = null; // Topic-word distribution, size T * V.
	private double[][] phisum = null; // Cumulative topic-word distribution,
										// size T * V.
	// Number of times to add the sum arrays, such as thetasum and phisum.
	public int numstats = 0;

	/******************* Temp variables while sampling *********************/
	// z is defined in the superclass TopicModel.
	// private int[][] z = null; // Topic assignments for each word.
	// ndt[d][t]: the counts of document d having topic t.
	private int[][] ndt = null;
	// ndsum[d]: the counts of document d having any topic.
	private int[] ndsum = null;
	// ntw[t][w]: the counts of word w appearing under topic t.
	private int[][] ntw = null;
	// ntsum[t]: the counts of any word appearing under topic t.
	private int[] ntsum = null;

	/**
	 * Create a new topic model with all variables initialized. The z[][] is
	 * randomly assigned.
	 */
	public LDA(Corpus corpus2, ModelParameters param2) {
		super(corpus2, param2);
		tAlpha = param.T * param.alpha;
		vBeta = param.V * param.beta;
		// Allocate memory for temporary variables and initialize their
		// values.
		allocateMemoryForTempVariables();
		// Initialize the first status of Markov chain randomly.
		initializeFirstMarkovChainRandomly();
	}

	/**
	 * Create a new topic model with all variables initialized. The z[][] is
	 * assigned to the value loaded from other models.
	 */
	public LDA(Corpus corpus2, ModelParameters param2, int[][] z2,
			double[][] twdist) {
		super(corpus2, param2);
		tAlpha = param.T * param.alpha;
		vBeta = param.V * param.beta;
		// Allocate memory for temporary variables and initialize their
		// values.
		allocateMemoryForTempVariables();
		// Assign z2 to z.
		z = z2;
		// Assign topic-word distribution.
		phi = twdist;
	}

	// ------------------------------------------------------------------------
	// Memory Allocation and Initialization
	// ------------------------------------------------------------------------

	/**
	 * Allocate memory for temporary variables and initialize their values. Note
	 * that z[][] is not created in this function, but in the function
	 * initializeFirstMarkovChainRandomly().
	 */
	private void allocateMemoryForTempVariables() {
		/******************* Posterior distributions *********************/
		theta = ArrayAllocationAndInitialization.allocateAndInitialize(theta,
				param.D, param.T);
		phi = ArrayAllocationAndInitialization.allocateAndInitialize(phi,
				param.T, param.V);
		if (param.sampleLag > 0) {
			thetasum = ArrayAllocationAndInitialization.allocateAndInitialize(
					thetasum, param.D, param.T);
			phisum = ArrayAllocationAndInitialization.allocateAndInitialize(
					phisum, param.T, param.V);
		}

		/******************* Temp variables while sampling *********************/
		ndt = ArrayAllocationAndInitialization.allocateAndInitialize(ndt,
				param.D, param.T);
		ndsum = ArrayAllocationAndInitialization.allocateAndInitialize(ndsum,
				param.D);
		ntw = ArrayAllocationAndInitialization.allocateAndInitialize(ntw,
				param.T, param.V);
		ntsum = ArrayAllocationAndInitialization.allocateAndInitialize(ntsum,
				param.T);
	}

	/**
	 * Initialize the first status of Markov chain randomly. Note that z[][] is
	 * created in this function.
	 */
	private void initializeFirstMarkovChainRandomly() {
		z = new int[param.D][];
		for (int d = 0; d < param.D; ++d) {
			int N = docs[d].length;
			z[d] = new int[N];

			for (int n = 0; n < N; ++n) {
				int word = docs[d][n];
				int topic = (int) Math.floor(randomGenerator.nextDouble()
						* param.T);
				z[d][n] = topic;

				updateCount(d, topic, word, +1);
			}
		}
	}

	/**
	 * There are several main steps:
	 * 
	 * 1. Run a certain number of Gibbs Sampling sweeps.
	 * 
	 * 2. Compute the posterior distributions.
	 */
	@Override
	public void run() {
		// 1. Run a certain number of Gibbs Sampling sweeps.
		runGibbsSampling();
		// 2. Compute the posterior distributions.
		computePosteriorDistribution();
	}

	// ------------------------------------------------------------------------
	// Gibbs Sampler
	// ------------------------------------------------------------------------

	/**
	 * Run a certain number of Gibbs Sampling sweeps.
	 */
	private void runGibbsSampling() {
		for (int i = 0; i < param.nIterations; ++i) {
			for (int d = 0; d < param.D; ++d) {
				int N = docs[d].length;
				for (int n = 0; n < N; ++n) {
					// Sample from p(z_i|z_-i, w)
					sampleTopicAssignment(d, n);
				}
			}

			if (i >= param.nBurnin && param.sampleLag > 0
					&& i % param.sampleLag == 0) {
				updatePosteriorDistribution();
			}
		}
	}

	/**
	 * Sample a topic assigned to the word in position n of document d.
	 */
	private void sampleTopicAssignment(int d, int n) {
		int old_topic = z[d][n];
		int word = docs[d][n];
		updateCount(d, old_topic, word, -1);

		double[] p = new double[param.T];
		for (int t = 0; t < param.T; ++t) {
			p[t] = (ndt[d][t] + param.alpha) / (ndsum[d] + tAlpha)
					* (ntw[t][word] + param.beta) / (ntsum[t] + vBeta);
		}
		int new_topic = InverseTransformSampler.sample(p,
				randomGenerator.nextDouble());

		z[d][n] = new_topic;
		updateCount(d, new_topic, word, +1);
	}

	/**
	 * Update the counts in the Gibbs sampler.
	 */
	private void updateCount(int d, int topic, int word, int flag) {
		ndt[d][topic] += flag;
		ndsum[d] += flag;
		ntw[topic][word] += flag;
		ntsum[topic] += flag;
	}

	// ------------------------------------------------------------------------
	// Posterior Distribution Computation
	// ------------------------------------------------------------------------

	/**
	 * After burn in phase, update the posterior distributions every sample lag.
	 */
	private void updatePosteriorDistribution() {
		for (int d = 0; d < param.D; ++d) {
			for (int t = 0; t < param.T; ++t) {
				thetasum[d][t] += (ndt[d][t] + param.alpha)
						/ (ndsum[d] + tAlpha);
			}
		}

		for (int t = 0; t < param.T; ++t) {
			for (int w = 0; w < param.V; ++w) {
				phisum[t][w] += (ntw[t][w] + param.beta) / (ntsum[t] + vBeta);
			}
		}
		++numstats;
	}

	/**
	 * Compute the posterior distributions.
	 */
	private void computePosteriorDistribution() {
		computeDocumentTopicDistribution();
		computeTopicWordDistribution();
	}

	/**
	 * Document-topic distribution: theta[][].
	 */
	private void computeDocumentTopicDistribution() {
		if (param.sampleLag > 0) {
			for (int d = 0; d < param.D; ++d) {
				for (int t = 0; t < param.T; ++t) {
					theta[d][t] = thetasum[d][t] / numstats;
				}
			}
		} else {
			for (int d = 0; d < param.D; ++d) {
				for (int t = 0; t < param.T; ++t) {
					theta[d][t] = (ndt[d][t] + param.alpha)
							/ (ndsum[d] + tAlpha);
				}
			}
		}
	}

	/**
	 * Topic-word distribution: phi[][].
	 */
	private void computeTopicWordDistribution() {
		if (param.sampleLag > 0) {
			for (int t = 0; t < param.T; ++t) {
				for (int w = 0; w < param.V; ++w) {
					phi[t][w] = phisum[t][w] / numstats;
				}
			}
		} else {
			for (int t = 0; t < param.T; ++t) {
				for (int w = 0; w < param.V; ++w) {
					phi[t][w] = (ntw[t][w] + param.beta) / (ntsum[t] + vBeta);
				}
			}
		}
	}

	@Override
	public double[][] getTopicWordDistribution() {
		return phi;
	}

	@Override
	public double[][] getDocTopicDistribution() {
		// TODO Auto-generated method stub
		return theta;
	}
}
