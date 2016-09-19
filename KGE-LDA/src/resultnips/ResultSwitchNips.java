package resultnips;

import util.ReadWriteFile;

public class ResultSwitchNips {

	public static void main(String[] args) throws Exception {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; i++) {

			double pmi = RunSwitchLDAnips.main(args);
			sb.append(pmi + "\n");

		}

		ReadWriteFile.writeFile("file//switch_lda_nips_30.txt", sb.toString());
	}

}
