/***********************************************
 * 
 * This is the implementation of the conference paper Chen et al., CIKM 2013.
 * 
 * If you use this program, please cite the paper below:
 * 
 * Zhiyuan Chen, Arjun Mukherjee, Bing Liu, Meichun Hsu, Malu Castellanos, and Riddhiman Ghosh.
 * Discovering Coherent Topics Using General Knowledge. In Proceedings of CIKM 2013, pages 209-218.
 * 
 * The latest version of this program can be found in: https://github.com/czyuan/GKLDA.git.
 * 
 * @author Zhiyuan (Brett) Chen
 * @email czyuanacm@gmail.com
 * 
 **********************************************/
package launch;

import java.io.File;

import global.CmdOption20ng;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import task.TopicModelMultiDomainRunningTask;

/**
 * The main entry of the program.
 */
public class MainEntry20ng {
	public static void main(String[] args) {

		CmdOption20ng cmdOption = new CmdOption20ng();

		cmdOption.inputCorporeaDirectory = "../Data/Input/20ng";

		cmdOption.inputKnowledgeFilePath = "../Data/Input/Knowledge/20ng_must.txt";

		cmdOption.outputRootDirectory = "../Data/Output/20ng/";

		cmdOption.nTopics = 30;

		cmdOption.nBurnin = 0;

		CmdLineParser parser = new CmdLineParser(cmdOption);

		try {
			long startTime = System.currentTimeMillis();
			System.out.println("Program Starts.");

			// Parse the arguments.
			parser.parseArgument(args);

			// Check if the input directory is valid.
			if (new File(cmdOption.inputCorporeaDirectory).listFiles() == null) {
				System.err.println("Input directory is not correct, program exits!");
				return;
			}

			// Run the proposed method on the multiple domains.
			TopicModelMultiDomainRunningTask task = new TopicModelMultiDomainRunningTask(cmdOption);
			task.run();

			System.out.println("Program Ends.");
			long endTime = System.currentTimeMillis();
			showRunningTime(endTime - startTime);
		} catch (CmdLineException cle) {
			System.out.println("Command line error: " + cle.getMessage());
			showCommandLineHelp(parser);
			return;
		} catch (Exception e) {
			System.out.println("Error in program: " + e.getMessage());
			e.printStackTrace();
			return;
		}
	}

	private static void showCommandLineHelp(CmdLineParser parser) {
		System.out.println("java [options ...] [arguments...]");
		parser.printUsage(System.out);
	}

	private static void showRunningTime(long time) {
		System.out.println("Elapsed time: " + String.format("%.3f", (time) / 1000.0) + " seconds");
		System.out.println("Elapsed time: " + String.format("%.3f", (time) / 1000.0 / 60.0) + " minutes");
		System.out.println("Elapsed time: " + String.format("%.3f", (time) / 1000.0 / 3600.0) + " hours");
	}
}
