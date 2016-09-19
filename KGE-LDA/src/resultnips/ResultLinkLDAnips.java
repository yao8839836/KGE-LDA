package resultnips;

import util.ReadWriteFile;

public class ResultLinkLDAnips {

	public static void main(String[] args) throws Exception {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 10; i++) {
			double pmi = RunLinkLDAnips.main(args);
			sb.append(pmi + "\n");
		}

		ReadWriteFile.writeFile("file//link_lda_nips_30.txt", sb.toString());

	}

}
