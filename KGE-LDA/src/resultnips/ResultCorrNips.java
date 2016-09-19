package resultnips;

import util.ReadWriteFile;

public class ResultCorrNips {

	public static void main(String[] args) throws Exception {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; i++) {

			double pmi = RunCorrLDAnips.main(args);
			sb.append(pmi + "\n");

		}

		ReadWriteFile.writeFile("file//corr_lda_nips_30.txt", sb.toString());

	}

}
