# KGE-LDA

The implementation of Knowledge Graph Embedding LDA in our paper:

Liang Yao, Yin Zhang, Baogang Wei, Zhe Jin, Rui Zhang, Yangyang Zhang, Qinfei Chen. Incorporating Knowledge Graph Embeddings into Topic Modeling.  In 31st AAAI Conference on Artificial Intelligence (AAAI 2017). (accepted)

Require Java 7+ and Eclipse.

#Implementation

KGE-LDA(a): KGE-LDA/src/topic/LinkLDA_KGE.java

KGE-LDA(b): KGE-LDA/src/topic/CorrLDA_KGE.java

# Main entries

The main entries for KGE-LDA(a), KGE-LDA(b), Corr-LDA, CI-LDA (Link-LDA), CTM and LDA on the three datasets 20NG, NIPS and Ohsumed are in KGE-LDA/src/result20ng/, KGE-LDA/src/resultnips/ and KGE-LDA/src/resultohsumed/.
The main entries are ResultXXXXXX.java

The main entries for GK-LDA on the three datasets 20NG, NIPS and Ohsumed are in GKLDA-master/Src/src/launch. The main entries are ResultGKLDAXXX.java


The main entries for LF-LDA on the three datasets 20NG, NIPS and Ohsumed are in LFTM/src/test/. The main entries are ResultLFLDAXXX.java

# Reproduce results

To reproduce the results in the paper:

(1) Decompress /KGE-LDA/data.zip and /KGE-LDA/file.7z in the same fold.

(2) Download the three Wikipedia index files 20ng_word_wiki_small_index.zip (http://pan.baidu.com/s/1qXDVoVq), nips_word_wiki_index_small.rar (http://pan.baidu.com/s/1hs9HZve) and ohsumed_23_word_wiki_index.zip (http://pan.baidu.com/s/1miATVkO), decompress them and put the decompressed folds to the GKLDA-master/Src/file/, /KGE-LDA/file/ and /LFTM/file/.
The 4,776,093 Wikipedia articles are at (http://pan.baidu.com/s/1slaTPoT), I extracted them from http://deepdive.stanford.edu/opendata/.

(3) Run the main entries.

#Others

(1) The raw text datasets are in three folds under /KGE-LDA/data/.

(2) The tokenized documents are in /KGE-LDA/file/20ng/, /KGE-LDA/file/nips/ and /KGE-LDA/file/ohsumed/.

(3) The linked entities in WordNet(via NLTK) are in /KGE-LDA/file/20ng_wordnet/, /KGE-LDA/file/nips_wordnet/ and /KGE-LDA/file/ohsumed_wordnet/. their ids are in /KGE-LDA/file/xxx_wordnet_id/.

(4) To tokenize your own documents, you also need to download the model file of Stanford CoreNLP (http://pan.baidu.com/s/1bpDqa7d) and add it to the class path.

(5) The unique entities for 20NG are in /KGE-LDA/knowledge/WN18/entity_appear.txt, the unique entities for NIPS are in /KGE-LDA/knowledge/WN18/entity_appear_nips.txt,  the unique entities for Ohsumed are in /KGE-LDA/knowledge/WN18/entity_appear_ohsumed.txt

(6) The 50 dimensional entity embeddings for 20NG are in /KGE-LDA/knowledge/WN18/entity2vec_appear.bern, the 50 dimensional entity embeddings for NIPS are in /KGE-LDA/knowledge/WN18/entity2vec_appear_nips.bern, the 50 dimensional entity embeddings for Ohsumed are in /KGE-LDA/knowledge/WN18/entity2vec_appear_ohsumed.bern. To get all 50 dimensional entity embeddings in WN18, see the three files: /KGE-LDA/knowledge/WN18/entity2vec.bern, /KGE-LDA/knowledge/WN18/entity2id.txt, /KGE-LDA/knowledge/WN18/num_synset.txt
