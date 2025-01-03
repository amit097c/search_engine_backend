package com.acc.search.engine.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

public class SpellChecking {
	
	private static ArrayList<String> vocab = new ArrayList<>();

	private static void getVocab() throws IOException {
		File files = new File("src/main/resources/static/dat/text");
		File[] texts = files.listFiles();
		StringBuilder line = new StringBuilder();
		assert texts != null;
		for (File txt : texts) {
			BufferedReader br = new BufferedReader(new FileReader(txt));
			String str;
			while ((str = br.readLine()) != null) {
				line.append(str);
			}
			br.close();
		}
		String fullText = line.toString();
		StringTokenizer tokenizer =
				new StringTokenizer(fullText, "0123456789 ,`*$|~(){}_@><=+[]\\?;/&#-.!:\"'\n\t\r");
		while (tokenizer.hasMoreTokens()) {
			String tk = tokenizer.nextToken().toLowerCase(Locale.ROOT);
			if (!vocab.contains(tk)) {
				vocab.add(tk);
			}
		}
	}

	public static String[] getAltWords(String query) throws IOException {
		getVocab();
		HashMap<String, Integer> map = new HashMap<>();
		String[] altWords = new String[10];
		for (String w : vocab) {
			int editDis = editDistance(query, w);
			map.put(w, editDis);
		}
		Map<String, Integer> map1 = sortByValue(map);
		// get top 10 alternative words
		int rank = 0;
		for (Map.Entry<String, Integer> en : map1.entrySet()) {
			if (en.getValue() != 0) {
				altWords[rank] = en.getKey();
				rank++;
				if (rank == 10){ break; }
			}
		}
		return altWords;
	}

	public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> map)
	{
		// Create a list from elements of HashMap
		List<Map.Entry<String, Integer> > list = new LinkedList<>(map.entrySet());

		// Sort the list
		list.sort(Map.Entry.comparingByValue());
		
		// put data from sorted list to hashmap
		HashMap<String, Integer> temp = new LinkedHashMap<>();
		for (Map.Entry<String, Integer> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

	public static int editDistance(String word1, String word2) {
		int len1 = word1.length();
		int len2 = word2.length();
		int[][] dp = new int[len1 + 1][len2 + 1];	// Edit distance table

		for (int i = 0; i <= len1; i++) { dp[i][0] = i;	}
		for (int j = 0; j <= len2; j++) { dp[0][j] = j; }
		//iterate though, and check last char
		for (int i = 0; i < len1; i++) {
			char c1 = word1.charAt(i);
			for (int j = 0; j < len2; j++) {
				char c2 = word2.charAt(j);
	 
				//if last two chars equal
				if (c1 == c2) {
					//update dp value for +1 length
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;
	 
					int min = Math.min(replace, insert);
					min = Math.min(delete, min);
					dp[i + 1][j + 1] = min;
				}
			}
		}
		return dp[len1][len2];
	}

	public static void main(String[] args) throws IOException{

		getAltWords("computer");
	}

	public static String suggestCorrection(String query) {
		// TODO Auto-generated method stub
		return null;
	}
}
