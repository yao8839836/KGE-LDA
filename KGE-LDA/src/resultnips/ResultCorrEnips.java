package resultnips;

import util.ReadWriteFile;

public class ResultCorrEnips {

	public static void main(String[] args) throws Exception {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; i++) {

			double pmi = RunCorrLDA_KGEnips.main(args);
			sb.append(pmi + "\n");

		}

		ReadWriteFile.writeFile("file//corr_e_lda_nips_30.txt", sb.toString());

	}

}
