# KGE-LDA

The implementation of Knowledge Graph Embedding LDA.

# Main entries

The main entries for KGE-LDA(a), KGE-LDA(b), Corr-LDA, CI-LDA (Link-LDA), CTM and LDA on the three datasets 20NG, NIPS and Ohsumed are in KGE-LDA/src/result20ng/, KGE-LDA/src/resultnips/ and KGE-LDA/src/resultohsumed/.
The main entries are ResultXXXXXX.java

The main entries for GK-LDA on the three datasets 20NG, NIPS and Ohsumed are in GKLDA-master/Src/src/launch. The main entries are ResultGKLDAXXX.java


The main entries for LF-LDA on the three datasets 20NG, NIPS and Ohsumed are in LFTM/src/test/. The main entries are ResultLFLDAXXX.java

# Reproduce results

To reproduce the results in the paper:

(1) Decompress /KGE-LDA/data.zip and /KGE-LDA/file.7z in the same fold.

(2) Download the three Wikipedia index files 20ng_word_wiki_small_index.zip, nips_word_wiki_index_small.rar and ohsumed_23_word_wiki_index.zip, decompress them and put the decompressed folds to the GKLDA-master/Src/file/, /KGE-LDA/file/ and /LFTM/file/.
The 4,776,093 Wikipedia articles are in , I extracted them from http://deepdive.stanford.edu/opendata/.

(3) Run the main entries.

#Others

(1) The raw text datasets are in three folds under /KGE-LDA/data/.

(2) The tokenized documents are in /KGE-LDA/file/20ng/, /KGE-LDA/file/nips/ and /KGE-LDA/file/ohsumed/.

(3) The linked entities in WordNet(via NLTK) are in /KGE-LDA/file/20ng_wordnet/, /KGE-LDA/file/nips_wordnet/ and /KGE-LDA/file/ohsumed_wordnet/. their ids are in /KGE-LDA/file/xxx_wordnet_id/.