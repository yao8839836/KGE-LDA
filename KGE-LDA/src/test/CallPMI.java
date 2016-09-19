package test;

import java.io.IOException;

public class CallPMI {

	public static void main(String[] args) throws IOException {

		args = new String[2];

		args[0] = "ctm_nips_topics";

		args[1] = "data//vocab_nips.txt";

		double pmi = PMIByIndexNips.main(args);

		System.out.println(pmi);

	}

}
