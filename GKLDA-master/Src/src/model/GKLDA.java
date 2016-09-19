package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import knowledge.MustSet;
import knowledge.MustSets;
import nlp.Corpus;
import utility.ArrayAllocationAndInitialization;
import utility.ExceptionUtility;
import utility.FileReaderAndWriter;
import utility.InverseTransformSampler;
import utility.ItemWithValue;

/**
 * This implements the GK-LDA (Chen et al., CIKM 2013) which is the first
 * knowledge-based topic model to deal with wrong knowledge.
 * 
 * @author Zhiyuan (Brett) Chen
 * @email czyuanacm@gmail.com
 */
public class GKLDA extends TopicModel {
	private int MS = 0; // #Must-sets.

	/******************* Hyperparameters *********************/
	// The hyperparameter for the document-topic distribution.
	// alpha is in the variable param in TopicModel.
	private double tAlpha = 0;
	// The hyperparameter for the topic-mset distribution.
	// beta is in the variable param in TopicModel.
	private double sBeta = 0;
	// The hyperparameter for the topic-mset-word distribution.
	// gamma[ms][i] depends on the size of the must-set ms and gamma.
	private double[][] gamma = null;
	// vGamma[t][ms] is sum_{i} randaGamma[ms][i] * gamma
	// size T * MS.
	private double[][] vGamma = null;

	/******************* Posterior distributions *********************/
	private double[][] theta = null; // Document-topic distribution, size D * T.
	private double[][] thetasum = null; // Cumulative document-topic
										// distribution, size
										// D * T.
	private double[][] phi = null; // Topic-mset distribution, size T * MS.
	private double[][] phisum = null; // Cumulative topic-mset distribution,
										// size T * MS.
	private double[][][] eta; // Topic-mset-word distribution, size T * MS *
								// size of ms. The third index is the word index
								// in the set, instead of the word id, in order
								// to save memory space.
	private double[][][] etasum; // Cumulative topic-mset-word distribution,
									// size T * MS * size of ms.
	private double[][] omega; // Topic-word distribution, size T * V.
								// omega[t][w] = sum_{ms} phi[t][ms] *
								// eta[t][ms][w_i].
	// Number of times to add the sum arrays, such as thetasum and phisum.
	public int numstats = 0;

	/******************* Temp variables while sampling *********************/
	// z is defined in the superclass TopicModel.
	private int[][] y; // Set index assignment for each word.
	// ndt[d][t]: the counts of document d having topic t.
	private double[][] ndt = null;
	// ndsum[d]: the counts of document d having any topic.
	private double[] ndsum = null;
	// ntw[t][ms]: the counts of mset ms appearing under topic t.
	private double[][] nts = null;
	// ntsum[t]: the counts of any mset appearing under topic t.
	private double[] ntsum = null;
	// ntsw[k][ms][i]: number of ith word in the mset ms assigned to topic t and
	// set ms, size T * MS * size of ms.
	private double[][][] ntsw = null;
	// ntsw[k][ms]: number of any word assigned to topic t and mset ms, size T *
	// MS * size of ms.
	private double[][] ntssum = null;

	/******************* Knowledge *********************/
	// Word correlation metric, used to detect wrong knowledge.
	private WordCorrelationMetric wcm = null;
	private MustSets mustsets = null;
	// urn[ms][w1_i][w2_i]: the generalized polya urn model
	// matrix of word ms[w1_i] and ms[w2_i] under
	// must-set ms, size: MS * size of ms * size of ms.
	private double[][][] urn;

	/**
	 * Create a new topic model with all variables initialized. The z[][] is
	 * randomly assigned.
	 */
	public GKLDA(Corpus corpus2, ModelParameters param2) {
		super(corpus2, param2);

		/******************* Knowledge *********************/
		// This is different from M-LDA as we need to construct a word
		// correlation metric that is used to detect wrong knowledge and try to
		// fix them by adding singleton must-sets.
		TopicModel LDA_currentDomain = findCurrentDomainTopicModel(param.topicModelList_LDA);
		wcm = getWordCorrelationMetricFromTopicModelResult(LDA_currentDomain);

		mustsets = readKnowledgeFromFile(param.knowledgeFilePath);

		detectWrongKnowledgeAndAddSingtonSet(mustsets, wcm);
		MS = mustsets.size();

		/******************* Hyperparameters *********************/
		tAlpha = param.T * param.alpha;
		sBeta = MS * param.beta;

		// Allocate memory for temporary variables and initialize their
		// values.
		allocateMemoryForTempVariables();

		// Initialize the first status of Markov chain using topic
		// assignments from the LDA result.
		TopicModel topicmodel_currentDomain = findCurrentDomainTopicModel(param.topicModelList_LDA);
		initializeFirstMarkovChainUsingExistingZ(topicmodel_currentDomain.z);
	}

