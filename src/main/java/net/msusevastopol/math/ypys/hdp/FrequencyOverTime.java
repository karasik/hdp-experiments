package net.msusevastopol.math.ypys.hdp;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

import net.msusevastopol.math.ypys.utils.CollectionUtils;
import net.msusevastopol.math.ypys.utils.DateUtils;

public class FrequencyOverTime
{
	private int[] frequency;
	private Map<IDate, Integer> mapping;

	public FrequencyOverTime(ICorpus corpus)
	{
		this(corpus, null);
	}
	
	public FrequencyOverTime(ICorpus corpus, Integer topic)
	{
		IDate last = corpus.getDocuments()
				.get(corpus.getDocuments().size() - 1).getDate();
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
