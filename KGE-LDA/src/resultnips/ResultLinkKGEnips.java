package resultnips;

import util.ReadWriteFile;

public class ResultLinkKGEnips {

	public static void main(String[] args) throws Exception {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; i++) {

			double pmi = RunLinkLDA_KGEnips.main(args);
			sb.append(pmi + "\n");

		}

		ReadWriteFile.writeFile("file//link_kge_lda_nips_30.txt", sb.toString());

	}

}