	/**
	 * Create a new topic model with all variables initialized. The z[][] is
	 * assigned to the value loaded from other models.
	 */
	public GKLDA(Corpus corpus2, ModelParameters param2, int[][] z2, double[][] twdist) {
		super(corpus2, param2);
		tAlpha = param.T * param.alpha;
		sBeta = MS * param.beta;
		// Allocate memory for temporary variables and initialize their
		// values.
		allocateMemoryForTempVariables();
		// Assign z2 to z.
		z = z2; // Here we do not call initializeFirstMarkovChainUsingExistingZ
		// because we do not load the knowledge.
		// Assign Topic-Word distribution.
		omega = twdist;
	}

	// ------------------------------------------------------------------------
	// Knowledge Extraction.
	// ------------------------------------------------------------------------

	/**
	 * Read the knowledge in the form of must-set from the file. If the
	 * knowledge word does not appear in the domain, ignore it.
	 */
	private MustSets readKnowledgeFromFile(String knowledgeFilePath) {
		MustSets msets = new MustSets();
		ArrayList<String> lines = FileReaderAndWriter.readFileAllLines(knowledgeFilePath);
		for (String line : lines) {
			if (line.trim().equals("")) {
				continue;
			}
			MustSet mset = MustSet.getMustSetFromALine(line, corpus.vocab);
			if (mset != null && mset.wordset.size() >= 2) {
				// Ignore singleton set here because we will add singleton sets
				// for those words that do not have must-set.
				msets.addMustSet(mset);
			}
		}
		return msets;
	}

	/**
	 * Construct the Word Correlation Metric from the topic model result of this
	 * domain from the last iteration.
	 */
	private WordCorrelationMetric getWordCorrelationMetricFromTopicModelResult(TopicModel topicmodel_currentDomain) {
		return new WordCorrelationMetric(topicmodel_currentDomain.getTopWordStrsWithProbabilitiesUnderTopics(-1));
	}

	/**
	 * Detect the wrong knowledge and add singleton set to the knowledge for
	 * wrong knowledge words.
	 */
	private void detectWrongKnowledgeAndAddSingtonSet(MustSets mustsets, WordCorrelationMetric wcm) {
		for (int v = 0; v < corpus.vocab.size(); ++v) {
			String wordstr = corpus.vocab.getWordstrByWordid(v);

			ArrayList<MustSet> mustsetList = mustsets.getMustSetListGivenWordstr(wordstr);
			boolean hasGoodSynset = false;
			boolean hasSingleWordSynset = false;
			for (MustSet mustset : mustsetList) {
				if (wcm.isMustSetGoodForWord(mustset, wordstr, param.wordCorrelationValueThresholdForMustSetQuality)) {
					hasGoodSynset = true;
					break;
				}
				if (mustset.size() == 1) {
					hasSingleWordSynset = true;
					break;
				}
			}
			if (!hasGoodSynset && !hasSingleWordSynset) {
				// The word does not have at least one good must-set and the
				// singleton must-set does not exist, we need to
				// add a singleton must-set {w}.
				MustSet mustset = new MustSet(wordstr);
				mustsets.addMustSet(mustset);
			}
		}
	}

	// ------------------------------------------------------------------------
	// Memory Allocation and Initialization
	// ------------------------------------------------------------------------

