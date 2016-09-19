package task;

import global.CmdOption20ng;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import model.ModelParameters;
import model.TopicModel;
import multithread.TopicModelMultiThreadPool;
import nlp.Corpus;

/**
 * The task of knowledge-based topic models on multiple domains.
 */
public class TopicModelMultiDomainRunningTask {
	private CmdOption20ng cmdOption = null;

	public TopicModelMultiDomainRunningTask(CmdOption20ng cmdOption2) {
		cmdOption = cmdOption2;
	}

	/**
	 * Read the corpus of each domain.
	 */
	private ArrayList<Corpus> getCorpora(String inputCorporeaDirectory, String suffixInputCorporeaDocs,
			String suffixInputCorporeaVocab) {
		ArrayList<Corpus> corpora = new ArrayList<Corpus>();
		File[] domainFiles = new File(inputCorporeaDirectory).listFiles();
		for (File domainFile : domainFiles) {
			if (domainFile.isDirectory()) {
				// Only consider folders.
				String domain = domainFile.getName();
				String docsFilepath = domainFile.getAbsolutePath() + File.separator + domain + suffixInputCorporeaDocs;
				String vocabFilepath = domainFile.getAbsolutePath() + File.separator + domain
						+ suffixInputCorporeaVocab;
				Corpus corpus = Corpus.getCorpusFromFile(domain, docsFilepath, vocabFilepath);
				corpora.add(corpus);
			}
		}
		return corpora;
	}

	/**
	 * For each model, create its output directory and run the model.
	 */
	public void run() {
		// Read the corpus of each domain.
		ArrayList<Corpus> corpora = getCorpora(cmdOption.inputCorporeaDirectory, cmdOption.suffixInputCorporeaDocs,
				cmdOption.suffixInputCorporeaVocab);

		run(corpora, cmdOption.nTopics, cmdOption.modelName, cmdOption.outputRootDirectory);
	}

	/**
	 * There are 2 learning iterations.
	 * 
	 * LearningIteration1: Run LDA on each domain.
	 * 
	 * LearningIteration2: Run GK-LDA on each domain using the input knowledge.
	 */
	private void run(ArrayList<Corpus> corpora, int nTopics, String modelName, String outputRootDirectory) {
		ArrayList<TopicModel> topicModelList_FirstIteration = null; // LDA
																	// models.
		ArrayList<TopicModel> topicModelList_LastIteration = null;
		ArrayList<TopicModel> topicModelList_CurrentIteration = null;

		for (int iter = 0; iter < 2; ++iter) {
			System.out.println("###################################");
			System.out.println("Learning Iteration " + iter + " Starts!");
			System.out.println("###################################");

			long startTime = System.currentTimeMillis();

			String currentIterationRootDirectory = outputRootDirectory + "LearningIteration" + iter + File.separator;
			// The first LearningIteration is LDA, the second is GK-LDA.
			String currentIterationModelName = iter == 0 ? "LDA" : modelName;

			// Run the topic model.
			System.out.println("-----------------------------------");
			System.out.println("Running Topic Model on each domain.");
			System.out.println("-----------------------------------");
			topicModelList_LastIteration = topicModelList_CurrentIteration;
			topicModelList_CurrentIteration = runTopicModelForOneLearningIteration(corpora, nTopics,
					currentIterationModelName, currentIterationRootDirectory, topicModelList_LastIteration,
					topicModelList_FirstIteration);
			if (iter == 0) {
				topicModelList_FirstIteration = topicModelList_CurrentIteration;
			}

			double timeLength = (System.currentTimeMillis() - startTime) / 1000.0;

			System.out.println("###################################");
			System.out.println("Learning Iteration " + iter + " Ends! " + timeLength + "seconds");
			System.out.println("###################################");
			System.out.println("");
		}
	}

	/**
	 * Run the topic model (LDA or GK-LDA) for one learning iteration. We use
	 * multithreading and each thread handles the model in one domain.
	 */
	private ArrayList<TopicModel> runTopicModelForOneLearningIteration(ArrayList<Corpus> corpora, int nTopics,
			String currentIterationModelName, String currentIterationRootDirectory,
			ArrayList<TopicModel> topicModelList_LastIteration, ArrayList<TopicModel> topicModelList_FirstIteration) {
		ArrayList<TopicModel> topicModelList_CurrentIteration = new ArrayList<TopicModel>();
		TopicModelMultiThreadPool threadPool = new TopicModelMultiThreadPool(cmdOption.nthreads);

		for (Corpus corpus : corpora) {
			String currentIterationModelDirectory = currentIterationRootDirectory + "DomainModels" + File.separator
					+ corpus.domain + File.separator;

			if (new File(currentIterationModelDirectory).exists()) {
				// If the model of a domain in this learning
				// iteration already exists, we load it and add it into the
				// topic model list.
				// ModelLoader modelLoader = new ModelLoader();
				// TopicModel modelForDomain = modelLoader.loadModel(
				// currentIterationModelName, corpus.domain,
				// currentIterationModelDirectory);
				// System.out.println("Loaded the model of domain "
				// + corpus.domain);
				// topicModelList_CurrentIteration.add(modelForDomain);
			} else {
				// Run the model on each domain.
				// Construct all the parameters needed to run the model.
				ModelParameters param = new ModelParameters(corpus, nTopics, cmdOption);

				param.modelName = currentIterationModelName;
				param.outputModelDirectory = currentIterationModelDirectory;
				param.topicModelList_LDA = topicModelList_FirstIteration;
				param.knowledgeFilePath = cmdOption.inputKnowledgeFilePath;

				threadPool.addTask(corpus, param);
			}
		}
		threadPool.awaitTermination();
		topicModelList_CurrentIteration.addAll(threadPool.topicModelList);
		// Sort the topic model list based on the domain name alphabetically.
		Collections.sort(topicModelList_CurrentIteration, new Comparator<TopicModel>() {
			@Override
			public int compare(TopicModel o1, TopicModel o2) {
				return o1.corpus.domain.toLowerCase().compareTo(o2.corpus.domain.toLowerCase());
			}
		});
		return topicModelList_CurrentIteration;
	}
}
