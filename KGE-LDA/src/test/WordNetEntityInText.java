package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import util.ReadWriteFile;

public class WordNetEntityInText {

	public static void main(String[] args) throws IOException {

		/*
		 * 读id,行号关联数组
		 */

		File f = new File("knowledge//WN18//num_synset.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
		String line = "";

		Map<String, String> synset_num = new HashMap<>();

		while ((line = reader.readLine()) != null) {

			String[] temp = line.split("\t");

			synset_num.put(temp[1], temp[0]);

		}

		reader.close();

		int count = 0;

		for (String synset : synset_num.keySet()) {

			if (synset.contains("VB") || synset.contains("NN") || synset.contains("JJ"))

				count++;

			else
				System.out.println(synset);

		}

		System.out.println(count);

		/*
		 * 遍历每个NLTK生成的文件
		 */

		File[] files = new File("file//ohsumed_wordnet//").listFiles();

		for (File file : files) {

			String content = ReadWriteFile.getTextContent(file);

			String[] temp = content.split("\n");

			StringBuilder str_to_write = new StringBuilder();

			for (String entity : temp) {

				if (!entity.equals("None")) {

					if (entity.equals(""))
						continue;

					String synset = entity.substring(entity.indexOf("'") + 1, entity.lastIndexOf("'"));

					// System.out.println(synset);

					String[] attributes = synset.split("\\.");

					StringBuilder sb = new StringBuilder();

					sb.append("__" + attributes[0]);

					if (attributes[1].equals("n"))
						sb.append("_" + "NN");
					else if (attributes[1].equals("v"))
						sb.append("_" + "VB");
					else if (attributes[1].equals("a"))
						sb.append("_" + "JJ");

					if (!attributes[2].equals("") && Character.isDigit(attributes[2].charAt(0)))
						sb.append("_" + Integer.parseInt(attributes[2]));

					String str = sb.toString();
					if (synset_num.containsKey(str))
						str_to_write.append(synset_num.get(str) + "\t" + str + "\n");

				}
			}

			ReadWriteFile.writeFile("file//ohsumed_wordnet_id//" + file.getName(), str_to_write.toString());

		}

	}

}