	/**
	 * Allocate memory for temporary variables and initialize their values. Note
	 * that z[][] and y[][] are not created in this function, but in the
	 * function initializeFirstMarkovChainRandomly().
	 * 
	 * We Allocate gamma, eta, etasum, ntsw specifically to save the memory. For
	 * each must-set ms, we only allocate the size of words inside it.
	 */
	private void allocateMemoryForTempVariables() {
		/******************* Hyperparameters *********************/
		gamma = new double[MS][];
		for (int ms = 0; ms < MS; ++ms) {
			int size = mustsets.getMustSet(ms).size();
			gamma[ms] = new double[size];
			for (int i = 0; i < size; ++i) {
				gamma[ms][i] = getGammaBasedOnMustsetSize(size);
			}
		}
		vGamma = ArrayAllocationAndInitialization.allocateAndInitialize(vGamma, param.T, MS);
		for (int t = 0; t < param.T; ++t) {
			for (int ms = 0; ms < MS; ++ms) {
				int size = mustsets.getMustSet(ms).size();
				for (int i = 0; i < size; ++i) {
					vGamma[t][ms] += gamma[ms][i];
				}
			}
		}

		/******************* Posterior distributions *********************/
		theta = ArrayAllocationAndInitialization.allocateAndInitialize(theta, param.D, param.T);
		phi = ArrayAllocationAndInitialization.allocateAndInitialize(phi, param.T, MS);
		eta = new double[param.T][MS][];
		for (int t = 0; t < param.T; ++t) {
			for (int ms = 0; ms < MS; ++ms) {
				int size = mustsets.getMustSet(ms).size();
				eta[t][ms] = new double[size];
				for (int i = 0; i < size; ++i) {
					eta[t][ms][i] = 0.0;
				}
			}
		}
		omega = ArrayAllocationAndInitialization.allocateAndInitialize(omega, param.T, param.V);

		if (param.sampleLag > 0) {
			thetasum = ArrayAllocationAndInitialization.allocateAndInitialize(thetasum, param.D, param.T);
			phisum = ArrayAllocationAndInitialization.allocateAndInitialize(phisum, param.T, MS);
			etasum = new double[param.T][MS][];
			for (int t = 0; t < param.T; ++t) {
				for (int ms = 0; ms < MS; ++ms) {
					int size = mustsets.getMustSet(ms).size();
					etasum[t][ms] = new double[size];
					for (int i = 0; i < size; ++i) {
						etasum[t][ms][i] = 0.0;
					}
				}
			}
		}

		/*******************
		 * Temp variables while sampling
		 *********************/
		ndt = ArrayAllocationAndInitialization.allocateAndInitialize(ndt, param.D, param.T);
		ndsum = ArrayAllocationAndInitialization.allocateAndInitialize(ndsum, param.D);
		nts = ArrayAllocationAndInitialization.allocateAndInitialize(nts, param.T, MS);
		ntsum = ArrayAllocationAndInitialization.allocateAndInitialize(ntsum, param.T);
		ntsw = new double[param.T][MS][];
		for (int t = 0; t < param.T; ++t) {
			for (int ms = 0; ms < MS; ++ms) {
				int size = mustsets.getMustSet(ms).size();
				ntsw[t][ms] = new double[size];
				for (int i = 0; i < size; ++i) {
					ntsw[t][ms][i] = 0;
				}
			}
		}
		ntssum = ArrayAllocationAndInitialization.allocateAndInitialize(ntssum, param.T, MS);

		/******************* Knowledge *********************/
		// This is different from M-LDA. We assign the word correlation metric
		// value to the urn matrix.
		urn = new double[MS][][];
		for (int ms = 0; ms < MS; ++ms) {
			int size = mustsets.getMustSet(ms).size();
			urn[ms] = new double[size][size];
			for (int w1_i = 0; w1_i < size; ++w1_i) {
				for (int w2_i = 0; w2_i < size; ++w2_i) {
					if (w1_i == w2_i) {
						urn[ms][w1_i][w2_i] = 1.0;
					} else {
						MustSet mustset = mustsets.getMustSet(ms);
						String wordstr1 = mustset.getWordstr(w1_i);
						String wordstr2 = mustset.getWordstr(w2_i);
						double distance = wcm.getWordCorrelationValue(wordstr1, wordstr2);
						urn[ms][w1_i][w2_i] = distance;
					}
				}
			}
		}
	}

	/**
	 * Get gamma[ms][i] which depends on the size of the must-set ms and gamma.
	 */
	public double getGammaBasedOnMustsetSize(int size) {
		if (size == 1) {
			return 1;
		}
		return param.lambdaForComputingGamma / Math.exp(size);
	}

