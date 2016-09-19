package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import util.Corpus;
import util.ReadWriteFile;

public class WordWikiIndexMultiThread implements Runnable {

	List<File> task_list;

	List<String> vocab;

	int index;

	public WordWikiIndexMultiThread(List<File> task_list, List<String> vocab, int index) {

		this.task_list = task_list;

		this.vocab = vocab;

		this.index = index;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		StringBuilder sb = new StringBuilder();

		Map<String, Set<String>> word_wikis = new HashMap<>();

		for (String word : vocab) {
			word_wikis.put(word, new HashSet<String>());
		}

		int count = 0;

		for (File file : task_list) {

			try {
				String content = ReadWriteFile.getTextContent(file);

				for (String word : vocab) {

					if (content.contains(word)) {

						Set<String> wikis = word_wikis.get(word);
						wikis.add(file.getName());

					}

				}

				System.out.println(count);
				count++;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		for (String word : vocab) {

			StringBuilder word_sb = new StringBuilder();
			Set<String> wikis = word_wikis.get(word);

			for (String wiki : wikis) {
				word_sb.append(wiki + " ");
			}

			String str = word_sb.toString().trim();
			sb.append(str + "\n");

		}

		try {

			System.out.println("写文件");
			ReadWriteFile.writeFile("file//ohsumed_23_word_wiki//ohsumed_word_wikis_" + index + "_"
					+ System.currentTimeMillis() + "_.txt", sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block

			System.out.println("Error");
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws IOException, InterruptedException {

		List<String> vocab = Corpus.getVocab("data//vocab_ohsumed.txt");

		File[] files = new File("E:\\wiki_full\\").listFiles();

		// for (File f : files) {
		//
		// System.out.println(f.getName() + "\t" + files.length);
		// }

		int threads = 20; // 线程数

		ExecutorService pool = Executors.newFixedThreadPool(threads); // 线程池

		for (int i = 0; i < threads; i++) {

			List<File> file_list = new ArrayList<>();

			for (int j = 4500000; j < files.length && j < 4800000; j++) {

				if (j % 20 == i) {
					file_list.add(files[j]);
				}

			}

			pool.submit(new Thread(new WordWikiIndexMultiThread(file_list, vocab, i)));

		}

		pool.shutdown();
		while (!pool.isTerminated())
			Thread.sleep(1000);

	}

}
