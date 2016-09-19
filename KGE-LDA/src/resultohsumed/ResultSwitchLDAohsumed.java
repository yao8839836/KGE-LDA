package resultohsumed;

import util.ReadWriteFile;

public class ResultSwitchLDAohsumed {

	public static void main(String[] args) throws Exception {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; i++) {

			String result = RunSwitchLDAohsumed.main(args);
			sb.append(result + "\n");

		}

		ReadWriteFile.writeFile("file//switch_lda_ohsumed_30.txt", sb.toString());
	}

}
