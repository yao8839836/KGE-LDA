package result20ng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import classifiers.Classifiers;
import preprocessing.BuildInstances;
import test.PMIByIndex20ng;
import topic.SwitchLDA;
import util.Common;
import util.Corpus;
import util.Evaluation;
import util.ReadWriteFile;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class RunSwitchLDA20ng {

	public static String main(String[] args) throws Exception {

		List<String> vocab = Corpus.getVocab("data//vocab_20ng.txt");

		int[][] docs = Corpus.getDocuments("data//corpus_20ng.txt");

		System.out.println(docs.length);

		File[] entity_files = new File("file//20ng_wordnet_id//").listFiles();

		int[][] entities = Corpus.getEntities(entity_files);

		List<String> entity_list = Corpus.getVocab("knowledge//WN18//entity_appear.txt");

		// 将原始id转成索引

		for (int i = 0; i < entities.length; i++) {

			for (int j = 0; j < entities[i].length; j++) {

				System.out.print(entities[i][j] + "\t");

				entities[i][j] = entity_list.indexOf(entities[i][j] + "");

				System.out.println(entities[i][j]);

			}

		}

		int V = vocab.size();

		int E = entity_list.size();

		SwitchLDA slda = new SwitchLDA(docs, entities, V, E);

		int K = 30;
		double alpha = (double) 50 / K;
		double beta = 0.01;
		double beta_bar = 0.01;
		double gamma = 0.01;
		int iterations = 1000;

		slda.markovChain(K, alpha, beta, beta_bar, gamma, iterations);

		double[][] phi = slda.estimatePhi();

		double[][] theta = slda.estimateTheta();

		double[][] phi_copy = Common.makeCopy(phi);

		StringBuilder sb = new StringBuilder();

		for (double[] phi_t : phi_copy) {

			for (int i = 0; i < 10; i++) {

				int max_index = Common.maxIndex(phi_t);

				sb.append(vocab.get(max_index) + "\t");

				phi_t[max_index] = 0;

			}
			sb.append("\n");

		}

		String filename = "file//switch_lda_20ng_topics.txt";

		// 语义一致性
		double average_coherence = Corpus.average_coherence(docs, phi_copy, 15);

		System.out.println("average coherence : " + average_coherence);

		sb.append("average coherence\t" + average_coherence);

		ReadWriteFile.writeFile(filename, sb.toString());

		args = new String[2];

		args[0] = "switch_lda_20ng_topics";

		args[1] = "data//vocab_20ng.txt";

		double pmi = PMIByIndex20ng.main(args);

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

		ReadWriteFile.writeFile("file//Switch_LDA_feature.txt", sb.toString());

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

		file = new File("file//Switch_LDA_feature.txt");

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

		ReadWriteFile.writeFile("file//switch_lda_train.arff", training_data);

		ReadWriteFile.writeFile("file//switch_lda_test.arff", test_data);

		/*
		 * 读训练集
		 */

		file = new File("file//switch_lda_train.arff");

		ArffLoader loader = new ArffLoader();
		loader.setFile(file);

		Instances train = loader.getDataSet();
		train.setClassIndex(train.numAttributes() - 1);

		/*
		 * 读测试集
		 */

		file = new File("file//switch_lda_test.arff");

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

		double accuaracy = (double) count / num_instances;

		System.out.println("Accuracy : " + accuaracy);

		double macro_f1 = Evaluation.macro_F1(classifier, test);

		System.out.println("Macro Averaged F1 : " + macro_f1);

		return pmi + "\t" + accuaracy;

		// List<String> vocab = Corpus.getVocab("data//vocab_20ng.txt");
		//
		// int[][] docs = Corpus.getDocuments("data//corpus_20ng.txt");
		//
		// StringBuilder sb = new StringBuilder();
		//
		// for (int[] doc : docs) {
		//
		// StringBuilder doc_str = new StringBuilder();
		//
		// for (int word : doc) {
		//
		// doc_str.append(vocab.get(word) + " ");
		//
		// }
		// sb.append(doc_str.toString().trim() + "\n");
		// }
		//
		// ReadWriteFile.writeFile("file//corpus_20ng_words", sb.toString());

		/*
		 * 读id,行号关联数组
		 */

		// File f = new File("knowledge//WN18//num_synset.txt");
		// BufferedReader reader = new BufferedReader(new InputStreamReader(new
		// FileInputStream(f), "UTF-8"));
		// String line = "";
		//
		// Map<String, String> synset_num = new HashMap<>();
		//
		// while ((line = reader.readLine()) != null) {
		//
		// String[] temp = line.split("\t");
		//
		// synset_num.put(temp[1], temp[0]);
		//
		// }
		//
		// reader.close();
		//
		// String content = ReadWriteFile.getTextContent(new
		// File("file//corpus_20ng_wordnet"));
		//
		// StringBuilder str_to_write = new StringBuilder();
		//
		// String[] doc_entities = content.split("\n");
		//
		// Set<String> entity_set = new HashSet<>();
		//
		// for (String entities : doc_entities) {
		//
		// String[] doc_entity = entities.trim().split(" ");
		//
		// StringBuilder doc_str = new StringBuilder();
		//
		// for (String entity : doc_entity) {
		//
		// if (!entity.equals("None")) {
		//
		// String synset = entity.substring(entity.indexOf("'") + 1,
		// entity.lastIndexOf("'"));
		//
		// // System.out.println(synset);
		//
		// String[] attributes = synset.split("\\.");
		//
		// StringBuilder sb = new StringBuilder();
		//
		// sb.append("__" + attributes[0]);
		//
		// if (attributes[1].equals("n"))
		// sb.append("_" + "NN");
		// else if (attributes[1].equals("v"))
		// sb.append("_" + "VB");
		// else if (attributes[1].equals("a"))
		// sb.append("_" + "JJ");
		//
		// if (!attributes[2].equals("") &&
		// Character.isDigit(attributes[2].charAt(0)))
		// sb.append("_" + Integer.parseInt(attributes[2]));
		//
		// String str = sb.toString();
		// if (synset_num.containsKey(str))
		// doc_str.append(synset_num.get(str) + " ");
		// else
		// doc_str.append(-1 + " ");
		//
		// entity_set.add(synset_num.get(str));
		//
		// }
		//
		// else
		// doc_str.append(-1 + " ");
		// }
		//
		// str_to_write.append(doc_str.toString().trim() + "\n");
		// }
		//
		// ReadWriteFile.writeFile("file//corpus_20ng_wordnet_id",
		// str_to_write.toString());
		//
		// System.out.println(entity_set.size());

	}
}
