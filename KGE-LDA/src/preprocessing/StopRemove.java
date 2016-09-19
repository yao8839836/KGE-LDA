package preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.ReadWriteFile;

public class StopRemove {

	/**
	 * 将文本去除停用词，保存文件、返回词计数器
	 * 
	 * @param files
	 *            分完词的文件列表
	 * @param stop_words
	 *            停止词列表
	 * @param path
	 *            存放路径
	 * @return
	 * @throws IOException
	 */

	public static Map<String, Integer> removeStopWords(File[] files, Set<String> stop_words, String path)
			throws IOException {

		Map<String, Integer> word_count = new HashMap<>();

		for (File f : files) {

			String line = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));

			StringBuilder sb = new StringBuilder();

			while ((line = reader.readLine()) != null) {

				String[] words = line.split(" ");

				for (String token : words) {

					if (!stop_words.contains(token)) {

						sb.append(token + " ");

						if (word_count.containsKey(token))
							word_count.put(token, word_count.get(token) + 1);
						else
							word_count.put(token, 1);

					}

				}
			}
			reader.close();

			String filename = f.getName();

			ReadWriteFile.writeFile(path + filename, sb.toString().trim());

		}

		return word_count;

	}

	/**
	 * 去除文本中的罕见词，得到词表
	 * 
	 * @param files
	 *            去除停用词后的文件列表
	 * @param word_count
	 *            词计数器
	 * @param threshold
	 *            频次阈值
	 * @param path
	 *            存放路径
	 * @param word_set
	 *            词的选择范围（维基中的词）
	 * @return
	 * @throws IOException
	 */

	public static List<String> removeRareWords(File[] files, Map<String, Integer> word_count, int threshold,
			String path, Set<String> word_set) throws IOException {

		/*
		 * 写文件
		 */

		for (File f : files) {

			String line = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));

			StringBuilder sb = new StringBuilder();

			while ((line = reader.readLine()) != null) {

				String[] words = line.split(" ");

				for (String token : words) {

					if (word_count.get(token) >= threshold && word_set.contains(token))
						sb.append(token + " ");

				}

			}

			reader.close();

			String filename = f.getName();

			ReadWriteFile.writeFile(path + filename, sb.toString().trim());

		}
		/*
		 * 得到语料库的词表
		 */
		List<String> vocab = new ArrayList<String>();

		for (String word : word_count.keySet()) {

			if (word_count.get(word) >= threshold && word_set.contains(word))
				vocab.add(word);

		}

		return vocab;
	}

	/**
	 * Remove outer punctuation from a String bat-man. --> bat-man
	 * 
	 * @param s
	 * @return
	 */
	public static String outerDepunc(String s) {
		int L = s.length();

		// Remove chars off the front
		int firstChar = 0;
		while (firstChar < L && !Character.isLetterOrDigit(s.charAt(firstChar)))
			firstChar++;

		// Remove chars off the back
		int lastChar = L - 1;
		while (lastChar >= 0 && !Character.isLetterOrDigit(s.charAt(lastChar)))
			lastChar--;

		if (firstChar >= lastChar)
			return "";
		else
			return s.substring(firstChar, lastChar + 1);
	}

}
