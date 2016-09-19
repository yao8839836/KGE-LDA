package test;

import java.io.File;

import classifiers.Classifiers;
import util.Evaluation;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class Predict {

	public static void main(String[] args) throws Exception {

		/*
		 * 读训练集
		 */

		File file = new File("file//train_doc2vec.arff");

		ArffLoader loader = new ArffLoader();
		loader.setFile(file);

		Instances train = loader.getDataSet();
		train.setClassIndex(train.numAttributes() - 1);

		/*
		 * 读测试集
		 */

		file = new File("file//test_doc2vec.arff");

		loader = new ArffLoader();
		loader.setFile(file);

		Instances test = loader.getDataSet();
		test.setClassIndex(train.numAttributes() - 1);

		// 训练

		Classifier classifier = Classifiers.SVM_Linear(train);

		int num_instances = test.numInstances();

		int count = 0;

		for (int j = 0; j < num_instances; j++) {

			Instance test_instance = test.instance(j);

			int real_label = (int) test_instance.classValue();

			double class_value = classifier.classifyInstance(test_instance);

			int predict_result = (int) class_value;

			if (predict_result == real_label)
				count++;

		}

		System.out.println("Accuracy : " + (double) count / num_instances);

		double macro_f1 = Evaluation.macro_F1(classifier, test);

		System.out.println("Macro Averaged F1 : " + macro_f1);

	}

}
