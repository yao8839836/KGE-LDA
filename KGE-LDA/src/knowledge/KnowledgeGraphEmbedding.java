package knowledge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ejml.data.DenseMatrix64F;

public class KnowledgeGraphEmbedding {

	Map<Integer, double[]> entityMap;

	public int numVectors;

	int dimension = 50;

	/**
	 * 构造方法,从文件中得到词向量
	 * 
	 * @param filename
	 *            存储词向量的文件
	 * @throws IOException
	 */
	public KnowledgeGraphEmbedding(String filename) throws IOException {

		File f = new File(filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
		String line = "";

		entityMap = new HashMap<>();

		int line_no = 0;

		while ((line = reader.readLine()) != null) {

			String[] temp = line.split("\t");

			double[] vector = new double[temp.length];

			for (int i = 0; i < vector.length; i++) {

				vector[i] = Double.parseDouble(temp[i]);
			}

			entityMap.put(line_no, vector);

			line_no++;

		}

		reader.close();

	}

	public KnowledgeGraphEmbedding() {

	}

	/**
	 * 获取实体向量表
	 * 
	 * @return 实体向量表
	 */
	public Map<Integer, double[]> getEntityVector() {

		return entityMap;
	}

	public DenseMatrix64F readData(String filename) throws IOException {

		File f = new File(filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
		String line = "";

		List<String> lines = new ArrayList<String>();

		while ((line = reader.readLine()) != null)
			lines.add(line);
		reader.close();

		this.numVectors = lines.size();

		DenseMatrix64F data = new DenseMatrix64F(numVectors, dimension);

		int line_num = lines.size();

		for (int i = 0; i < line_num; i++) {
			String vector = lines.get(i);
			String[] vals = vector.split("\t");

			int j = 0;
			for (String val : vals) {
				double dVal = Double.parseDouble(val);
				data.set(i, j, dVal);
				j++;
			}
		}
		return data;

	}

}
