package test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import util.ReadWriteFile;

public class DeleteMustLinks {

	public static void main(String[] args) throws IOException {

		String sense_file_content = ReadWriteFile.getTextContent(new File("knowledge//WN18//num_synset.txt"));

		String[] senses = sense_file_content.split("\n");

		System.out.println(senses.length);

		Set<String> wn18_senses = new HashSet<>();

		for (String sense : senses) {

			String[] temp = sense.split("\t");

			String sense_name = temp[1];

			System.out.println(sense_name);

			wn18_senses.add(sense_name);

		}

		String sense_word_content = ReadWriteFile.getTextContent(new File("file//ohsumed_sense_word.txt"));

		senses = sense_word_content.split("\n");

		StringBuilder sb = new StringBuilder();

		for (String sense : senses) {

			String[] temp = sense.split("\t");

			String sense_name = temp[0];

			System.out.println(sense_name);

			String synset = sense_name.substring(sense_name.indexOf("'") + 1, sense_name.lastIndexOf("'"));

			String[] attributes = synset.split("\\.");

			StringBuilder syn_sb = new StringBuilder();

			syn_sb.append("__" + attributes[0]);

			if (attributes[1].equals("n"))
				syn_sb.append("_" + "NN");
			else if (attributes[1].equals("v"))
				syn_sb.append("_" + "VB");
			else if (attributes[1].equals("a"))
				syn_sb.append("_" + "JJ");

			if (!attributes[2].equals("") && Character.isDigit(attributes[2].charAt(0)))
				syn_sb.append("_" + Integer.parseInt(attributes[2]));

			String str = syn_sb.toString();
			System.out.println(str);

			if (temp.length <= 1)
				continue;
			String words = temp[1];

			if (words.split(" ").length <= 1)
				continue;

			if (wn18_senses.contains(str))
				sb.append(sense + "\n");

		}

		ReadWriteFile.writeFile("file//ohsumed_sense_word_wn18_must.txt", sb.toString());

	}

}
