package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import util.ReadWriteFile;

public class GetWNEntities {

	public static void main(String[] args) throws IOException {

		/*
		 * 读id,行号关联数组
		 */

		File f = new File("knowledge//WN18//entity2id.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
		String line = "";

		Map<String, String> id_num = new HashMap<>();

		while ((line = reader.readLine()) != null) {

			String[] temp = line.split("\t");

			id_num.put(temp[0], temp[1]);

			System.out.println(line);

		}

		reader.close();

		/*
		 * 读id,Synset关联数组
		 */

		f = new File("knowledge//WN18//wordnet-mlj12-definitions.txt");
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
		line = "";

		Map<String, String> id_synset = new HashMap<>();

		while ((line = reader.readLine()) != null) {

			String[] temp = line.split("\t");

			id_synset.put(temp[0], temp[1]);

		}

		reader.close();

		/*
		 * 获取行号，Synset关联
		 */

		StringBuilder sb = new StringBuilder();

		for (String id : id_num.keySet()) {

			sb.append(id_num.get(id) + "\t" + id_synset.get(id) + "\n");
		}

		ReadWriteFile.writeFile("knowledge//WN18//num_synset.txt", sb.toString());

	}

}