	/**
	 * Initialized the first status of Markov chain using topic assignments from
	 * the LDA results.
	 */
	private void initializeFirstMarkovChainUsingExistingZ(int[][] z2) {
		z = new int[param.D][];
		y = new int[param.D][];
		for (int d = 0; d < param.D; ++d) {
			int N = docs[d].length;
			z[d] = new int[N];
			y[d] = new int[N];

			for (int n = 0; n < N; ++n) {
				int word = docs[d][n];
				String wordstr = docsStr[d][n];

				// Copy the topic assignment from z2 to z.
				int topic = z2[d][n];
				z[d][n] = topic;
				// Sample a must-set.
				ArrayList<MustSet> mustsetList = mustsets.getMustSetListGivenWordstr(wordstr);
				int setIndex = 0;
				if (mustsetList.size() > 1) {
					// Only random choose when there is more than one option.
					setIndex = (int) Math.floor(randomGenerator.nextDouble() * mustsetList.size());
				}
				MustSet mustset = mustsetList.get(setIndex);
				int ms = mustsets.getMustSetIndex(mustset);
				y[d][n] = ms;

				updateCount(d, topic, ms, word, wordstr, +1);
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
			// System.out.println(i);
			for (int d = 0; d < param.D; ++d) {
				int N = docs[d].length;
				for (int n = 0; n < N; ++n) {
					// Sample from p(z_i|z_-i, w)
					sampleTopicAssignment(d, n);
				}
			}

			if (i >= param.nBurnin && param.sampleLag > 0 && i % param.sampleLag == 0) {
				updatePosteriorDistribution();
			}
		}
	}

	/**
	 * Sample a topic assigned to the word in position n of document d.
	 */
	private void sampleTopicAssignment(int d, int n) {
		int old_topic = z[d][n];
		int ms = y[d][n];
		int word = docs[d][n];
		String wordstr = docsStr[d][n];
		updateCount(d, old_topic, ms, word, wordstr, -1);

		// Compute the conditional distribution of Gibbs sampler.
		ArrayList<MustSet> mustsetList = mustsets.getMustSetListGivenWordstr(wordstr);
		double[] p = new double[param.T * mustsetList.size()];
		for (int t = 0; t < param.T; ++t) {
			for (int si = 0; si < mustsetList.size(); ++si) {
				MustSet mustset = mustsetList.get(si);
				int s = mustsets.getMustSetIndex(mustset);
				int wordIndex = mustset.getWordIndex(wordstr);
				p[t * mustsetList.size() + si] = (ndt[d][t] + param.alpha) / (ndsum[d] + tAlpha)
						* (nts[t][s] + param.beta) / (ntsum[t] + sBeta) * (ntsw[t][s][wordIndex] + gamma[s][wordIndex])
						/ (ntssum[t][s] + vGamma[t][s]);
				ExceptionUtility.assertAsException(p[t * mustsetList.size() + si] >= 0, "The probability is negative!");
			}
		}

		// Sample a topic and a must-set.
		int pairIndex = InverseTransformSampler.sample(p, randomGenerator.nextDouble());
		int new_topic = pairIndex / mustsetList.size();
		MustSet new_mustset = mustsetList.get(pairIndex % mustsetList.size());
		int new_ms = mustsets.getMustSetIndex(new_mustset);
		z[d][n] = new_topic;
		y[d][n] = new_ms;

		updateCount(d, new_topic, new_ms, word, wordstr, +1);
	}

	/**
	 * Update the counts in the Gibbs sampler.
	 * 
	 * @param i
	 */
	private void updateCount(int d, int topic, int ms, int word, String wordstr, int flag) {
		ndt[d][topic] += flag;
		ndsum[d] += flag;

		MustSet mustset = mustsets.getMustSet(ms);
		int w1_i = mustset.getWordIndex(wordstr);
		// Iterate the words under this must-set.
		for (int w2_i = 0; w2_i < mustset.size(); ++w2_i) {
			double count = flag * urn[ms][w1_i][w2_i];
			nts[topic][ms] += count;
			ntsum[topic] += count;
			// This is different from M-LDA.
			ntsw[topic][ms][w2_i] += count;
			ntssum[topic][ms] += count;
		}
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
				thetasum[d][t] += (ndt[d][t] + param.alpha) / (ndsum[d] + tAlpha);
			}
		}

