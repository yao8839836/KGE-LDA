package launch;

import java.io.File;
import java.io.IOException;

import utility.Common;
import utility.PMIByIndexNips;
import utility.ReadWriteFile;

public class ResultGKLDANips {

	public static void main(String[] args) throws IOException {

		StringBuilder sb = new StringBuilder();

		for (int iter = 0; iter < 10; iter++) {

			File dir = new File("../Data/Output/nips/");

			Common.deleteDir(dir);

			MainEntryNips.main(args);

			String[] args_new = new String[2];

			args_new[0] = "nips";

			args_new[1] = "file//vocab_nips.txt";

			double pmi = PMIByIndexNips.main(args_new);

			sb.append(pmi + "\n");
		}

		ReadWriteFile.writeFile("file//gklda_nips_30.txt", sb.toString());

	}

}
