package topic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CorrLDA {

	int[][] documents;

	int[][] entities;

	int V;

	int E;

	int K;

	double alpha;

	double beta;

	double beta_bar;

	int[][] z;

	int[][] z_bar;

	int[][] nw;

	int[][] ne;

	int[][] nd;

	int[] nwsum;

	int[] nesum;

	int[] ndsum;

	int iterations;

	public CorrLDA(int[][] documents, int[][] entities, int V, int E) {

		this.documents = documents;
		this.entities = entities;
		this.V = V;
		this.E = E;

	}

	public void initialState() {

		int D = documents.length;

		nd = new int[D][K];
		ndsum = new int[D];

		nw = new int[V][K];
		nwsum = new int[K];

		ne = new int[E][K];
		nesum = new int[K];

		z = new int[D][];
		z_bar = new int[D][];

		for (int d = 0; d < D; d++) {

			// words
			int Nd = documents[d].length;
			z[d] = new int[Nd];

			for (int n = 0; n < Nd; n++) {

				int topic = (int) (Math.random() * K);
				z[d][n] = topic;

				updateCount(d, topic, documents[d][n], +1);
			}

			// entities
			int Ed = entities[d].length;
			z_bar[d] = new int[Ed];

			for (int m = 0; m < Ed; m++) {

				int topic = (int) (Math.random() * K);
				z_bar[d][m] = topic;

				updateEntityCount(d, topic, entities[d][m], +1);
			}

		}

	}

	public void markovChain(int K, double alpha, double beta, double beta_bar, int iterations) {

		this.K = K;
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

		for (int d = 0; d < documents.length; d++) {

			Set<Integer> topic_set = new HashSet<>();

			// words
			for (int n = 0; n < z[d].length; n++) {

				int topic = sampleFullConditional(d, n);
				z[d][n] = topic;

				topic_set.add(topic);

			}

			List<Integer> topic_list = new ArrayList<>(topic_set);

			// entities
			for (int m = 0; m < z_bar[d].length; m++) {

				int topic = sampleFullConditionalEntity(d, m, topic_list);
				z_bar[d][m] = topic;

			}
		}
	}

	int sampleFullConditional(int d, int n) {

		int topic = z[d][n];

		updateCount(d, topic, documents[d][n], -1);

		double[] p = new double[K];

		for (int k = 0; k < K; k++) {

			p[k] = (nd[d][k] + alpha) / (ndsum[d] + K * alpha) * (nw[documents[d][n]][k] + beta)
					/ (nwsum[k] + V * beta);
		}

		topic = sample(p);

		updateCount(d, topic, documents[d][n], +1);

		return topic;

	}

	/**
	 * 只能从当前文档词的主题分配中选择
	 * 
	 * @param d
	 * @param m
	 * @param topic_list
	 * @return
	 */
	int sampleFullConditionalEntity(int d, int m, List<Integer> topic_list) {

		int topic = z_bar[d][m];

		updateEntityCount(d, topic, entities[d][m], -1);

		double[] p = new double[topic_list.size()];

		for (int k = 0; k < p.length; k++) {

			p[k] = ((double) nd[d][topic_list.get(k)]) / (ndsum[d]) * (ne[entities[d][m]][topic_list.get(k)] + beta_bar)
					/ (nesum[topic_list.get(k)] + E * beta_bar);
		}

		int index = sample(p);

		topic = topic_list.get(index);

		updateEntityCount(d, topic, entities[d][m], +1);

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

		nd[d][topic] += flag;
		ndsum[d] += flag;
		nw[word][topic] += flag;
		nwsum[topic] += flag;
	}

	void updateEntityCount(int d, int topic, int entity, int flag) {

		ne[entity][topic] += flag;
		nesum[topic] += flag;

		nd[d][topic] += flag;
		ndsum[d] += flag;
	}

	public double[][] estimateTheta() {
		double[][] theta = new double[documents.length][K];
		for (int d = 0; d < documents.length; d++) {
			for (int k = 0; k < K; k++) {
				theta[d][k] = (nd[d][k] + alpha) / (ndsum[d] + K * alpha);
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

	public double[][] estimatePhiBar() {
		double[][] phi_bar = new double[K][E];
		for (int k = 0; k < K; k++) {
			for (int e = 0; e < E; e++) {
				phi_bar[k][e] = (ne[e][k] + beta_bar) / (nesum[k] + E * beta_bar);
			}
		}
		return phi_bar;
	}

}
