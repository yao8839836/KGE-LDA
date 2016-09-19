package test;

import java.io.File;
import java.io.IOException;

import util.ReadWriteFile;

public class OhsumedDocs {

	static StringBuilder sb = new StringBuilder();

	public static void main(String[] args) throws IOException {
		traverseFolder2("data//ohsumed_10//");

		ReadWriteFile.writeFile("data//ohsumed_10.txt", sb.toString());
	}

	public static void traverseFolder2(String path) {

		File file = new File(path);
		if (file.exists()) {
			File[] files = file.listFiles();
			if (files.length == 0) {
				System.out.println("文件夹是空的!");
				return;
			} else {
				for (File file2 : files) {
					if (file2.isDirectory()) {
						System.out.println("文件夹:" + file2.getAbsolutePath());
						traverseFolder2(file2.getAbsolutePath());
					} else {
						String str = file2.getAbsolutePath();
						System.out.println("文件:" + str);

						String[] temp = str.split("\\\\");
						sb.append(str + "\t" + temp[temp.length - 3] + "\t" + temp[temp.length - 2] + "\n");

					}
				}
			}
		} else {
			System.out.println("文件不存在!");
		}
	}

}
