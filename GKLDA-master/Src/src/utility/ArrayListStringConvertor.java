package utility;

import java.util.ArrayList;

/**
 * Transfer from list to string, or verse vice.
 * 
 * 1. Transfer from 1D list of string to string.
 * 
 * 2. Transfer from 2D list of string to string.
 * 
 * 3. Transfer from 2D list of string to 1D list of string.
 * 
 * 4. Transfer from string to 1D list of string.
 * 
 */
public class ArrayListStringConvertor {
	public static String convertFrom1DListToString(ArrayList<String> list) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); ++i) {
			sb.append(list.get(i));
			sb.append(' ');
		}
		return sb.toString().trim();
	}

	public static String convertFrom2DListToString(
			ArrayList<ArrayList<String>> list) {
		StringBuilder sb = new StringBuilder();
		for (ArrayList<String> l : list) {
			for (int i = 0; i < l.size(); ++i) {
				sb.append(convertFrom1DListToString(l));
				sb.append(' ');
			}
		}
		return sb.toString().trim();
	}

	public static ArrayList<String> convertFrom2DListTo1DList(
			ArrayList<ArrayList<String>> list) {
		ArrayList<String> returnList = new ArrayList<String>();
		for (ArrayList<String> l : list) {
			returnList.add(convertFrom1DListToString(l));
		}
		return returnList;
	}

	public static ArrayList<String> convertFromStringTo1DList(String str,
			String separator) {
		ArrayList<String> list = new ArrayList<String>();
		String[] strSplits = str.split(separator);
		for (String s : strSplits) {
			list.add(s);
		}
		return list;
	}
}
