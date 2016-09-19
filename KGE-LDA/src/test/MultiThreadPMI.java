package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import util.ReadWriteFile;

public class MultiThreadPMI implements Runnable {

	List<String[]> domain_task_list;

	static StringBuilder sb;

	public MultiThreadPMI(List<String[]> task) {

		sb = new StringBuilder();

		this.domain_task_list = task;

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		for (String[] topic : domain_task_list) {

			try {
				double topic_pmi = pmi(topic);
				System.out.println(topic_pmi);
				sb.append(topic_pmi + "\n");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

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

		int length = word_pair_list.size();

		int[] count_1 = new int[length];

		int[] count_2 = new int[length];

		int[] count = new int[length];

		File[] files = new File("E:\\wiki_full\\").listFiles();

		// long time = System.currentTimeMillis();

		for (File f : files) {

			String content = ReadWriteFile.getTextContent(f);

			for (int index = 0; index < length; index++) {

				Set<String> pair = word_pair_list.get(index);

				List<String> two_words = new ArrayList<>(pair);

				String word_1 = two_words.get(0);

				String word_2 = two_words.get(1);

				if (content.contains(word_1) && content.contains(word_2)) {
					count[index]++;
					count_1[index]++;
					count_2[index]++;
				} else if (content.contains(word_1) && !content.contains(word_2)) {
					count_1[index]++;
				} else if (!content.contains(word_1) && content.contains(word_2)) {
					count_2[index]++;
				}

			}

		}

		// long cost = (System.currentTimeMillis() - time) / 1000;

		// System.out.println("Time cost : " + cost);

		double[] pmi = new double[length];

		for (int index = 0; index < length; index++) {

			double p_i = (double) count_1[index] / files.length;

			double p_j = (double) count_2[index] / files.length;

			double p_i_j = (double) (count[index] + 1) / files.length;

			// if (p_i_j == 0)
			// p_i_j = (double) 1 / files.length;
			// if (p_i == 0)
			// p_i = (double) 1 / files.length;
			// if (p_j == 0)
			// p_j = (double) 1 / files.length;

			pmi[index] = Math.log(p_i_j / (p_i * p_j));

		}

		double topic_pmi = 0;

		for (int i = 0; i < length; i++) {
			topic_pmi += pmi[i];
		}

		return topic_pmi;
	}

	/**
	 * 算一个主题的NPMI (EACL 2014)，文档共生
	 * 
	 * @param topic
	 * @return
	 * @throws IOException
	 */
	public static double npmi(String[] topic) throws IOException {

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

		int length = word_pair_list.size();

		int[] count_1 = new int[length];

		int[] count_2 = new int[length];

		int[] count = new int[length];

		File[] files = new File("G:\\wiki_full\\").listFiles();

		// long time = System.currentTimeMillis();

		for (File f : files) {

			String content = ReadWriteFile.getTextContent(f);

			for (int index = 0; index < length; index++) {

				Set<String> pair = word_pair_list.get(index);

				List<String> two_words = new ArrayList<>(pair);

				String word_1 = two_words.get(0);

				String word_2 = two_words.get(1);

				if (content.contains(word_1) && content.contains(word_2)) {
					count[index]++;
					count_1[index]++;
					count_2[index]++;
				} else if (content.contains(word_1) && !content.contains(word_2)) {
					count_1[index]++;
				} else if (!content.contains(word_1) && content.contains(word_2)) {
					count_2[index]++;
				}

			}

		}

		// long cost = (System.currentTimeMillis() - time) / 1000;

		// System.out.println("Time cost : " + cost);

		double[] pmi = new double[length];

		for (int index = 0; index < length; index++) {

			double p_i = (double) count_1[index] / files.length;

			double p_j = (double) count_2[index] / files.length;

			double p_i_j = (double) (count[index] + 1) / files.length;

			// if (p_i_j == 0)
			// p_i_j = (double) 1 / files.length;
			// if (p_i == 0)
			// p_i = (double) 1 / files.length;
			// if (p_j == 0)
			// p_j = (double) 1 / files.length;

			pmi[index] = Math.log(p_i_j / (p_i * p_j)) / (-Math.log(p_i_j));

		}

		double topic_pmi = 0;

		for (int i = 0; i < length; i++) {
			topic_pmi += pmi[i];
		}

		return topic_pmi;
	}

	/**
	 * 算一个主题的PMI (Newman et al., 2010)，窗口共生
	 * 
	 * @param topic
	 * @return
	 * @throws IOException
	 */
	public static double pmi_window(String[] topic) throws IOException {

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

		int length = word_pair_list.size();

		int[] count_1 = new int[length];

		int[] count_2 = new int[length];

		int[] count = new int[length];

		File[] files = new File("G:\\wiki_full\\").listFiles();

		// long time = System.currentTimeMillis();

		int windows = 0;

		for (File f : files) {

			String content = ReadWriteFile.getTextContent(f);

			String[] temp = content.split("\n");

			List<String> words = new ArrayList<>();

			for (String sentence : temp) {

				String[] temp1 = sentence.split(" ");

				for (String word : temp1) {

					words.add(word);
				}

			}

			if (words.size() < 10)
				continue;

			int iter = words.size() - 10;

			windows += iter;

			for (int i = 0; i < iter; i++) {

				List<String> sub_list = words.subList(i, i + 10);

				for (int index = 0; index < length; index++) {

					Set<String> pair = word_pair_list.get(index);

					List<String> two_words = new ArrayList<>(pair);

					String word_1 = two_words.get(0);

					String word_2 = two_words.get(1);

					if (sub_list.contains(word_1) && sub_list.contains(word_2)) {
						count[index]++;
						count_1[index]++;
						count_2[index]++;
					} else if (sub_list.contains(word_1) && !sub_list.contains(word_2)) {
						count_1[index]++;
					} else if (!sub_list.contains(word_1) && sub_list.contains(word_2)) {
						count_2[index]++;
					}

				}

			}

		}

		// long cost = (System.currentTimeMillis() - time) / 1000;

		// System.out.println("Time cost : " + cost);

		double[] pmi = new double[length];

		for (int index = 0; index < length; index++) {

			double p_i = (double) count_1[index] / windows;

			double p_j = (double) count_2[index] / windows;

			double p_i_j = (double) (count[index] + 1) / windows;

			pmi[index] = Math.log(p_i_j / (p_i * p_j));

		}

		double topic_pmi = 0;

		for (int i = 0; i < length; i++) {
			topic_pmi += pmi[i];
		}

		return topic_pmi;
	}

	/**
	 * 算一个主题的NPMI (EACL 2014)，文档共生
	 * 
	 * @param topic
	 * @return
	 * @throws IOException
	 */
	public static double npmi_window(String[] topic) throws IOException {

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

		int length = word_pair_list.size();

		int[] count_1 = new int[length];

		int[] count_2 = new int[length];

		int[] count = new int[length];

		File[] files = new File("G:\\wiki_full\\").listFiles();

		// long time = System.currentTimeMillis();

		int windows = 0;

		for (File f : files) {

			String content = ReadWriteFile.getTextContent(f);

			String[] temp = content.split("\n");

			List<String> words = new ArrayList<>();

			for (String sentence : temp) {

				String[] temp1 = sentence.split(" ");

				for (String word : temp1) {

					words.add(word);
				}

			}

			if (words.size() < 10)
				continue;

			int iter = words.size() - 10;

			windows += iter;

			for (int i = 0; i < iter; i++) {

				List<String> sub_list = words.subList(i, i + 10);

				for (int index = 0; index < length; index++) {

					Set<String> pair = word_pair_list.get(index);

					List<String> two_words = new ArrayList<>(pair);

					String word_1 = two_words.get(0);

					String word_2 = two_words.get(1);

					if (sub_list.contains(word_1) && sub_list.contains(word_2)) {
						count[index]++;
						count_1[index]++;
						count_2[index]++;
					} else if (sub_list.contains(word_1) && !sub_list.contains(word_2)) {
						count_1[index]++;
					} else if (!sub_list.contains(word_1) && sub_list.contains(word_2)) {
						count_2[index]++;
					}

				}

			}

		}

		// long cost = (System.currentTimeMillis() - time) / 1000;

		// System.out.println("Time cost : " + cost);

		double[] pmi = new double[length];

		for (int index = 0; index < length; index++) {

			double p_i = (double) count_1[index] / windows;

			double p_j = (double) count_2[index] / windows;

			double p_i_j = (double) (count[index] + 1) / windows;

			pmi[index] = Math.log(p_i_j / (p_i * p_j)) / (-Math.log(p_i_j));

		}

		double topic_pmi = 0;

		for (int i = 0; i < length; i++) {
			topic_pmi += pmi[i];
		}

		return topic_pmi;
	}

	public static double main(String[] args) throws IOException, InterruptedException {

		String topic_file = "file//" + args[0] + ".txt";

		String content = ReadWriteFile.getTextContent(new File(topic_file));

		String[] lines = content.split("\n");

		List<String[]> topics = new ArrayList<>();

		for (String line : lines) {

			String[] words = line.trim().split("\t");

			if (words.length == 10) {

				topics.add(words);

				System.out.println("ten");

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

		System.out.println("主题数: " + topics.size());

		int threads = 10; // 线程数

		ExecutorService pool = Executors.newFixedThreadPool(threads); // 线程池
		for (int i = 0; i < threads; i++) {

			List<String[]> sub_list = topics.subList(i * 6, (i + 1) * 6);

			pool.submit(new Thread(new MultiThreadPMI(sub_list)));

		}
		pool.shutdown();

		while (!pool.isTerminated())
			Thread.sleep(1000);
		System.out.println(args[0]);

		ReadWriteFile.writeFile("file//" + args[0] + ".coherence", sb.toString());

		/*
		 * 求平均PMI
		 */
		String coherences = ReadWriteFile.getTextContent(new File("file//" + args[0] + ".coherence"));

		String[] coherence = coherences.split("\n");

		System.out.println(coherence.length);

		double average = 0;

		for (String score : coherence) {
			average += Double.parseDouble(score);
		}

		return average / coherence.length;
	}

}
