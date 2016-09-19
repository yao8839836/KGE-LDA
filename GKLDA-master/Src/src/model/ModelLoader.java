package model;

import java.io.File;
import java.util.ArrayList;

import nlp.Corpus;
import utility.ExceptionUtility;
import utility.FileReaderAndWriter;

public class ModelLoader {
	/**
	 * Load the topic model from the modelDirectory. For different model names,
	 * we may need to assign different variables.
	 */
	public TopicModel loadModel(String modelName, String domain,
			String modelDirectory) {
		TopicModel model = null;
		try {
			// Load the information for general topic models.
			ModelParameters param = loadModelParameters(domain, modelDirectory);
			Corpus corpus = loadCorpus(domain, modelDirectory);

			// Load posterior distribution.
			double[][] twdist = loadTwoDimentionalDistribution(modelDirectory
					+ File.separator + domain + ModelPrinter.topicWordDistSuff);

			int[][] z = loadTopicWordAssignment(domain, modelDirectory);
			if (modelName.equals("LDA")) {
				return new LDA(corpus, param, z, twdist);
			} else if (modelName.equals("GKLDA")) {
				return new GKLDA(corpus, param, z, twdist);
			} else {
				ExceptionUtility
						.throwAndCatchException("The model name is not recognizable!");
			}
		} catch (Exception ex) {
			System.out.println("Error while loading the topic model: "
					+ ex.getMessage());
			ex.printStackTrace();
		}
		return model;
	}

	public ModelParameters loadModelParameters(String domain,
			String modelDirectory) {
		String filepath = modelDirectory + domain
				+ ModelPrinter.modelParamSuffix;
		return ModelParameters.getModelParameters(filepath);
	}

	public int[][] loadTopicWordAssignment(String domain, String modelDirectory) {
		String filepath = modelDirectory + domain + ModelPrinter.tassignSuffix;
		ArrayList<String> lines = FileReaderAndWriter
				.readFileAllLines(filepath);

		int D = lines.size();
		int[][] z = new int[D][];
		for (int d = 0; d < D; ++d) {
			String line = lines.get(d);
			// Read the word with its topic.
			String[] wordsWithTopics = line.split("[ \t\r\n]");
			int N = wordsWithTopics.length;
			z[d] = new int[N];
			for (int n = 0; n < N; ++n) {
				String wordWithTopic = wordsWithTopics[n];
				String[] strSplits = wordWithTopic.split(":");
				if (strSplits.length != 2) {
					ExceptionUtility
							.throwAndCatchException("Incorrect format of word with topic!");
				}
				// String wordStr = strSplits[0];
				int topic = Integer.parseInt(strSplits[1]);
				z[d][n] = topic;
			}
		}
		return z;
	}

	public double[][] loadTwoDimentionalDistribution(String filepath) {
		ArrayList<String> lines = FileReaderAndWriter
				.readFileAllLines(filepath);

		int D1 = lines.size();
		double[][] twdist = new double[D1][];
		for (int d1 = 0; d1 < D1; ++d1) {
			String line = lines.get(d1);
			String[] strSplits = line.split("[ \t\r\n]");
			int D2 = strSplits.length;
			twdist[d1] = new double[D2];
			for (int d2 = 0; d2 < D2; ++d2) {
				twdist[d1][d2] = Double.parseDouble(strSplits[d2]);
			}
		}
		return twdist;
	}

	public Corpus loadCorpus(String domain, String modelDirectory) {
		String docsFilepath = modelDirectory + domain + ModelPrinter.docsSuffix;
		String vocabFilepath = modelDirectory + domain
				+ ModelPrinter.vocabSuffix;
		return Corpus.getCorpusFromFile(domain, docsFilepath, vocabFilepath);
	}
}
