package test;

import models.LFDMM;
import models.LFLDA;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import utility.CmdArgsOhsumed;
import eval.ClusteringEval;

public class LFTMohsumed {

	public static void main(String[] args) {

		CmdArgsOhsumed cmdArgs = new CmdArgsOhsumed();
		CmdLineParser parser = new CmdLineParser(cmdArgs);
		try {

			// parser.parseArgument(args);

			if (cmdArgs.model.equals("LFLDA")) {
				LFLDA lflda = new LFLDA(cmdArgs.corpus, cmdArgs.vectors, cmdArgs.ntopics, cmdArgs.alpha, cmdArgs.beta,
						cmdArgs.lambda, cmdArgs.initers, cmdArgs.niters, cmdArgs.twords, cmdArgs.expModelName,
						cmdArgs.initTopicAssgns, cmdArgs.savestep);
				lflda.inference();
			} else if (cmdArgs.model.equals("LFDMM")) {
				LFDMM lfdmm = new LFDMM(cmdArgs.corpus, cmdArgs.vectors, cmdArgs.ntopics, cmdArgs.alpha, cmdArgs.beta,
						cmdArgs.lambda, cmdArgs.initers, cmdArgs.niters, cmdArgs.twords, cmdArgs.expModelName,
						cmdArgs.initTopicAssgns, cmdArgs.savestep);
				lfdmm.inference();
			} else if (cmdArgs.model.equals("Eval")) {
				ClusteringEval.evaluate(cmdArgs.labelFile, cmdArgs.dir, cmdArgs.prob);
			} else {
				System.out.println("Error: Option \"-model\" must get \"LFLDA\" or \"LFDMM\" or \"Eval\"");
				System.out.println("\tLFLDA: Specify the LF-LDA topic model");
				System.out.println("\tLFDMM: Specify the LF-DMM topic model");
				System.out.println("\tEval: Specify the document clustering evaluation");
				help(parser);
				return;
			}
		} catch (CmdLineException cle) {
			System.out.println("Error: " + cle.getMessage());
			help(parser);
			return;
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
			return;
		}
	}

	public static void help(CmdLineParser parser) {
		System.out.println("java -jar LFTM.jar [options ...] [arguments...]");
		parser.printUsage(System.out);
	}

}
