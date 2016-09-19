package knowledge;

import java.util.ArrayList;
import java.util.Iterator;

import nlp.Vocabulary;
import nlp.WordSet;

/**
 * This class implements the must-set used in GK-LDA.
 */

public class MustSet implements Iterable<String> {
	public WordSet wordset = null;

	// public double weight = 1.0;

	public MustSet() {
		wordset = new WordSet();
	}

	public MustSet(ArrayList<String> wordstrList) {
		wordset = new WordSet(wordstrList);
	}

	/**
	 * Construct a singleton must-set.
	 */
	public MustSet(String wordstr) {
		ArrayList<String> wordstrList = new ArrayList<String>();
		wordstrList.add(wordstr);
		wordset = new WordSet(wordstrList);
	}

	/**
	 * Get a must-set from a line, e.g., {price, cheap, expensive}.
	 */
	public static MustSet getMustSetFromALine(String line, Vocabulary vocab) {
		WordSet wordset = new WordSet();
		line = line.replace("{", "");
		line = line.replace("}", "");
		String[] strSplits = line.split("[\\s,]");
		for (String split : strSplits) {
			String wordstr = split.trim();
			if (vocab.containsWordstr(wordstr)) {
				wordset.addWord(wordstr);
			}
		}
		MustSet mustset = new MustSet();
		mustset.wordset = wordset;
		return mustset;
	}
	
	public String getWordstr(int index) {
		return wordset.wordstrsList.get(index);
	}

	public int getWordIndex(String wordstr) {
		return wordset.getWordIndex(wordstr);
	}

	public int size() {
		return wordset.size();
	}

	@Override
	public String toString() {
		return "{" + wordset.toString() + "}";
	}

	@Override
	public Iterator<String> iterator() {
		return wordset.iterator();
	}

}
