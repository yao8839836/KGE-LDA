package nlp;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import utility.ExceptionUtility;
import utility.FileReaderAndWriter;

/**
 * Contains mapping from word id to word, and vice versa.
 */
public class Vocabulary {
	public Map<Integer, String> wordidToWordstrMap = null;
	public Map<String, Integer> wordstrToWordidMap = null;

	public Vocabulary() {
		wordstrToWordidMap = new TreeMap<String, Integer>();
		wordidToWordstrMap = new TreeMap<Integer, String>();
	}

	/**
	 * Read the vocabulary from the file.
	 * 
	 * @param filePath
	 * @return
	 */
	public static Vocabulary getVocabularyFromFile(String filePath) {
		Vocabulary vocab = new Vocabulary();

		ArrayList<String> lines = FileReaderAndWriter
				.readFileAllLines(filePath);
		for (String line : lines) {
			String[] splits = line.trim().split(":");
			ExceptionUtility.assertAsException(splits.length == 2);
			int wordid = Integer.parseInt(splits[0]);
			String wordstr = splits[1];
			vocab.addWordstrWithWordid(wordid, wordstr);
		}

		return vocab;
	}

	public void addVocabulary(Vocabulary vocab) {
		for (String wordstr : vocab.wordstrToWordidMap.keySet()) {
			if (!containsWordstr(wordstr)) {
				addWordstrWithoutWordid(wordstr);
			}
		}
	}

	/**
	 * Add a (wordid, wordstr) into the vocabulary. If the wordstr already
	 * exists in the vocabulary, then output errors.
	 */
	public void addWordstrWithWordid(int wordid, String wordstr) {
		ExceptionUtility.assertAsException(!containsWordid(wordid),
				"The word id already exists in the vocabulary!");
		ExceptionUtility.assertAsException(!containsWordstr(wordstr),
				"The word string already exists in the vocabulary!");
		wordidToWordstrMap.put(wordid, wordstr);
		wordstrToWordidMap.put(wordstr, wordid);
	}

	/**
	 * Add a wordstr into the vocabulary. We assign it a new wordid. If the
	 * wordstr already exists in the vocabulary, then output errors.
	 */
	public void addWordstrWithoutWordid(String wordstr) {
		ExceptionUtility.assertAsException(!containsWordstr(wordstr),
				"The word string already exists in the vocabulary!");
		int wordid = this.size();
		wordidToWordstrMap.put(wordid, wordstr);
		wordstrToWordidMap.put(wordstr, wordid);
	}

	public boolean containsWordstr(String wordstr) {
		return wordstrToWordidMap.containsKey(wordstr);
	}

	public boolean containsWordid(int wordid) {
		return wordidToWordstrMap.containsKey(wordid);
	}

	public String getWordstrByWordid(int wordid) {
		ExceptionUtility.assertAsException(containsWordid(wordid));
		return wordidToWordstrMap.get(wordid);
	}

	public int getWordidByWordstr(String wordstr) {
		ExceptionUtility.assertAsException(containsWordstr(wordstr));
		return wordstrToWordidMap.get(wordstr);
	}

	public int size() {
		return wordidToWordstrMap.size();
	}

	public void printToFile(String filepath) {
		StringBuilder sbOutput = new StringBuilder();
		for (Map.Entry<Integer, String> entry : wordidToWordstrMap.entrySet()) {
			int wordid = entry.getKey();
			String wordstr = entry.getValue();
			sbOutput.append(wordid + ":" + wordstr);
			sbOutput.append(System.getProperty("line.separator"));
		}
		FileReaderAndWriter.writeFile(filepath, sbOutput.toString());
	}
}
