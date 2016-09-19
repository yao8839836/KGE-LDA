package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.Corpus;
import util.ReadWriteFile;

public class PMIByIndex20ng {

	public static final int wiki_docs = 4776093;

	/**
	 * 算一个主题的PMI (Newman et al., 2010)，文档共生
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

			String content = ReadWriteFile.getTextContent(new File("file//20ng_word_wiki_small_index//" + word));

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

		String topic_file = "file//" + args[0] + ".txt";

		String content = ReadWriteFile.getTextContent(new File(topic_file));

		String[] lines = content.split("\n");

		List<String> vocab = Corpus.getVocab(args[1]);

		List<String[]> topics = new ArrayList<>();

		for (String line : lines) {

			String[] words = line.trim().split("\t");

			String[] words_index = new String[words.length];

			for (int i = 0; i < words.length; i++) {
				words_index[i] = "" + vocab.indexOf(words[i]);
			}

			if (words.length == 10) {

				topics.add(words_index);

			}

			// if (line.contains(":")) {
			//
			// line = line.substring(line.indexOf(':') + 1, line.length())
			// .trim();
			//
			// System.out.println(line);
			//
			// String[] temp = line.split(" ");
			//
			// System.out.println(temp.length);
			//
			// String[] words = new String[10];
			//
			// for (int i = 0; i < words.length; i++) {
			// words[i] = temp[i];
			// }
			//
			// topics.add(words);
			// }
		}

		StringBuilder sb = new StringBuilder();
		int topic_num = topics.size();

		System.out.println("主题数: " + topic_num);

		double pmi_total = 0;

		for (String[] topic : topics) {

			StringBuilder topic_str = new StringBuilder();

			for (String word_index : topic) {

				int index = Integer.parseInt(word_index);

				topic_str.append(vocab.get(index) + "\t");
			}

			double pmi = pmi(topic);

			topic_str.append(pmi);

			sb.append(topic_str.toString().trim() + "\n");

			pmi_total += pmi;
		}

		double average_pmi = pmi_total / topic_num;

		sb.append(average_pmi);

		ReadWriteFile.writeFile(topic_file, sb.toString());

		return average_pmi;

	}
}
