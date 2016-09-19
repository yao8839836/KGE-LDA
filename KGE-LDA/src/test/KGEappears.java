package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import knowledge.KnowledgeGraphEmbedding;
import util.Corpus;
import util.ReadWriteFile;

public class KGEappears {

	public static void main(String[] args) throws IOException {

		File[] entity_files = new File("file//ohsumed_wordnet_id//").listFiles();

		int[][] entities = Corpus.getEntities(entity_files);

		Set<String> entity_set = new HashSet<>();

		for (int[] entity : entities) {

			for (int e : entity) {

				entity_set.add(e + "");
			}
		}

		List<String> entity_list = new ArrayList<>(entity_set);

		KnowledgeGraphEmbedding kge = new KnowledgeGraphEmbedding("knowledge//WN18//entity2vec_100.bern");

		Map<Integer, double[]> vector_map = kge.getEntityVector();

		ReadWriteFile.writeList(entity_list, "knowledge//WN18//entity_appear_ohsumed_100.txt");

		StringBuilder sb = new StringBuilder();

		for (String entity : entity_list) {

			double[] vector = vector_map.get(Integer.parseInt(entity));

			StringBuilder ent = new StringBuilder();

			for (double e : vector) {

				ent.append(e + "\t");
			}

			sb.append(ent.toString().trim() + "\n");

		}
		ReadWriteFile.writeFile("knowledge//WN18//entity2vec_appear_ohsumed_100.bern", sb.toString());

	}
}
