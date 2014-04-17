package net.msusevastopol.math.ypys.hdp;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.msusevastopol.math.ypys.utils.CollectionUtils;
import net.msusevastopol.math.ypys.utils.DateUtils;

public class FrequencyOverWords
{
	private Map<String, Double> prop;

	public FrequencyOverWords(Map<String, Double> prop)
	{
		this.prop = prop;
	}

	public FrequencyOverWords()
	{
		prop = CollectionUtils.newMap();
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
	public FrequencyOverWords diff(FrequencyOverWords p)
	{
		return FrequencyOverWords.diff(this, p);
	}

	private static FrequencyOverWords diff(FrequencyOverWords a,
			FrequencyOverWords b)
	{
		a.normalize();
		b.normalize();

		FrequencyOverWords c = new FrequencyOverWords();
		for (String word : a.prop.keySet())
			c.prop.put(word, -a.prop.get(word) * Math.log(b.prop.get(word)));

		c.normalize();
		return c;
	}

	public String[] getTopWords(int top)
	{
		WordProp[] result = getTopWordsExtended(top);
		String[] strings = new String[result.length];

		for (int i = 0; i < result.length; i++)
			strings[i] = result[i].word;

		return strings;
	}

	public WordProp[] getTopWordsExtended(int top)
	{
		WordProp[] result = new WordProp[prop.size()];
		int index = 0;
		for (String word : prop.keySet())
			result[index++] = new WordProp(word, prop.get(word));

		Arrays.sort(result, new Comparator<WordProp>()
		{

			public int compare(WordProp a, WordProp b)
			{
				return Double.valueOf(b.prop).compareTo(a.prop);
			}
		});

		return Arrays.copyOf(result, Math.min(result.length, top));
	}

	public static FrequencyOverWords getFromCorpus(ICorpus corpus,
			Integer topic, IDate date, int radius)
	{
		FrequencyOverWords result = new FrequencyOverWords();
		List<IDocument> documents = corpus.getDocuments();

		for (IDocument document : documents)
			if ((null == topic || topic.equals(document.getTopic()))
					&& (null == date || inRange(date, document.getDate(),
							radius)))
				for (String word : document.getWords())
					result.add(word);

		return result;
	}

	private static boolean inRange(IDate date, IDate date2, int radius)
	{
		return DateUtils.diffHours(date, date2) <= radius;
	}

	public static FrequencyOverWords getFromCorpusWithTfIdf(ICorpus corpus,
			Integer topic, IDate date, int radius)
	{
		FrequencyOverWords dateSpecific = getFromCorpus(corpus, topic, date,
				radius);
		FrequencyOverWords all = getFromCorpus(corpus, topic, null, 0);

		return dateSpecific.diff(all);
	}
}
