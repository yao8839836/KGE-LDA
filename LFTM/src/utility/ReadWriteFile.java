package utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReadWriteFile {

	/**
	 * 将指定内容写入指定文�?
	 * 
	 * @param file
	 * @param content
	 * @throws IOException
	 */
	public static void writeFile(String file, String content)
			throws IOException {

		File result = new File(file);
		OutputStream out = new FileOutputStream(result, false);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out,
				"utf-8"));

		bw.write(content);

		bw.close();
		out.close();
	}

	/**
	 * 
	 * 读取文件内容
	 * 
	 * @param f
	 *            文件
	 * @return
	 * @throws IOException
	 */
	public static String getTextContent(File f) throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(f), "UTF-8"));

		String line = "";

		StringBuilder content = new StringBuilder();

		while ((line = reader.readLine()) != null) {

			content.append(line + "\n");
		}
		reader.close();

		return content.toString();
	}

	/**
	 * 将词表写入文�?
	 * 
	 * @param vocab
	 * @param filename
	 * @throws IOException
	 */
	public static void writeList(List<String> vocab, String filename)
			throws IOException {

		StringBuilder sb = new StringBuilder();

		for (String word : vocab) {

			sb.append(word + "\n");
		}

		writeFile(filename, sb.toString());

	}

	/**
	 * 读取词集合，�?行一个词
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static Set<String> getWordSet(String filename) throws IOException {

		File f = new File(filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(f), "UTF-8"));
		String line = "";

		Set<String> word_set = new HashSet<>();

		while ((line = reader.readLine()) != null) {

			word_set.add(line);
		}
		reader.close();

		return word_set;
	}

}
