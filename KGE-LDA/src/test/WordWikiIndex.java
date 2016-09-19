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

public class WordWikiIndex {

	public static void main(String[] args) throws IOException {

		List<String> vocab = Corpus.getVocab("data//vocab_nips.txt");

		File[] files = new File("G:\\wiki_full\\").listFiles();

		Map<String, Set<String>> word_wikis = new HashMap<>();

		for (String word : vocab) {
			word_wikis.put(word, new HashSet<String>());
		}

		int count = 0;

		for (File file : files) {

			if (count >= 200000)
				break;

			String content = ReadWriteFile.getTextContent(file);

			for (String word : vocab) {

				if (content.contains(word)) {

					Set<String> wikis = word_wikis.get(word);
					wikis.add(file.getName());

				}
			}
			System.out.println(count);
			count++;
		}

		StringBuilder sb = new StringBuilder();

		for (String word : vocab) {

			StringBuilder word_sb = new StringBuilder();
			Set<String> wikis = word_wikis.get(word);

			for (String wiki : wikis) {
				word_sb.append(wiki + " ");
			}

			String str = word_sb.toString().trim();
			sb.append(str + "\n");

			ReadWriteFile.writeFile("file//nips_word_wiki//" + word + "_" + System.currentTimeMillis() + ".txt", str);

		}

		ReadWriteFile.writeFile("file//nips_word_wikis" + "_" + System.currentTimeMillis() + ".txt", sb.toString());

	}
}
