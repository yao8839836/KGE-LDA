package multithread;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import nlp.Corpus;
import model.ModelParameters;
import model.TopicModel;

/**
 * This implements multithreading pool which is able to return a list of topic
 * models running in this learning iteration.
 */
public class TopicModelMultiThreadPool {
	private int numberOfThreads = 1;
	private ExecutorService executor = null;
	ArrayList<Future<TopicModel>> futureList = new ArrayList<Future<TopicModel>>();
	// All topic models that run in this learning iteration.
	public ArrayList<TopicModel> topicModelList = null;

	public TopicModelMultiThreadPool(int numberOfThreads2) {
		numberOfThreads = numberOfThreads2;
		executor = Executors.newFixedThreadPool(numberOfThreads);
		topicModelList = new ArrayList<TopicModel>();
	}

	public void addTask(Corpus corpus, ModelParameters param) {
		try {
			Callable<TopicModel> callable = new TopicModelCallable(corpus,
					param);
			Future<TopicModel> future = executor.submit(callable);
			futureList.add(future);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void awaitTermination() {
		try {
			executor.shutdown();
			executor.awaitTermination(60, TimeUnit.DAYS);

			// Get all the topic models. Note that they are sorted according to
			// the domain name alphabetically.
			for (Future<TopicModel> future : futureList) {
				TopicModel topicModel = future.get();
				topicModelList.add(topicModel);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