		for (int t = 0; t < param.T; ++t) {
			for (int ms = 0; ms < MS; ++ms) {
				phisum[t][ms] += (nts[t][ms] + param.beta) / (ntsum[t] + sBeta);
			}
		}

		for (int t = 0; t < param.T; ++t) {
			for (int ms = 0; ms < MS; ++ms) {
				int size = mustsets.getMustSet(ms).size();
				for (int i = 0; i < size; ++i) {
					etasum[t][ms][i] += (ntsw[t][ms][i] + gamma[ms][i]) / (ntssum[t][ms] + vGamma[t][ms]);
				}
			}
		}

		++numstats;
	}

	/**
	 * Compute the posterior distributions.
	 */
	private void computePosteriorDistribution() {
		computeDocumentTopicDistribution();
		computeTopicMustsetDistribution();
		computeTopicMustsetWordDistribution();
		computeTopicWordDistribution();
	}

	/**
	 * Document-topic distribution: theta[][].
	 */
	private void computeDocumentTopicDistribution() {
		if (param.sampleLag > 0) {
			for (int d = 0; d < param.D; d++) {
				for (int t = 0; t < param.T; t++) {
					theta[d][t] = thetasum[d][t] / numstats;
				}
			}
		} else {
			for (int d = 0; d < param.D; d++) {
				for (int t = 0; t < param.T; t++) {
					theta[d][t] = (ndt[d][t] + param.alpha) / (ndsum[d] + tAlpha);
				}
			}
		}
	}

	/**
	 * Topic-mset distribution: phi[][].
	 */
	private void computeTopicMustsetDistribution() {
		if (param.sampleLag > 0) {
			for (int t = 0; t < param.T; t++) {
				for (int ms = 0; ms < MS; ms++) {
					phi[t][ms] = phisum[t][ms] / numstats;
				}
			}
		} else {
			for (int t = 0; t < param.T; t++) {
				for (int ms = 0; ms < MS; ms++) {
					phi[t][ms] = (nts[t][ms] + param.beta) / (ntsum[t] + sBeta);
				}
			}
		}
	}

	/**
	 * Topic-mset-word distribution: eta[][][].
	 */
	private void computeTopicMustsetWordDistribution() {
		if (param.sampleLag > 0) {
			for (int t = 0; t < param.T; ++t) {
				for (int ms = 0; ms < MS; ++ms) {
					int size = mustsets.getMustSet(ms).size();
					for (int i = 0; i < size; ++i) {
						eta[t][ms][i] = etasum[t][ms][i] / numstats;
					}
				}
			}
		} else {
			for (int t = 0; t < param.T; ++t) {
				for (int ms = 0; ms < MS; ++ms) {
					int size = mustsets.getMustSet(ms).size();
					for (int i = 0; i < size; ++i) {
						eta[t][ms][i] = (ntsw[t][ms][i] + gamma[ms][i]) / (ntssum[t][ms] + vGamma[t][ms]);
					}
				}
			}
		}
	}

	/**
	 * Topic-word distribution: omega[][][], not estimated from the model, but
	 * computed using phi and eta.
	 * 
	 * omega[t][w] = sum_{s} phi[t][s] * eta[t][s][w_i]
	 */
	private void computeTopicWordDistribution() {
		// Initialized to 0.
		for (int t = 0; t < param.T; ++t) {
			for (int w = 0; w < param.V; ++w) {
				omega[t][w] = 0;
			}
		}

		for (int t = 0; t < param.T; t++) {
			for (int ms = 0; ms < MS; ++ms) {
				ArrayList<String> wordstrList = mustsets.getMustSet(ms).wordset.wordstrsList;
				for (int i = 0; i < wordstrList.size(); ++i) {
					// P(w|t) = Sum_{s} P(w|s,t) * P(s|t).
					int word = corpus.vocab.getWordidByWordstr(wordstrList.get(i));
					double prob = phi[t][ms] * eta[t][ms][i];
					omega[t][word] += prob;
				}
			}
		}
	}

	@Override
	public double[][] getTopicWordDistribution() {
		return omega;
	}

	@Override
	/**
	 * Print out the must-links for each topic.
	 */
	public void printKnowledge(String filepath) {
		StringBuilder sbKnowledge = new StringBuilder();
		sbKnowledge.append(mustsets.toString());
		FileReaderAndWriter.writeFile(filepath, sbKnowledge.toString());
	}

	@Override
	public double[][] getDocTopicDistribution() {
		// TODO Auto-generated method stub
		return theta;
	}

}

