GK-LDA (General Knowledge based LDA)
===

GK-LDA is an open-source Java package implementing the algorithm proposed in the paper (Chen et al., CIKM 2013), created by [Zhiyuan (Brett) Chen](http://www.cs.uic.edu/~zchen/). For more details, please refer to [this paper](http://www.cs.uic.edu/~zchen/papers/CIKM2013-Zhiyuan(Brett)Chen.pdf).

If you use this package, please cite the paper: __Zhiyuan Chen, Arjun Mukherjee, Bing Liu, Meichun Hsu, Malu Castellanos, and Riddhiman Ghosh. Discovering Coherent Topics Using General Knowledge. In Proceedings of _CIKM 2013_, pages 209-218.__

If you have any question or bug report, please send it to Zhiyuan (Brett) Chen (czyuanacm@gmail.com).

## Table of Contents
- [Quick Start](#quickstart)
- [Commandline Arguments](#commandlinearguments)
- [Input and Output](#inputandoutput)
- [Contact Information](#contactinformation)

<a name="quickstart"/>
## Quick Start

First, Clone the repo: `git clone https://github.com/czyuan/GKLDA.git`.

Then, 2 quick start options are available:

1. Import the directory into Eclipse (__recommended__).

  _If you get the exception Java.Lang.OutOfMemoryError, please increase the Java heap memory for Eclipse: http://www.mkyong.com/eclipse/eclipse-java-lang-outofmemoryerror-java-heap-space/._
  
2. Use [Maven](http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)

  a. Then, change the current working directory to Src.
  ```
  cd GKLDA/Src
  ```
  b. Build the package.
  ```
  mvn clean package
  ```
  c. Increase the Java heap memory for Maven.
  ```
  export MAVEN_OPTS=-Xmx1024m
  ```
  d. Run the program.
  ```
  mvn exec:java -Dexec.mainClass="launch.MainEntry"
  ```

<a name="commandlinearguments"/>
## Commandline Arguments
The commandline arguments are stored in the file "global/CmdOption.java". If no argument is provided, the program uses the default arguments. There are several arguments that are subject to change:

1. -i: the path of input domains directory.
2. -know: the file path of input knowledge file.
2. -o: the path of output model directory.
3. -nthreads: the number of threads used in the program. The program runs in parallel supporting multithreading.
4. -nTopics: the number of topics used in Topic Model for each domain.

<a name="inputandoutput"/>
## Input and Output
### Input
The input directory should contain domain files. For each domain, there should be 2 files (can be opened by text editors):

1. domain.docs: each line (representing a document) contains a list of word ids.
2. domain.vocab: mapping from word id (starting from 0) to word, separated by ":".

The input directory should also contain a knowledge file, in which each line represents a must-set (i.e., a set of words that should appear together under the same topic).

### Output
The output directory contains topic model results for each learning iteration. LearningIteration 0 is always LDA, i.e., without any knowledge. LearningIteration 1 is GK-LDA with the input knowledge. LDA is run first in order to construct word correlation metric used in GK-LDA.

Under each learning iteration folder and sub-folder "DomainModels", there are a list of domain folders where each domain folder contains topic model results for each domain. Under each domain folder, there are 6 files (can be opened by text editors):

1. domain.docs: each line (representing a document) contains a list of word ids.
2. domain.param: parameter settings.
3. domain.tassign: topic assignment for each word in each document.
4. domain.twdist: topic-word distribution
5. domain.twords: top words under each topic. The columns are separated by '\t' where each column corresponds to each topic.
6. domain.vocab: mapping from word id (starting from 0) to word.

<a name="contactinformation"/>
## Contact Information
* Author: Zhiyuan (Brett) Chen
* Affiliation: University of Illinois at Chicago
* Research Area: Text Mining, Machine Learning, Statistical Natural Language Processing, and Data Mining
* Email: czyuanacm@gmail.com
* Homepage: http://www.cs.uic.edu/~zchen/
