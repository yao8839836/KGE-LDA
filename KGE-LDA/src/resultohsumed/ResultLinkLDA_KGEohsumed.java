package resultohsumed;

import util.ReadWriteFile;

public class ResultLinkLDA_KGEohsumed {

	public static void main(String[] args) throws Exception {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; i++) {

			String result = RunLinkLDA_KGEohsumed.main(args);
			sb.append(result + "\n");

		}

		ReadWriteFile.writeFile("file//link_kge_lda_ohsumed_30.txt", sb.toString());
	}

}
