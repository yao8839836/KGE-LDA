package test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import util.ReadWriteFile;

public class DeleteOhsumedDocs {

	static Map<String, Integer> doc_count = new HashMap<>();

	static Set<String> contents = new HashSet<>();

	static StringBuilder sb = new StringBuilder();

	public static void main(String[] args) throws IOException {

		traverseFolder1("data//ohsumed-first-20000-docs//");

		traverseFolder2("data//ohsumed-first-20000-docs//");

		// ReadWriteFile.writeFile("data//ohsumed.txt", sb.toString());

		System.out.println(contents.size());

		System.out.println(doc_count.keySet().size());

	}

	public static void traverseFolder1(String path) throws IOException {

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
						traverseFolder1(file2.getAbsolutePath());
					} else {
						String str = file2.getAbsolutePath();
						System.out.println("文件:" + str);

						String name = file2.getName();
						System.out.println(name);

						String content = ReadWriteFile.getTextContent(file2);

						contents.add(content);

						if (doc_count.containsKey(name)) {
							doc_count.put(name, doc_count.get(name) + 1);
						} else {
							doc_count.put(name, 1);
						}

					}
				}
			}
		} else {
			System.out.println("文件不存在!");
		}
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

						String name = file2.getName();

						if (doc_count.get(name) > 1) {
							file2.delete();

						} else {
							String[] temp = str.split("\\\\");
							sb.append(str + "\t" + temp[temp.length - 3] + "\t" + temp[temp.length - 2] + "\n");
						}

					}
				}
			}
		} else {
			System.out.println("文件不存在!");
		}
	}

}
