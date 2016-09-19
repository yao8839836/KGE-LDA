package resultohsumed;

import util.ReadWriteFile;

public class ResultCorrKGEohsumed {

	public static void main(String[] args) throws Exception {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; i++) {

			String result = RunCorrKGEohsumed.main(args);
			sb.append(result + "\n");

		}

		ReadWriteFile.writeFile("file//corr_kge_lda_ohsumed_30.txt", sb.toString());
	}

}
