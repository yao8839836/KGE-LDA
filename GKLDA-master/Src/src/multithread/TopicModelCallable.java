package multithread;

import java.util.concurrent.Callable;

import nlp.Corpus;
import model.ModelParameters;
import model.ModelPrinter;
import model.TopicModel;

public class TopicModelCallable implements Callable<TopicModel> {
	private ModelParameters param = null;
	private Corpus corpus = null;

	public TopicModelCallable(Corpus corpus2, ModelParameters param2) {
		corpus = corpus2;
		param = param2;
	}

	@Override
	/**
	 * Run the topic model in a domain and print it into the disk.
	 */
	public TopicModel call() throws Exception {
		System.out.println("\"" + param.domain + "\" <" + param.modelName
				+ "> Starts...");

		TopicModel model = TopicModel.selectModel(corpus, param);
		model.run();

		ModelPrinter modelPrinter = new ModelPrinter(model);
		modelPrinter.printModel(param.outputModelDirectory);

		System.out.println("\"" + param.domain + "\" <" + param.modelName
				+ "> Ends!");

		return model;
	}
}
