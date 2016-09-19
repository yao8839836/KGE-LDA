package resultnips;

import util.ReadWriteFile;

public class ResultSwitchENips {

	public static void main(String[] args) throws Exception {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; i++) {

			double pmi = RunSwitchLDA_KGEnips.main(args);
			sb.append(pmi + "\n");

		}

		ReadWriteFile.writeFile("file//switch_e_lda_nips_30.txt", sb.toString());
	}
}
