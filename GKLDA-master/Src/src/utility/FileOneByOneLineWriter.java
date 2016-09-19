package utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Write String to the file one line by one line.
 * 
 */
public class FileOneByOneLineWriter {
	private BufferedWriter writer = null;

	/**
	 * Create the file if it does not exist.
	 * 
	 * @param outputFilePath
	 */
	public FileOneByOneLineWriter(String outputFilePath) {
		try {
			outputFilePath = OSFilePathConvertor
					.convertOSFilePath(outputFilePath);
			File outputFile = new File(outputFilePath);
			outputFile.getParentFile().mkdirs();
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
			writer = new BufferedWriter(new FileWriter(outputFile));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void write(String line) {
		try {
			writer.write(line);
			writer.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void writeLine(String line) {
		try {
			writer.write(line);
			writer.write(System.getProperty("line.separator"));
			writer.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void writeLine() {
		try {
			writer.write(System.getProperty("line.separator"));
			writer.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void close() {
		try {
			writer.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
