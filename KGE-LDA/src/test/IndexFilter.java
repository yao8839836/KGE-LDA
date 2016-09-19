package test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import util.ReadWriteFile;

public class IndexFilter {

	public static void main(String[] args) throws IOException {

		File[] files = new File("file//ohsumed_23_word_wiki_index//").listFiles();

		for (File file : files) {

			String content = ReadWriteFile.getTextContent(file);

			String[] lines = content.split("\n");

			Set<String> wikis = new HashSet<>();

			for (String line : lines) {

				if (line.contains("."))
					wikis.add(line);
			}
			StringBuilder sb = new StringBuilder();
			for (String wiki : wikis) {
				sb.append(wiki + "\n");
			}
			ReadWriteFile.writeFile(file.toString(), sb.toString());
		}

	}

}
