package resultnips;

import util.ReadWriteFile;

public class ResultCTMNips {

	public static void main(String[] args) throws Exception {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; i++) {

			double pmi = RunCTMnips.main(args);
			sb.append(pmi + "\n");

		}

		ReadWriteFile.writeFile("file//ctm_nips_30.txt", sb.toString());

	}

}
