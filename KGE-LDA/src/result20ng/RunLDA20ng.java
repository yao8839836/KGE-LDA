package result20ng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import preprocessing.BuildInstances;
import test.PMIByIndex20ng;
import topic.LDA;
import util.Common;
import util.Corpus;
import util.Evaluation;
import util.ReadWriteFile;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import classifiers.Classifiers;

public class RunLDA20ng {

	public static String main(String[] args) throws Exception {

		List<String> vocab = Corpus.getVocab("data//vocab_20ng.txt");

		int[][] docs = Corpus.getDocuments("data//corpus_20ng.txt");

		int K = 30;

		LDA lda = new LDA(docs, vocab.size());

		int iterations = 1000;

		/*
		 * 先验
		 */

		double alpha = (double) 50 / K;
		double beta = 0.01;

		lda.markovChain(K, alpha, beta, iterations);

		double[][] phi = lda.estimatePhi();

		double[][] theta = lda.estimateTheta();

		double[][] phi_copy = Common.makeCopy(phi);

		// 将每个主题的前10个词写文件
		double[][] phi_for_write = Common.makeCopy(phi);

		StringBuilder sb = new StringBuilder();

		for (double[] phi_t : phi_for_write) {

			for (int i = 0; i < 10; i++) {

				int max_index = Common.maxIndex(phi_t);

				sb.append(vocab.get(max_index) + "\t");

				phi_t[max_index] = 0;

			}
			sb.append("\n");

		}

		String filename = "file//lda_20ng_topics.txt";

		// 语义一致性
		double average_coherence = Corpus.average_coherence(docs, phi_copy, 15);

		System.out.println("average coherence : " + average_coherence);

		sb.append("average coherence\t" + average_coherence);

		ReadWriteFile.writeFile(filename, sb.toString());

		args = new String[2];

		args[0] = "lda_20ng_topics";

		args[1] = "data//vocab_20ng.txt";

		double pmi = PMIByIndex20ng.main(args);

		// double pmi = 0;

		/*
		 * 特征向量写文件
		 */
		sb = new StringBuilder();

		for (int d = 0; d < theta.length; d++) {

			StringBuilder doc = new StringBuilder();
			for (int k = 0; k < theta[d].length; k++) {
				doc.append(theta[d][k] + "\t");
			}
			sb.append(doc.toString().trim() + "\n");
		}

		ReadWriteFile.writeFile("file//LDA_feature.txt", sb.toString());

		Map<String, double[]> vector_map = new HashMap<>();

		Map<String, String> train_or_test = new HashMap<>();

		Map<String, String> doc_label = new HashMap<>();

		/*
		 * 读label, 训练或测试
		 */

		File file = new File("data//20ng_label.txt");

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

		String line = "";

		int count = 0;

		while ((line = reader.readLine()) != null) {

			String[] temp = line.split("\t");

			train_or_test.put(count + "", temp[1]);

			doc_label.put(count + "", temp[2]);

			count++;

		}

		reader.close();

		/*
		 * 读特征向量
		 */

		file = new File("file//LDA_feature.txt");

		reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

		count = 0;

		while ((line = reader.readLine()) != null) {

			String[] temp = line.split("\t");

			double[] vector = new double[temp.length];

			for (int i = 0; i < vector.length; i++) {

				vector[i] = Double.parseDouble(temp[i]);

			}

			vector_map.put(count + "", vector);

			count++;

		}

		reader.close();

		String training_data = BuildInstances.getTrainingSet(doc_label, train_or_test, vector_map, K);

		String test_data = BuildInstances.getTestSet(doc_label, train_or_test, vector_map, K);

		ReadWriteFile.writeFile("file//train_lda.arff", training_data);

		ReadWriteFile.writeFile("file//test_lda.arff", test_data);

		/*
		 * 读训练集
		 */

		file = new File("file//train_lda.arff");

		ArffLoader loader = new ArffLoader();
		loader.setFile(file);

		Instances train = loader.getDataSet();
		train.setClassIndex(train.numAttributes() - 1);

		/*
		 * 读测试集
		 */

		file = new File("file//test_lda.arff");

		loader = new ArffLoader();
		loader.setFile(file);

		Instances test = loader.getDataSet();
		test.setClassIndex(train.numAttributes() - 1);

		// 训练

		Classifier classifier = Classifiers.SVM_Linear(train);

		int num_instances = test.numInstances();

		count = 0;

		for (int j = 0; j < num_instances; j++) {

			Instance test_instance = test.instance(j);

			int real_label = (int) test_instance.classValue();

			double class_value = classifier.classifyInstance(test_instance);

			int predict_result = (int) class_value;

			if (predict_result == real_label)
				count++;

		}

		double accuracy = (double) count / num_instances;

		System.out.println("Accuracy : " + accuracy);

		double macro_f1 = Evaluation.macro_F1(classifier, test);

		System.out.println("Macro Averaged F1 : " + macro_f1);

		return pmi + "\t" + accuracy;

	}

}
