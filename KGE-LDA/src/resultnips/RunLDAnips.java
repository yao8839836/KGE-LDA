package resultnips;

import java.util.List;

import test.PMIByIndexNips;
import topic.LDA;
import util.Common;
import util.Corpus;
import util.ReadWriteFile;

public class RunLDAnips {

	public static double main(String[] args) throws Exception {

		List<String> vocab = Corpus.getVocab("data//vocab_nips.txt");

		int[][] docs = Corpus.getDocuments("data//corpus_nips.txt");

		int K = 30;

		LDA lda = new LDA(docs, vocab.size());

		int iterations = 1000;

		/*
		 * 先验
		 */

		double alpha = (double) 50 / K;
		double beta = 0.01;

		lda.markovChain(K, alpha, beta, iterations);

		double[][] phi = lda.estimatePhi();

		// double[][] theta = lda.estimateTheta();

		// double[][] phi_copy = Common.makeCopy(phi);

		// 将每个主题的前10个词写文件
		double[][] phi_for_write = Common.makeCopy(phi);

		StringBuilder sb = new StringBuilder();

		for (double[] phi_t : phi_for_write) {

			for (int i = 0; i < 10; i++) {

				int max_index = Common.maxIndex(phi_t);

				sb.append(vocab.get(max_index) + "\t");

				phi_t[max_index] = 0;

			}
			sb.append("\n");

		}

		String filename = "file//lda_nips_topics.txt";

		// 语义一致性
		// double average_coherence = Corpus.average_coherence(docs, phi_copy,
		// 15);
		//
		// System.out.println("average coherence : " + average_coherence);
		//
		// sb.append("average coherence\t" + average_coherence);
		//
		ReadWriteFile.writeFile(filename, sb.toString());

		args = new String[2];

		args[0] = "lda_nips_topics";

		args[1] = "data//vocab_nips.txt";

		double pmi = PMIByIndexNips.main(args);

		return pmi;

	}

}
