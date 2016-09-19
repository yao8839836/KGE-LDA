package nlp;

import java.util.ArrayList;
import java.util.Iterator;

import utility.ArrayListStringConvertor;

/**
 * This class implements a set of words that can be treated as must-sets,
 * cannot-sets or any sets that have related words.
 * 
 * We use ArrayList to implement the set of words because we need the word
 * index. We assume that there are no duplicate words in the list.
 */
public class WordSet implements Iterable<String> {
	public ArrayList<String> wordstrsList = null;
	public String setString = null;

	public WordSet() {
		wordstrsList = new ArrayList<String>();
		setString = "";
	}

	public WordSet(ArrayList<String> wordstrsList2) {
		wordstrsList = wordstrsList2;
		setString = ArrayListStringConvertor
				.convertFrom1DListToString(wordstrsList);
	}

	public void addWord(String wordstr) {
		if (!wordstrsList.contains(wordstr)) {
			wordstrsList.add(wordstr);
		}
		setString = ArrayListStringConvertor
				.convertFrom1DListToString(wordstrsList);
	}

	public int getWordIndex(String wordstr) {
		return wordstrsList.indexOf(wordstr);
	}

	public int size() {
		return wordstrsList.size();
	}

	@Override
	public boolean equals(Object obj) {
		WordSet set = (WordSet) obj;
		return this.setString.equals(set.setString);
	}

	@Override
	public int hashCode() {
		return setString.hashCode();
	}

	@Override
	public String toString() {
		return setString;
	}

	@Override
	public Iterator<String> iterator() {
		return wordstrsList.iterator();
	}

}
