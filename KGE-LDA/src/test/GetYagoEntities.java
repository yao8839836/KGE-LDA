package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import util.ReadWriteFile;

public class GetYagoEntities {

	public static void main(String[] args) throws IOException {

		String triples = ReadWriteFile.getTextContent(new File("knowledge\\YAGO2\\yagoFacts.tsv"));

		Set<String> entities = new HashSet<>();

		Set<String> relations = new HashSet<>();

		StringBuilder sb = new StringBuilder();

		String[] lines = triples.split("\n");

		for (String line : lines) {

			String[] temp = line.split("\t");

			entities.add(temp[1]);

			entities.add(temp[3]);

			relations.add(temp[2]);

			sb.append(temp[1] + "\t" + temp[3] + "\t" + temp[2] + "\n");

		}

		ReadWriteFile.writeFile("knowledge\\YAGO2\\train.txt", sb.toString());

		// 实体数，关系数、三元组数

		System.out.println(entities.size() + "\t" + relations.size() + "\t" + lines.length);

		List<String> entity_list = new ArrayList<>(entities);
		List<String> relation_list = new ArrayList<>(relations);

		sb = new StringBuilder();

		int entity_list_size = entity_list.size();

		for (int i = 0; i < entity_list_size; i++) {

			sb.append(entity_list.get(i) + "\t" + i + "\n");

		}

		ReadWriteFile.writeFile("knowledge\\YAGO2\\entity2id.txt", sb.toString());

		sb = new StringBuilder();

		int relation_list_size = relation_list.size();

		for (int i = 0; i < relation_list_size; i++) {

			sb.append(relation_list.get(i) + "\t" + i + "\n");

		}

		ReadWriteFile.writeFile("knowledge\\YAGO2\\relation2id.txt", sb.toString());
	}

}
