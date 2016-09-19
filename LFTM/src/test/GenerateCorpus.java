package test;

import java.io.IOException;
import java.util.List;

import utility.Corpus;
import utility.ReadWriteFile;

public class GenerateCorpus {

	public static void main(String[] args) throws IOException {

		List<String> vocab = Corpus.getVocab("data//vocab_ohsumed.txt");

		int[][] docs = Corpus.getDocuments("data//corpus_ohsumed.txt");

		StringBuilder sb = new StringBuilder();

		for (int[] doc : docs) {

			StringBuilder doc_str = new StringBuilder();

			for (int word : doc) {

				doc_str.append(vocab.get(word) + " ");

			}
			sb.append(doc_str.toString().trim() + "\n");

		}

		ReadWriteFile.writeFile("data//ohsumed.corpus", sb.toString());

	}

}
