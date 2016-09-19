package test;

import java.io.File;
import java.io.IOException;

import util.ReadWriteFile;

public class BuildWordIndexNIPS {

	public static void main(String[] args) throws IOException {

		File[] files = new File("file//ohsumed_23_word_wiki//").listFiles();

		int count = 0;
		for (File file : files) {

			String content = ReadWriteFile.getTextContent(file);

			String[] lines = content.split("\n");

			System.out.println(lines.length);

			for (int i = 0; i < lines.length; i++) {

				String line = lines[i];

				String[] temp = line.split(" ");

				StringBuilder sb = new StringBuilder();

				for (String str : temp) {
					sb.append(str + "\n");
				}

				ReadWriteFile.writeFile("file//ohsumed_23_word_wiki_index//" + i, sb.toString());

			}
			System.out.println(count);
			count++;

		}

	}

}
