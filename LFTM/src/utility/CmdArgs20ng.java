package utility;

import org.kohsuke.args4j.Option;

public class CmdArgs20ng {

	@Option(name = "-model", usage = "Specify model", required = true)
	public String model = "LFLDA";

	@Option(name = "-corpus", usage = "Specify path to topic modeling corpus")
	public String corpus = "data/20ng.corpus";

	@Option(name = "-vectors", usage = "Specify path to the file containing word vectors")
	public String vectors = "data/20ng_vectors_50d.txt";

	@Option(name = "-ntopics", usage = "Specify number of topics")
	public int ntopics = 30;

	@Option(name = "-alpha", usage = "Specify alpha")
	public double alpha = 50.0 / ntopics;

	@Option(name = "-beta", usage = "Specify beta")
	public double beta = 0.01;

	@Option(name = "-lambda", usage = "Specify mixture weight lambda")
	public double lambda = 0.6;

	@Option(name = "-initers", usage = "Specify number of initial sampling iterations")
	public int initers = 1000;

	@Option(name = "-niters", usage = "Specify number of EM-style sampling iterations")
	public int niters = 200;

	@Option(name = "-twords", usage = "Specify number of top topical words")
	public int twords = 10;

	@Option(name = "-name", usage = "Specify a name to a topic modeling experiment")
	public String expModelName = "20ng";

	@Option(name = "-initFile")
	public String initTopicAssgns = "";

	@Option(name = "-sstep")
	public int savestep = 0;

	@Option(name = "-dir")
	public String dir = "";

	@Option(name = "-label")
	public String labelFile = "";

	@Option(name = "-prob")
	public String prob = "";

}