class WordCorrelationMetric {
	private ArrayList<ArrayList<ItemWithValue>> topWordsUnderTopics = null;
	private ArrayList<Map<String, Double>> wordstrToProbUnderTopicMapList = null;

	public WordCorrelationMetric(ArrayList<ArrayList<ItemWithValue>> topWordsUnderTopics2) {
		topWordsUnderTopics = topWordsUnderTopics2;
		wordstrToProbUnderTopicMapList = new ArrayList<Map<String, Double>>();
		for (int i = 0; i < topWordsUnderTopics.size(); ++i) {
			ArrayList<ItemWithValue> topWords = topWordsUnderTopics.get(i);
			Map<String, Double> mpWordToProb = new HashMap<String, Double>();
			for (ItemWithValue wordWithProb : topWords) {
				String wordStr = wordWithProb.getIterm().toString();
				double prob = wordWithProb.getValue();
				if (!mpWordToProb.containsKey(wordStr)) {
					mpWordToProb.put(wordStr, prob);
				}
			}
			wordstrToProbUnderTopicMapList.add(mpWordToProb);
		}
	}

	/**
	 * Get the word correlation value between two words.
	 */
	public double getWordCorrelationValue(String wordstr1, String wordstr2) {
		double higestProb1 = getHighestProbInTopics(wordstr1);
		double higestProb2 = getHighestProbInTopics(wordstr2);
		if (higestProb1 < higestProb2) {
			// If the first word has lower highest probability, then exchange
			// the words.
			String tmp = wordstr1;
			wordstr1 = wordstr2;
			wordstr2 = tmp;
		}

		// Find the topic (topicIndex) that word1 has the highest probability.
		double highestProb = 0.0;
		int topicIndex = -1;
		for (int i = 0; i < topWordsUnderTopics.size(); ++i) {
			Map<String, Double> mpWordToProb = wordstrToProbUnderTopicMapList.get(i);
			double prob = mpWordToProb.get(wordstr1);
			if (prob > highestProb) {
				highestProb = prob;
				topicIndex = i;
			}
		}

		// Find the probability of word2 in topic (topicIndex).
		double prob2 = wordstrToProbUnderTopicMapList.get(topicIndex).get(wordstr2);

		return getRatio(highestProb, prob2);
	}

	/**
	 * Get the ratio between two probabilities.
	 */
	private double getRatio(double highestProb, double prob2) {
		return 1.0 / (highestProb / prob2);
	}

	/**
	 * Get the highest probability of the word under each topic.
	 */
	private double getHighestProbInTopics(String wordStr) {
		double highestProb = 0.0;
		for (int i = 0; i < topWordsUnderTopics.size(); ++i) {
			if (wordstrToProbUnderTopicMapList == null) {
				System.out.println("NULL");
			}
			Map<String, Double> mpWordToProb = wordstrToProbUnderTopicMapList.get(i);
			if (mpWordToProb == null || wordStr == null) {
				System.out.println("NULL");
			}
			double prob = mpWordToProb.get(wordStr);
			if (prob > highestProb) {
				highestProb = prob;
			}
		}
		return highestProb;
	}

	/**
	 * Judge if the must-set is good for the word.
	 * 
	 * Def: the must-set s is good for the word w if either of the following
	 * holds:
	 * 
	 * 1. The maximum of the distance between the word w and every other word w'
	 * in the must-set is larger than the threshold.
	 */
	public boolean isMustSetGoodForWord(MustSet mset, String wordstr, double threshold) {
		return getLargestWordCorrelationWithWordInMustSet(mset, wordstr) >= threshold;
	}

	public double getLargestWordCorrelationWithWordInMustSet(MustSet mset, String wordstr) {
		double cloestDistance = 0;
		for (String wordstr2 : mset) {
			if (wordstr.equals(wordstr2)) {
				continue;
			}
			double distance = getWordCorrelationValue(wordstr, wordstr2);
			if (distance > cloestDistance) {
				cloestDistance = distance;
			}
		}
		return cloestDistance;
	}
}
