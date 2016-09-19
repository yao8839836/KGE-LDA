package test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import util.Corpus;
import util.ReadWriteFile;

public class BuildCorpus {

	public static void main(String[] args) throws IOException {

		List<String> vocab = Corpus.getVocab("data//vocab_ohsumed.txt");

		String path = "file//ohsumed_remove_rare//";

		File[] files = new File(path).listFiles();

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < files.length; i++) {

			File file = new File(path + i);

			String content = ReadWriteFile.getTextContent(file);

			String[] words = content.split(" ");

			StringBuilder doc = new StringBuilder();

			for (String word : words) {

				int index = vocab.indexOf(word);

				if (index != -1)
					doc.append(index + " ");

			}

			sb.append(doc.toString().trim() + "\n");

		}

		ReadWriteFile.writeFile("data\\corpus_ohsumed.txt", sb.toString());

	}

}
