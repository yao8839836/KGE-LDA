package utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utility.Corpus;
import utility.ReadWriteFile;

public class PMIByIndex20ng {

	public static final int wiki_docs = 4776093;

	/**
	 * 算一个主题的PMI (Newman et al., 2010)，文档共�?
	 * 
	 * @param topic
	 * @return
	 * @throws IOException
	 */
	public static double pmi(String[] topic) throws IOException {

		Set<Set<String>> word_pairs = new HashSet<>();

		for (String word_i : topic) {

			for (String word_j : topic) {

				if (!word_i.equals(word_j)) {

					Set<String> word_pair = new HashSet<>();

					word_pair.add(word_i);

					word_pair.add(word_j);

					word_pairs.add(word_pair);

				}
			}
		}

		List<Set<String>> word_pair_list = new ArrayList<>(word_pairs);

		/*
		 * 词出现的维基词条集合
		 */

		Map<String, Set<String>> word_wikis = new HashMap<>();

		for (String word : topic) {

			String content = ReadWriteFile.getTextContent(new File("file\\20ng_word_wiki_small_index\\" + word));

			String[] lines = content.split("\n");

			Set<String> wikis = new HashSet<>();

			for (String line : lines) {
				wikis.add(line);
			}

			word_wikis.put(word, wikis);

		}

		int length = word_pair_list.size();

		int[] count_1 = new int[length];

		int[] count_2 = new int[length];

		int[] count = new int[length];

		for (int index = 0; index < length; index++) {

			Set<String> pair = word_pair_list.get(index);

			List<String> two_words = new ArrayList<>(pair);

			String word_1 = two_words.get(0);

			String word_2 = two_words.get(1);

			Set<String> wikis_1 = word_wikis.get(word_1);

			Set<String> wikis_2 = word_wikis.get(word_2);

			count_1[index] = wikis_1.size();

			count_2[index] = wikis_2.size();

			for (String wiki : wikis_1) {
				if (wikis_2.contains(wiki))
					count[index]++;
			}

		}

		double[] pmi = new double[length];

		for (int index = 0; index < length; index++) {

			double p_i = (double) count_1[index] / wiki_docs;

			double p_j = (double) count_2[index] / wiki_docs;

			double p_i_j = (double) (count[index] + 1) / wiki_docs;

			pmi[index] = Math.log(p_i_j / (p_i * p_j));

		}

		double topic_pmi = 0;

		for (int i = 0; i < length; i++) {
			topic_pmi += pmi[i];
		}

		return topic_pmi;
	}

	public static double main(String[] args) throws IOException {

		String topic_file = "..//Data//Output//20ng//LearningIteration1//DomainModels//20ng//" + args[0] + ".twords";

		String content = ReadWriteFile.getTextContent(new File(topic_file));

		String[] lines = content.split("\n");

		List<String> vocab = Corpus.getVocab(args[1]);

		int K = lines[0].split("\t").length;

		String[][] topic_matrix = new String[K][10];

		for (int i = 1; i < lines.length; i++) {

			String[] temp = lines[i].split("\t");

			for (int k = 0; k < K; k++) {
				topic_matrix[k][i - 1] = vocab.indexOf(temp[k]) + "";
			}

		}

		int topic_num = topic_matrix.length;

		System.out.println("主题数 : " + K);

		double pmi_total = 0;

		for (String[] topic : topic_matrix) {

			double pmi = pmi(topic);

			pmi_total += pmi;
		}

		return pmi_total / topic_num;

	}
}
