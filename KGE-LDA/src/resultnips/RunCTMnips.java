package resultnips;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import test.PMIByIndexNips;
import topic.CTM;
import util.Common;
import util.Corpus;
import util.ReadWriteFile;

public class RunCTMnips {

	public static double main(String[] args) throws IOException {

		List<String> vocab = Corpus.getVocab("data//vocab_nips.txt");

		int[][] docs = Corpus.getDocuments("data//corpus_nips.txt");

		int K = 30;
		int V = vocab.size();

		/*
		 * 词的概念目录
		 */

		Map<Integer, Set<Integer>> word_concept = new HashMap<>();

		String content = ReadWriteFile.getTextContent(new File("file//nips_word_sense_wn18_appear_mod.txt"));

		String[] lines = content.split("\n");

		for (String line : lines) {

			String[] temp = line.split("\t");

			Set<Integer> concepts = new HashSet<>();

			if (temp.length > 1) {

				String[] categories = temp[1].split(" ");

				for (String category : categories) {

					int c = Integer.parseInt(category);
					concepts.add(c);
				}

			}

			int w = Integer.parseInt(temp[0]);
			word_concept.put(w, concepts);

		}

		/*
		 * 概念目录的词
		 */

		Map<Integer, Set<Integer>> concept_word = new HashMap<>();

		content = ReadWriteFile.getTextContent(new File("file//nips_sense_word_wn18_appear_mod.txt"));

		lines = content.split("\n");

		for (int i = 0; i < lines.length; i++) {

			String[] temp = lines[i].split("\t");

			Set<Integer> words = new HashSet<>();

			if (temp.length > 1) {

				String[] tokens = temp[1].split(" ");

				for (String token : tokens) {

					int w = vocab.indexOf(token);
					words.add(w);
				}

			}

			concept_word.put(i, words);

		}

		CTM ctm = new CTM(docs, V, word_concept, concept_word);

		int C = concept_word.keySet().size();

		System.out.println(C);
		int iterations = 1000;

		/*
		 * 先验
		 */

		double alpha = (double) 50 / (K);
		double beta = 0.01;

		double beta_bar = 0.01;

		ctm.markovChain(K, C, alpha, beta, beta_bar, iterations);

		double[][] phi = ctm.estimatePhi();

		// double[][] theta = lda.estimateTheta();

		double[][] phi_copy = Common.makeCopy(phi);

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

		String filename = "file//ctm_nips_topics.txt";

		// 语义一致性
		double average_coherence = Corpus.average_coherence(docs, phi_copy, 15);

		System.out.println("average coherence : " + average_coherence);

		sb.append("average coherence\t" + average_coherence);

		ReadWriteFile.writeFile(filename, sb.toString());

		args = new String[2];

		args[0] = "ctm_nips_topics";

		args[1] = "data//vocab_nips.txt";

		double pmi = PMIByIndexNips.main(args);

		return pmi;

	}

}
