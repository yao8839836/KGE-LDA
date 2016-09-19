package resultnips;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;

import knowledge.KnowledgeGraphEmbedding;
import test.PMIByIndexNips;
import topic.SwitchLDA_KGE;
import util.Common;
import util.Corpus;
import util.ReadWriteFile;

public class RunSwitchLDA_KGEnips {

	public static double main(String[] args) throws Exception {

		List<String> vocab = Corpus.getVocab("data//vocab_nips.txt");

		int[][] docs = Corpus.getDocuments("data//corpus_nips.txt");

		System.out.println(docs.length);

		File[] entity_files = new File("file//nips_wordnet_id//").listFiles();

		int[][] entities = Corpus.getEntities(entity_files);

		List<String> entity_list = Corpus.getVocab("knowledge//WN18//entity_appear_nips.txt");

		// 将原始id转成索引

		for (int i = 0; i < entities.length; i++) {

			for (int j = 0; j < entities[i].length; j++) {

				System.out.print(entities[i][j] + "\t");

				entities[i][j] = entity_list.indexOf(entities[i][j] + "");

				System.out.println(entities[i][j]);

			}

		}

		KnowledgeGraphEmbedding kge = new KnowledgeGraphEmbedding("knowledge//WN18//entity2vec_appear_nips.bern");

		Map<Integer, double[]> vector_map = kge.getEntityVector();
		// 单位化
		for (int id : vector_map.keySet()) {

			double[] vec = vector_map.get(id);

			double[] norm = Common.l2normalize(vec);

			vector_map.put(id, norm);

		}

		int dimension = 50;
		SwitchLDA_KGE linklda = new SwitchLDA_KGE(docs, entities, vocab.size(), entity_list.size(), vector_map,
				dimension);

		int K = 30;
		double alpha = (double) 50 / K;
		double beta = 0.01;

		double gamma = 0.01;

		Random randomno = new Random();

		double[] miu_0 = new double[dimension];

		for (int i = 0; i < dimension; i++) {
			miu_0[i] = randomno.nextGaussian();
		}

		miu_0 = Common.l2normalize(miu_0);

		double C_0 = 0.01, m_0 = 0.01, sigma_0 = 0.25;

		int iterations = 1000;

		linklda.markovChain(K, alpha, beta, gamma, miu_0, C_0, m_0, sigma_0, iterations);

		double[][] phi = linklda.estimatePhi();

		// double[][] theta = linklda.estimateTheta();

		double[][] phi_copy = Common.makeCopy(phi);

		StringBuilder sb = new StringBuilder();

		for (double[] phi_t : phi_copy) {

			for (int i = 0; i < 10; i++) {

				int max_index = Common.maxIndex(phi_t);

				sb.append(vocab.get(max_index) + "\t");

				phi_t[max_index] = 0;

			}
			sb.append("\n");

		}

		String filename = "file//kge_switch_lda_nips_topics.txt";

		// 语义一致性
		// double average_coherence = Corpus.average_coherence(docs, phi_copy,
		// 15);
		//
		// System.out.println("average coherence : " + average_coherence);
		//
		// sb.append("average coherence\t" + average_coherence);

		ReadWriteFile.writeFile(filename, sb.toString());

		args = new String[1];

		args = new String[2];

		args[0] = "kge_switch_lda_nips_topics";

		args[1] = "data//vocab_nips.txt";

		double pmi = PMIByIndexNips.main(args);

		return pmi;

	}

}
