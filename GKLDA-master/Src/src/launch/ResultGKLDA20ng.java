package launch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import preprocessing.BuildInstances;
import utility.Common;
import utility.PMIByIndex20ng;
import utility.ReadWriteFile;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import classifiers.Classifiers;

public class ResultGKLDA20ng {

	public static void main(String[] args) throws Exception {

		StringBuilder sb = new StringBuilder();

		for (int iter = 0; iter < 10; iter++) {

			File dir = new File("../Data/Output/20ng/");

			Common.deleteDir(dir);

			MainEntry20ng.main(args);

			String[] args_new = new String[2];

			args_new[0] = "20ng";

			args_new[1] = "file//vocab_20ng.txt";

			double pmi = PMIByIndex20ng.main(args_new);

			Map<String, double[]> vector_map = new HashMap<>();

			Map<String, String> train_or_test = new HashMap<>();

			Map<String, String> doc_label = new HashMap<>();

			/*
			 * 读label, 训练或测试
			 */

			File file = new File("file//20ng_label.txt");

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

			file = new File("..//Data//Output//20ng//LearningIteration1//DomainModels//20ng//20ng.dtdist");

			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

			count = 0;

			int K = 0;
			while ((line = reader.readLine()) != null) {

				String[] temp = line.split(" ");

				K = temp.length;
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

			ReadWriteFile.writeFile("file//train_gklda.arff", training_data);

			ReadWriteFile.writeFile("file//test_gklda.arff", test_data);

			/*
			 * 读训练集
			 */

			file = new File("file//train_gklda.arff");

			ArffLoader loader = new ArffLoader();
			loader.setFile(file);

			Instances train = loader.getDataSet();
			train.setClassIndex(train.numAttributes() - 1);

			/*
			 * 读测试集
			 */

			file = new File("file//test_gklda.arff");

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

			System.out.println(pmi);

			System.out.println("Accuracy : " + accuracy);

			sb.append(pmi + "\t" + accuracy + "\n");

		}

		ReadWriteFile.writeFile("file//gklda_20ng_30.txt", sb.toString());

	}

}
