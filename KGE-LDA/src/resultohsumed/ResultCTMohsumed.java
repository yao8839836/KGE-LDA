package resultohsumed;

import util.ReadWriteFile;

public class ResultCTMohsumed {

	public static void main(String[] args) throws Exception {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; i++) {

			String result = RunCTMohsumed.main(args);
			sb.append(result + "\n");

		}

		ReadWriteFile.writeFile("file//ctm_ohsumed_30.txt", sb.toString());
	}

}
