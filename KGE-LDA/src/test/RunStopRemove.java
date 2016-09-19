package test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import preprocessing.StopRemove;
import util.ReadWriteFile;

public class RunStopRemove {

	public static void main(String[] args) throws IOException {

		String filename = "file//english.stop";

		Set<String> stop_words = ReadWriteFile.getWordSet(filename);

		// stop_words = new HashSet<>();

		File[] files = new File("file//ohsumed//").listFiles();

		String remove_stop = "file//ohsumed_remove_stop//";

		Map<String, Integer> word_count = StopRemove.removeStopWords(files, stop_words, remove_stop);

		files = new File(remove_stop).listFiles();

		String remove_rare = "file//ohsumed_remove_rare//";

		Set<String> wiki_words = ReadWriteFile.getWordSet("data/wiki_vector_word.txt");

		List<String> vocab = StopRemove.removeRareWords(files, word_count, 10, remove_rare, wiki_words);

		StringBuilder sb = new StringBuilder();

		for (String word : vocab) {
			sb.append(word + "\n");
		}

		ReadWriteFile.writeFile("data//vocab_ohsumed.txt", sb.toString().trim());

	}

}
