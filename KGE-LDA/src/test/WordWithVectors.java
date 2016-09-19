package test;

import java.io.File;
import java.io.IOException;

import util.ReadWriteFile;

public class WordWithVectors {

	public static void main(String[] args) throws IOException {

		String content = ReadWriteFile.getTextContent(new File("data//wiki_vector_50d.txt"));

		String[] lines = content.split("\n");

		System.out.println(lines.length);

		StringBuilder sb = new StringBuilder();

		for (String line : lines) {

			String[] temp = line.split(" ");
			System.out.println(temp[0]);

			sb.append(temp[0] + "\n");
		}

		ReadWriteFile.writeFile("data\\wiki_vector_word.txt", sb.toString());

	}

}
