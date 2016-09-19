package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import preprocessing.StopRemove;
import util.ReadWriteFile;

public class Tokenizer {

	public static void main(String[] args) throws IOException {

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		File files = new File("data//ohsumed.txt");

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(files), "UTF-8"));

		String line = "";

		int count = 0;

		while ((line = reader.readLine()) != null) {

			String[] temp = line.split("\t");

			File file = new File(temp[0]);

			String content = ReadWriteFile.getTextContent(file);

			// create an empty Annotation just with the given text
			Annotation document = new Annotation(content);

			// run all Annotators on this text
			pipeline.annotate(document);

			StringBuilder str = new StringBuilder();

			List<CoreMap> sentences = document.get(SentencesAnnotation.class);

			for (CoreMap sentence : sentences) {

				// StringBuilder sent = new StringBuilder();
				for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
					// this is the text of the token
					String word = token.get(TextAnnotation.class);

					str.append(StopRemove.outerDepunc(word.toLowerCase()).trim() + " ");

					// sent.append(StopRemove.outerDepunc(word.toLowerCase())
					// .trim() + " ");

				}

				// str.append(sent.toString().trim() + "\n");

			}

			ReadWriteFile.writeFile("file\\ohsumed\\" + count, str.toString().trim());

			count++;

		}

		reader.close();

	}

}
