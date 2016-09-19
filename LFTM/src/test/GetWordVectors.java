package test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utility.Corpus;
import utility.ReadWriteFile;

public class GetWordVectors {

	public static void main(String[] args) throws IOException {

		List<String> vocab = Corpus.getVocab("data//vocab_ohsumed.txt");

		Set<String> word_set = new HashSet<>();

		for (String word : vocab) {
			word_set.add(word);
		}

		String content = ReadWriteFile.getTextContent(new File("data//wiki_vector_50d.txt"));

		StringBuilder sb = new StringBuilder();

		String[] lines = content.split("\n");

		for (String line : lines) {

			String[] temp = line.split(" ");

			if (word_set.contains(temp[0]))
				sb.append(line + "\n");

		}

		ReadWriteFile.writeFile("data//ohsumed_vectors_50d.txt", sb.toString());

	}
}
