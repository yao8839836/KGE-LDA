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

public class GenerateMustLinks {

	public static void main(String[] args) throws IOException {

		List<String> vocab = Corpus.getVocab("data//vocab_ohsumed.txt");

		Map<String, Set<String>> word_sense = new HashMap<>();

		for (String word : vocab) {

			word_sense.put(word, new HashSet<String>());
		}

		Set<String> sense_set = new HashSet<>();

		File[] texts = new File("file//ohsumed//").listFiles();

		for (File file : texts) {

			String words = ReadWriteFile.getTextContent(file);
			String[] word = words.split(" ");

			List<String> word_list = new ArrayList<>();

			for (String token : word) {

				if (!token.equals(""))
					word_list.add(token);
			}

			String entities = ReadWriteFile.getTextContent(new File("file//ohsumed_wordnet//" + file.getName()));

			String[] entity = entities.split("\n");

			System.out.println(word_list.size() + "\t" + entity.length);

			if (word_list.size() != entity.length)
				continue;

			for (int i = 0; i < entity.length; i++) {

				String word_token = word_list.get(i);

				sense_set.add(entity[i]);

				if (vocab.contains(word_token)) {

					Set<String> sense = word_sense.get(word_token);

					sense.add(entity[i]);
				}
			}

		}

		Map<String, Set<String>> sense_word = new HashMap<>();
		sense_set.remove("None");

		for (String sense : sense_set) {
			sense_word.put(sense, new HashSet<String>());
		}

		for (String word : vocab) {

			Set<String> sen_set = word_sense.get(word);

			for (String sense : sen_set) {

				Set<String> words_of_sense = sense_word.get(sense);

				if (words_of_sense != null)

					words_of_sense.add(word);

			}

		}

		StringBuilder sb = new StringBuilder();

		for (String sense : sense_set) {

			StringBuilder sense_sb = new StringBuilder(sense + "\t");

			Set<String> words_of_sense = sense_word.get(sense);

			for (String word : words_of_sense) {

				sense_sb.append(word + " ");
			}
			sb.append(sense_sb.toString().trim() + "\n");

		}

		ReadWriteFile.writeFile("file//ohsumed_sense_word.txt", sb.toString());

		// for (String word : vocab) {
		//
		// StringBuilder word_sb = new StringBuilder();
		//
		// word_sb.append(word + "\t");
		// Set<String> sen_set = word_sense.get(word);
		//
		// for (String sense : sen_set) {
		//
		// word_sb.append(sense + " ");
		// }
		//
		// sb.append(word_sb.toString().trim() + "\n");
		//
		// }
		//
		// ReadWriteFile.writeFile("file//nips_word_sense.txt", sb.toString());

	}

}
