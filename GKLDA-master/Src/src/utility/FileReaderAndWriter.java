package utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

public class FileReaderAndWriter {
	/**
	 * Read the content from the file.
	 * 
	 * @param filePath		the file path of the file.
	 * @return 				the content of the file as a string.
	 * @throws IOException
	 */
	public static String readFile(String filePath) {
		filePath = OSFilePathConvertor.convertOSFilePath(filePath);
		StringBuilder sbContent = new StringBuilder();
		try {
			File aFile = new File(filePath);
			BufferedReader input = new BufferedReader(new FileReader(aFile));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					sbContent.append(line);
					sbContent.append(System.getProperty("line.separator"));
				}
			} finally {
				input.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sbContent.toString();
	}

	/**
	 * Read each line of content from the file and store them in the
	 * list of Strings.
	 * 
	 * @param filePath		the file path of the file.
	 * @return 				the content of the file as a list of Strings.
	 * @throws IOException
	 */
	public static ArrayList<String> readFileAllLines(String filePath) {
		filePath = OSFilePathConvertor.convertOSFilePath(filePath);
		ArrayList<String> contentLines = new ArrayList<String>();
		try {
			File aFile = new File(filePath);
			BufferedReader input = new BufferedReader(new FileReader(aFile));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					contentLines.add(line);
				}
			} finally {
				input.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return contentLines;
	}

	/**
	 * Write the content to the file. If the file does not exist, create it
	 * first.
	 * 
	 * @param filePath
	 * @param content
	 * @throws IOException
	 */
	public static void writeFile(String filePath, String content) {
		filePath = OSFilePathConvertor.convertOSFilePath(filePath);
		try {
			File aFile = new File(filePath);
			// Create parent directory.
			// Attention: if the file path contains "..", mkdirs() does not
			// work!
			File parent = aFile.getParentFile();
			parent.mkdirs();
			
			if (!aFile.exists()) {
				aFile.createNewFile();
			}
			Writer output = new BufferedWriter(new FileWriter(aFile));
			try {
				output.write(content);
			} finally {
				output.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Add the method that we can specify the encoding for output. Write the
	 * content to the file. If the file does not exist, create it first.
	 * 
	 * @param filePath
	 * @param content
	 * @throws IOException
	 */
	public static void writeFile(String filePath, String content,
			String encoding) {
		filePath = OSFilePathConvertor.convertOSFilePath(filePath);
		try {
			File aFile = new File(filePath);
			// Create parent directory.
			// Attention: if the file path contains "..", mkdirs() does not
			// work!
			File parent = aFile.getParentFile();
			parent.mkdirs();
			
			if (!aFile.exists()) {
				aFile.createNewFile();
			}
			OutputStream fout = new FileOutputStream(filePath);
			OutputStreamWriter out = new OutputStreamWriter(fout, encoding);
			out.write(content);
			out.close();
			fout.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
