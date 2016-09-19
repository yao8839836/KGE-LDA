package global;

import org.kohsuke.args4j.Option;

/**
 * Command line options.
 * 
 * The program runs in parallel, which is controlled by the number of threads
 * (nthreads as below).
 */
public class CmdOption {

	// ------------------------------------------------------------------------
	// Input and Output
	// ------------------------------------------------------------------------

	@Option(name = "-i", usage = "Specify the input directory of each domain "
			+ "where each domain contains documents and vocabulary")
	public String inputCorporeaDirectory = "../Data/Input/Dataset/";
	
	@Option(name = "-know", usage = "Specify the file path of knowledge")
	public String inputKnowledgeFilePath = "../Data/Input/Knowledge/Knowledge_DomainIndependent.txt";

	@Option(name = "-o", usage = "Specify the output root directory of the program")
	public String outputRootDirectory = "../Data/Output/";

	@Option(name = "-sdocs", usage = "Specify the suffix of input docs file")
	public String suffixInputCorporeaDocs = ".docs";

	@Option(name = "-svocab", usage = "Specify the suffix of input vocab file")
	public String suffixInputCorporeaVocab = ".vocab";

	@Option(name = "-nthreads", usage = "Specify the number of maximum threads in multithreading")
	public int nthreads = 1;

	// ------------------------------------------------------------------------
	// General Settings for Topic Model
	// ------------------------------------------------------------------------

	@Option(name = "-ntopics", usage = "Specify the number of topics")
	public int nTopics = 15;

	@Option(name = "-burnin", usage = "Specify the number of iterations for burn-in period")
	public int nBurnin = 200;

	@Option(name = "-niters", usage = "Specify the number of Gibbs sampling iterations")
	public int nIterations = 2000;

	@Option(name = "-slag", usage = "Specify the length of interval to sample for "
			+ "calculating posterior distribution")
	public int sampleLag = 20; // -1 means only sample the last one.

	@Option(name = "-mname", usage = "Specify the name of the topic model")
	public String modelName = "GKLDA";

	/******************* Hyperparameters *********************/
	@Option(name = "-alpha", usage = "Specify the hyperparamter alpha")
	public double alpha = 1.0;

	@Option(name = "-beta", usage = "Specify the hyperparamter beta")
	public double beta = 0.1;

	@Option(name = "-rseed", usage = "Specify the seed for random number generator")
	public int randomSeed = 837191;

	/******************* Output *********************/
	@Option(name = "-twords", usage = "Specify the number of top words for each topic")
	public int twords = 20; // Print out top words ranked by probabilities per
							// each topic. -1: print out all words under topic.

	public CmdOption getSoftCopy() {
		CmdOption cmdOption2 = new CmdOption();
		cmdOption2.inputCorporeaDirectory = this.inputCorporeaDirectory;
		cmdOption2.outputRootDirectory = this.outputRootDirectory;
		cmdOption2.suffixInputCorporeaDocs = this.suffixInputCorporeaDocs;
		cmdOption2.suffixInputCorporeaVocab = this.suffixInputCorporeaVocab;
		cmdOption2.nthreads = this.nthreads;
		cmdOption2.nTopics = this.nTopics;
		cmdOption2.nBurnin = this.nBurnin;
		cmdOption2.nIterations = this.nIterations;
		cmdOption2.sampleLag = this.sampleLag;
		cmdOption2.modelName = this.modelName;
		cmdOption2.alpha = this.alpha;
		cmdOption2.beta = this.beta;
		cmdOption2.randomSeed = this.randomSeed;
		cmdOption2.twords = this.twords;
		return cmdOption2;
	}
}
