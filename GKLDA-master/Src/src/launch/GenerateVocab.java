package launch;

import java.io.IOException;
import java.util.List;

import utility.Corpus;
import utility.ReadWriteFile;

public class GenerateVocab {

	public static void main(String[] args) throws IOException {

		List<String> vocab = Corpus.getVocab("../Data/Input/ohsumed/ohsumed/vocab_ohsumed.txt");

		System.out.println(vocab);

		int V = vocab.size();

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < V; i++) {
			sb.append(i + ":" + vocab.get(i) + "\n");
		}

		ReadWriteFile.writeFile("../Data/Input/ohsumed/ohsumed/ohsumed.vocab", sb.toString());
	}

}
