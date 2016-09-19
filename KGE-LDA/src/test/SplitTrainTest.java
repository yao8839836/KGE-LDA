package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import util.ReadWriteFile;

public class SplitTrainTest {

	public static void main(String[] args) throws IOException {

		File files = new File("data//TMNfull.LABEL");

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(files), "UTF-8"));

		String line = "";

		int count = 0;

		StringBuilder sb = new StringBuilder();

		while ((line = reader.readLine()) != null) {

			if (Math.random() < 0.7) {
				System.out.println(count + "\t" + "train" + "\t" + line);
				sb.append(count + "\t" + "train" + "\t" + line + "\n");
			} else {
				System.out.println(count + "\t" + "test" + "\t" + line);
				sb.append(count + "\t" + "test" + "\t" + line + "\n");
			}

			count++;

		}

		reader.close();

		ReadWriteFile.writeFile("data//TMNfull_label.txt", sb.toString());

	}

}
