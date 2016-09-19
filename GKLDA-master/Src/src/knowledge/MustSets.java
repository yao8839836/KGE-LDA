package knowledge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nlp.Vocabulary;

/**
 * This class implements knowledge in the forms of must-sets.
 */
public class MustSets implements Iterable<MustSet> {
	public ArrayList<MustSet> mustsetList = null;
	public Map<MustSet, Integer> mustsetToIndexMap = null;
	public Map<String, ArrayList<MustSet>> wordstrToMustsetListMap = null;

	public MustSets() {
		mustsetList = new ArrayList<MustSet>();
		mustsetToIndexMap = new HashMap<MustSet, Integer>();
		wordstrToMustsetListMap = new HashMap<String, ArrayList<MustSet>>();
	}

	public void addMustSet(MustSet mustset) {
		mustsetToIndexMap.put(mustset, mustsetList.size());
		mustsetList.add(mustset);

		for (String wordstr : mustset) {
			addWordIntoMap(wordstr, mustset);
		}
	}

	public void addMustSets(MustSets mustsets) {
		for (MustSet mustset : mustsets) {
			this.addMustSet(mustset);
		}
	}

	/**
	 * Add the wordstr and its must-set into the map.
	 */
	private void addWordIntoMap(String wordstr, MustSet mustset) {
		if (!wordstrToMustsetListMap.containsKey(wordstr)) {
			wordstrToMustsetListMap.put(wordstr, new ArrayList<MustSet>());
		}
		wordstrToMustsetListMap.get(wordstr).add(mustset);
	}

	public void addSingletonMustSetToEveryWord(Vocabulary vocab) {
		for (int v = 0; v < vocab.size(); ++v) {
			String wordstr = vocab.getWordstrByWordid(v);
			MustSet mustset = new MustSet(wordstr);
			this.addMustSet(mustset);
		}
	}

	public void addSingletonMustSetToWordsWithNoMustSet(Vocabulary vocab) {
		for (int v = 0; v < vocab.size(); ++v) {
			String wordstr = vocab.getWordstrByWordid(v);
			if (!wordstrToMustsetListMap.containsKey(wordstr)) {
				MustSet mustset = new MustSet(wordstr);
				this.addMustSet(mustset);
			}
		}
	}

	public MustSets removeDomainIrrelevantWords(Vocabulary vocab) {
		MustSets mustsets_new = new MustSets();
		for (MustSet mustset : this.mustsetList) {
			MustSet mustset_new = new MustSet();
			for (String wordstr : mustset) {
				if (vocab.containsWordstr(wordstr)) {
					mustset_new.wordset.addWord(wordstr);
				}
			}
			if (mustset_new.wordset.size() > 0) {
				mustsets_new.addMustSet(mustset_new);
			}
		}
		return mustsets_new;
	}

	public MustSet getMustSet(int index) {
		assert (index < this.size() && index >= 0) : "Index is not correct!";
		return mustsetList.get(index);
	}

	public int getMustSetIndex(MustSet mustset) {
		assert (mustsetToIndexMap.containsKey(mustset)) : "This mustset is not in the mustsets!";
		return mustsetToIndexMap.get(mustset);
	}

	/**
	 * Get the list of must-sets that contain this word.
	 * 
	 * @return
	 */
	public ArrayList<MustSet> getMustSetListGivenWordstr(String wordstr) {
		if (!wordstrToMustsetListMap.containsKey(wordstr)) {
			return new ArrayList<MustSet>();
		} else {
			return wordstrToMustsetListMap.get(wordstr);
		}
	}

	public int size() {
		return mustsetList.size();
	}

	@Override
	public String toString() {
		StringBuilder sbMustSets = new StringBuilder();
		for (MustSet mustset : mustsetList) {
			sbMustSets.append(mustset.toString());
			sbMustSets.append(System.lineSeparator());
		}
		return sbMustSets.toString();
	}

	@Override
	public Iterator<MustSet> iterator() {
		return mustsetList.iterator();
	}

}
