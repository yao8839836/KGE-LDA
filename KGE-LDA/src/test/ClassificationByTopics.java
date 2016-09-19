package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import preprocessing.BuildInstances;
import util.ReadWriteFile;

public class ClassificationByTopics {

	public static void main(String[] args) throws IOException {

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

		file = new File("file//clinical_doc_200_20ng.vec");

		reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

		count = 0;

		while ((line = reader.readLine()) != null) {

			String[] temp = line.split(" ");

			double[] vector = new double[temp.length];

			for (int i = 0; i < vector.length; i++) {

				vector[i] = Double.parseDouble(temp[i]);

			}

			vector_map.put(count + "", vector);

			count++;

		}

		reader.close();

		int V = 200;

		String training_data = BuildInstances.getTrainingSet(doc_label, train_or_test, vector_map, V);

		String test_data = BuildInstances.getTestSet(doc_label, train_or_test, vector_map, V);

		ReadWriteFile.writeFile("file//train_doc2vec.arff", training_data);

		ReadWriteFile.writeFile("file//test_doc2vec.arff", test_data);

	}

}
