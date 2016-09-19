package test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.Corpus;
import util.ReadWriteFile;

public class GenerateWordCategory {

	public static void main(String[] args) throws IOException {

		List<String> vocab = Corpus.getVocab("data//vocab_ohsumed.txt");

		String sense_file_content = ReadWriteFile.getTextContent(new File("file//ohsumed_sense_word_wn18_appear.txt"));

		Map<Integer, Set<Integer>> word_categories = new HashMap<>();

		int V = vocab.size();

		for (int i = 0; i < V; i++) {
			word_categories.put(i, new HashSet<Integer>());
		}

		String[] lines = sense_file_content.split("\n");

		System.out.println(lines.length);

		for (int i = 0; i < lines.length; i++) {

			String line = lines[i];

			String[] temp = line.split("\t");

			String[] words = temp[1].split(" ");

			for (String word : words) {

				int index = vocab.indexOf(word);
				Set<Integer> categories = word_categories.get(index);

				categories.add(i);
			}

		}

		System.out.println(word_categories);

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < V; i++) {

			StringBuilder word_sb = new StringBuilder();

			word_sb.append(i + "\t");

			Set<Integer> categories = word_categories.get(i);

			for (int c : categories) {
				word_sb.append(c + " ");
			}

			sb.append(word_sb.toString().trim() + "\n");

		}

		ReadWriteFile.writeFile("file//ohsumed_word_sense_wn18_appear.txt", sb.toString());

	}
}
