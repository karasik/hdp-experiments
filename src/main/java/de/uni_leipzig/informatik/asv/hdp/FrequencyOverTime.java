package de.uni_leipzig.informatik.asv.hdp;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

public class FrequencyOverTime
{
	private int offset;
	private int[] frequency;
	private Map<IDate, Integer> mapping;

	public FrequencyOverTime(ICorpus corpus, Integer topic)
	{
		IDate first = corpus.getDocuments()[0].getDate();
		offset = first.getDay() * 24 + first.getHour();

		IDate last = corpus.getDocuments()[corpus.getDocuments().length - 1]
				.getDate();
		frequency = new int[DateUtils.toCode(last) + 1];

		for (IDocument document : corpus.getDocuments())
		{
			if (null == topic || topic.equals(document.getTopic()))
				frequency[DateUtils.toCode(document.getDate())]++;
		}

		mapping = CollectionUtils.newMap();
		for (int i = 0; i < frequency.length; i++)
			mapping.put(DateUtils.fromCode(i), frequency[i]);
	}

	public void save(String filename) throws FileNotFoundException
	{
		PrintWriter out = new PrintWriter(filename);

		for (int value : frequency)
			out.println(value);

		out.flush();
		out.close();
	}

	public Map<IDate, Integer> getMapping()
	{
		return mapping;
	}

}
