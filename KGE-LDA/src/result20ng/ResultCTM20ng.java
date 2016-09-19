package result20ng;

import util.ReadWriteFile;

public class ResultCTM20ng {

	public static void main(String[] args) throws Exception {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; i++) {

			String result = RunCTM20ng.main(args);
			sb.append(result + "\n");

		}

		ReadWriteFile.writeFile("file//ctm_20ng_30.txt", sb.toString());

	}

}
