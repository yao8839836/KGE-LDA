package test;

import java.io.IOException;

import utility.ReadWriteFile;

public class ResultLFLDANips {

	public static void main(String[] args) throws IOException {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; i++) {

			LFTMnips.main(args);

			String[] args_new = new String[2];

			args_new[0] = "nips";

			args_new[1] = "data//vocab_nips.txt";

			double pmi = PMIByIndexNips.main(args_new);

			System.out.println(pmi);

			sb.append(pmi + "\n");

		}
		ReadWriteFile.writeFile("file//LFLDA_nips_30.txt", sb.toString());

	}

}
