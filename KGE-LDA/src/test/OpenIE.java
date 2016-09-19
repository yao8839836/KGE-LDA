package test;

import java.util.*;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class OpenIE {

	public static void main(String[] args) throws Exception {

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, depparse, natlog, openie");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		Annotation doc = new Annotation("Obama was born in Hawaii. He is our president.");
		pipeline.annotate(doc);

		for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
			Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
			for (RelationTriple triple : triples) {
				System.out.println(triple.confidence + "\t" + triple.subjectLemmaGloss() + "\t"
						+ triple.relationLemmaGloss() + "\t" + triple.objectLemmaGloss());
			}
		}
	}

}
