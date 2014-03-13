package de.uni_leipzig.informatik.asv.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CLDACorpus
{
	private int[][] documents;
	private int vocabularySize = 0;
	private String[] wordStrings;
	private String[] dateList, authorsList;

	public CLDACorpus(InputStream is) throws IOException
	{
		ArrayList<int[]> corpus = new ArrayList<int[]>();
		ArrayList<String> dates = new ArrayList<String>();
		ArrayList<String> authors = new ArrayList<String>();
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		HashMap<String, Integer> vocabulary = new HashMap<String, Integer>();
		HashSet<String> distinctTweets = new HashSet<String>();
		String line = null;
		while ((line = in.readLine()) != null)
		{
			String[] parts = line.split("\\|");
			if (distinctTweets.contains(parts[0]))
				continue;
			distinctTweets.add(parts[0]);
			String[] words = parts[0].split(" ");
			dates.add(parts[1]);
			authors.add(parts[2]);
			int[] doc = new int[words.length];
			int index = 0;
			for (String word : words)
			{
				if (vocabulary.containsKey(word))
				{
					doc[index++] = vocabulary.get(word);
				}
				else
				{
					doc[index++] = vocabularySize;
					vocabulary.put(word, vocabularySize);
					vocabularySize++;

				}
			}
			corpus.add(doc);
		}

		documents = new int[corpus.size()][];
		for (int i = 0; i < documents.length; i++)
		{
			documents[i] = corpus.get(i);
		}

		wordStrings = new String[vocabularySize];
		for (String s : vocabulary.keySet())
		{
			wordStrings[vocabulary.get(s)] = s;
		}
		dateList = dates.toArray(new String[0]);
		authorsList = authors.toArray(new String[0]);
	}

	public int[][] getDocuments()
	{
		return documents;
	}

	public int getVocabularySize()
	{
		return vocabularySize;
	}

	public String[] getWordStrings()
	{
		return wordStrings;
	}

	public String[] getDateList()
	{
		return dateList;
	}

	public String[] getAuthorList()
	{
		return authorsList;
	}
}
