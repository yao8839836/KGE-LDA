package nlp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import utility.FileReaderAndWriter;

/**
 * A corpus contains two components:
 * 
 * 1. Documents where each document contains a list of word ids.
 * 
 * 2. Vocabulary: the mapping from word id to word.
 */
public class Corpus {
	public String domain = null; // Domain name.
	public Vocabulary vocab = null;
	public int[][] docs = null;
	public String[][] docsStr = null;

	// Build the inverted index that is used to compute document
	// frequency and co-document frequency.
	private Map<String, HashSet<Integer>> wordstrToSetOfDocsMap = null;

	public Corpus(String domain2) {
		domain = domain2;
		wordstrToSetOfDocsMap = new TreeMap<String, HashSet<Integer>>();
	}

	/**
	 * Read the corpus from the files (both docs and vocab).
	 */
	public static Corpus getCorpusFromFile(String domain, String docsFilepath,
			String vocabFilepath) {
		Corpus corpus = new Corpus(domain);

		// Read the vocab file.
		corpus.vocab = Vocabulary.getVocabularyFromFile(vocabFilepath);

		// Read the docs file.
		ArrayList<String> docsLines = FileReaderAndWriter
				.readFileAllLines(docsFilepath);
		// Ignore the empty line.
		ArrayList<String> docsLines_nonEmpty = new ArrayList<String>();
		for (String line : docsLines) {
			if (line.trim().length() > 0) {
				docsLines_nonEmpty.add(line);
			}
		}
				
		int size = docsLines_nonEmpty.size();
		corpus.docs = new int[size][];
		corpus.docsStr = new String[size][];
		for (int d = 0; d < size; ++d) {
			String docsLine = docsLines_nonEmpty.get(d);
			String[] splits = docsLine.trim().split(" ");
			int length = splits.length;
			corpus.docs[d] = new int[length];
			corpus.docsStr[d] = new String[length];
			for (int n = 0; n < length; ++n) {
				int wordid = Integer.parseInt(splits[n]);
				corpus.docs[d][n] = wordid;
				corpus.docsStr[d][n] = corpus.vocab.getWordstrByWordid(wordid);
				// Update the inverted index.
				String wordstr = corpus.vocab.getWordstrByWordid(wordid);
				if (!corpus.wordstrToSetOfDocsMap.containsKey(wordstr)) {
					corpus.wordstrToSetOfDocsMap.put(wordstr,
							new HashSet<Integer>());
				}
				HashSet<Integer> setOfDocs = corpus.wordstrToSetOfDocsMap
						.get(wordstr);
				setOfDocs.add(d);
			}
		}

		return corpus;
	}

	/**
	 * Get the number of documents in the corpus.
	 */
	public int getNoofDocuments() {
		return docs == null ? 0 : docs.length;
	}

	/**
	 * Get the number of documents that contain this word.
	 */
	public int getDocumentFrequency(String wordstr) {
		if (!wordstrToSetOfDocsMap.containsKey(wordstr)) {
			return 0;
		}
		return wordstrToSetOfDocsMap.get(wordstr).size();
	}

	/**
	 * Get the co-document frequency which is the number of documents that both
	 * words appear.
	 */
	public int getCoDocumentFrequency(String wordstr1, String wordstr2) {
		if (!wordstrToSetOfDocsMap.containsKey(wordstr1)
				|| !wordstrToSetOfDocsMap.containsKey(wordstr2)) {
			return 0;
		}
		HashSet<Integer> setOfDocs1 = wordstrToSetOfDocsMap.get(wordstr1);
		HashSet<Integer> setOfDocs2 = wordstrToSetOfDocsMap.get(wordstr2);
		HashSet<Integer> intersection = new HashSet<Integer>(setOfDocs1);
		intersection.retainAll(setOfDocs2);
		return intersection.size();
	}
}
