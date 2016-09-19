package launch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utility.ReadWriteFile;

public class GenerateMustLinks {

	public static void main(String[] args) throws IOException {

		String content = ReadWriteFile
				.getTextContent(new File("../Data/Input/Knowledge/ohsumed_sense_word_wn18_must.txt"));

		Set<Set<String>> must_set = new HashSet<>();

		String[] lines = content.split("\n");

		for (String line : lines) {

			System.out.println(line);
			String[] temp = line.split("\t");

			String[] words = temp[1].split(" ");

			for (String word_1 : words) {

				for (String word_2 : words) {

					if (!word_1.equals(word_2)) {

						Set<String> must = new HashSet<>();
						must.add(word_1);

						must.add(word_2);

						must_set.add(must);
					}

				}

			}

		}

		StringBuilder sb = new StringBuilder();

		for (Set<String> must : must_set) {

			StringBuilder must_str = new StringBuilder("{");

			List<String> pair = new ArrayList<>(must);

			must_str.append(pair.get(0));

			must_str.append(",");

			must_str.append(pair.get(1) + "}");

			sb.append(must_str.toString() + "\n");

		}

		ReadWriteFile.writeFile("../Data/Input/Knowledge/ohsumed_must.txt", sb.toString());

	}

}
