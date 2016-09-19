package topic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Concept topic model
 * 
 * @author: Liang Yao
 * @email yaoliang@zju.edu.cn
 */

public class CTM {

	int[][] documents;

	int V;

	int K;

	int C;

	Map<Integer, Set<Integer>> word_concept;

	Map<Integer, Set<Integer>> concept_word;

	double alpha;

	double beta;

	double beta_bar;

	int[][] z;

	int[][] nw;

	int[][] nwc;

	int[][] nd;

	int[] nwsum;

	int[] nwcsum;

	int[] ndsum;

	int iterations;

	public CTM(int[][] documents, int V, Map<Integer, Set<Integer>> word_concept,
			Map<Integer, Set<Integer>> concept_word) {

		this.documents = documents;
		this.V = V;

		this.word_concept = word_concept;

		this.concept_word = concept_word;

	}

	public void initialState() {

		int D = documents.length;
		nw = new int[V][K];
		nwc = new int[V][C];
		nd = new int[D][K + C];
		nwsum = new int[K];
		nwcsum = new int[C];
		ndsum = new int[D];

		z = new int[D][];
		for (int d = 0; d < D; d++) {

			int Nd = documents[d].length;

			z[d] = new int[Nd];

			for (int n = 0; n < Nd; n++) {

				int topic = (int) (Math.random() * (K + C));

				z[d][n] = topic;

				if (topic < K)
					updateCount(d, topic, documents[d][n], +1);
				else {
					// System.out.println(topic + "\t" + (topic - K) + "\t" + C
					// + "\t" + V);
					updateCount(d, topic - K, documents[d][n], +1);
				}

			}
		}

	}

	public void markovChain(int K, int C, double alpha, double beta, double beta_bar, int iterations) {

		this.K = K;
		this.C = C;
		this.alpha = alpha;
		this.beta = beta;
		this.beta_bar = beta_bar;
		this.iterations = iterations;

		initialState();

		for (int i = 0; i < this.iterations; i++) {

			System.out.println("iteration : " + i);
			gibbs();
		}
	}

	public void gibbs() {

		for (int d = 0; d < z.length; d++) {
			for (int n = 0; n < z[d].length; n++) {

				int topic = sampleFullConditional(d, n);
				z[d][n] = topic;

			}
		}
	}

	int sampleFullConditional(int d, int n) {

		int topic = z[d][n];

		if (topic < K)
			updateCount(d, topic, documents[d][n], +1);
		else
			updateCount(d, topic - K, documents[d][n], +1);

		Set<Integer> concepts = word_concept.get(documents[d][n]);

		List<Integer> concept_list = new ArrayList<>(concepts);

		int concept_size = concept_list.size();

		double[] p = new double[K + concept_size];

		for (int k = 0; k < K; k++) {

			p[k] = (nd[d][k] + alpha) / (ndsum[d] + (K + C) * alpha) * (nw[documents[d][n]][k] + beta)
					/ (nwsum[k] + V * beta);
		}

		for (int concept : concepts) {

			Set<Integer> words = concept_word.get(concept);

			int index = concept_list.indexOf(concept);

			int words_size = words.size();
			p[K + index] = (nd[d][K + concept] + alpha) / (ndsum[d] + (K + C) * alpha)
					* (nwc[documents[d][n]][concept] + beta_bar) / (nwcsum[concept] + words_size * beta_bar);
		}

		int sample_index = sample(p);

		if (sample_index < K)
			updateCount(d, sample_index, documents[d][n], +1);
		else
			updateCount(d, concept_list.get(sample_index - K), documents[d][n], +1);

		return topic;

	}

	int sample(double[] p) {

		int topic = 0;
		for (int k = 1; k < p.length; k++) {
			p[k] += p[k - 1];
		}
		double u = Math.random() * p[p.length - 1];
		for (int t = 0; t < p.length; t++) {
			if (u < p[t]) {
				topic = t;
				break;
			}
		}
		return topic;
	}

	void updateCount(int d, int topic, int word, int flag) {

		if (topic < K) {

			nd[d][topic] += flag;
			ndsum[d] += flag;
			nw[word][topic] += flag;
			nwsum[topic] += flag;

		} else {

			nd[d][topic] += flag;
			ndsum[d] += flag;
			nwc[word][topic] += flag;
			nwcsum[topic] += flag;

		}

	}

	public double[][] estimateTheta() {
		double[][] theta = new double[documents.length][K];
		for (int d = 0; d < documents.length; d++) {
			for (int k = 0; k < K; k++) {
				theta[d][k] = (nd[d][k] + alpha) / (ndsum[d] + (K + C) * alpha);
			}
		}
		return theta;
	}

	public double[][] estimatePhi() {
		double[][] phi = new double[K][V];
		for (int k = 0; k < K; k++) {
			for (int w = 0; w < V; w++) {
				phi[k][w] = (nw[w][k] + beta) / (nwsum[k] + V * beta);
			}
		}
		return phi;
	}

}
