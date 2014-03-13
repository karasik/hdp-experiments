package de.uni_leipzig.informatik.asv.hdp;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class Frequency
{
	private HashMap<String, Double> prop;

	public Frequency()
	{
		prop = new HashMap<String, Double>();
	}

	public void add(String word)
	{
		if (prop.containsKey(word))
			prop.put(word, prop.get(word) + 1);
		else
			prop.put(word, 1.0);
	}

	private void normalize()
	{
		double sum = 0.0;
		for (Double v : prop.values())
			sum += v;

		for (String word : prop.keySet())
			prop.put(word, prop.get(word) / sum);
	}

	/**
	 * Assumed that P contains this.
	 * 
	 */
	public Frequency diff(Frequency p)
	{
		return Frequency.diff(this, p);
	}

	private static Frequency diff(Frequency a, Frequency b)
	{
		a.normalize();
		b.normalize();

		Frequency c = new Frequency();
		for (String word : a.prop.keySet())
			c.prop.put(word, -a.prop.get(word) * Math.log(b.prop.get(word)));

		c.normalize();
		return c;
	}

	public String[] getTopWords(int top)
	{
		WordProp[] arr = new WordProp[prop.size()];
		int index = 0;
		for (String word : prop.keySet())
			arr[index++] = new WordProp(word, prop.get(word));

		Arrays.sort(arr, new Comparator<WordProp>()
		{

			public int compare(WordProp a, WordProp b)
			{
				return Double.valueOf(b.prop).compareTo(a.prop);
			}
		});

		int n = Math.min(top, arr.length);
		String[] words = new String[n];

		for (int i = 0; i < n; i++)
		{
			words[i] = arr[i].word;
		}

		return words;
	}

	private static class WordProp
	{
		public String word;
		public Double prop;

		public WordProp(String word, Double prop)
		{
			this.word = word;
			this.prop = prop;
		}
	}

	public static Frequency getFromCorpus(ICorpus corpus, Integer topic,
			Date date)
	{
		Frequency result = new Frequency();
		IDocument[] documents = corpus.getDocuments();

		for (IDocument document : documents)
			if ((null == topic || topic.equals(document.getTopic()))
					&& (null == date || date.equals(document.getDate())))
				for (String word : document.getWords())
					result.add(word);

		return result;
	}

	public static Frequency getFromCorpusAll(ICorpus corpus, Date date)
	{
		return getFromCorpus(corpus, null, date);
	}
}
