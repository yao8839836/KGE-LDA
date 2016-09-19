package resultohsumed;

import util.ReadWriteFile;

public class ResultSwitch_KGEohsumed {

	public static void main(String[] args) throws Exception {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; i++) {

			String result = RunSwitchLDA_KGEohsumed.main(args);
			sb.append(result + "\n");

		}

		ReadWriteFile.writeFile("file//switch_kge_lda_ohsumed_30.txt", sb.toString());
	}

}
