package net.msusevastopol.math.ypys.hdp;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
			if (!document.isAdditional()
					&& (null == topic || topic.equals(document.getTopic())))
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

	public double getMean()
	{
		BigDecimal result = BigDecimal.ZERO;
		long sum = 0;
		for (int i = 0; i < frequency.length; i++)
		{
			result = result.add(BigDecimal.valueOf((long) frequency[i] * i));
			sum += frequency[i];
		}
		return result.divide(BigDecimal.valueOf(sum), 50, RoundingMode.HALF_UP)
				.doubleValue();
	}

	public double getSD2()
	{
		double mean = getMean();
		BigDecimal result = BigDecimal.ZERO;
		long sum = 0;
		long max = 0;
		for (int i = 0; i < frequency.length; i++)
		{
			if (frequency[i] <= 50)
				continue;

			BigDecimal tmp = BigDecimal.valueOf(i - mean);
			result = result.add(BigDecimal.valueOf(frequency[i]).multiply(tmp)
					.multiply(tmp));
			sum += frequency[i];
			max = Math.max(frequency[i], max);
		}
		return result.divide(BigDecimal.valueOf(sum), 50, RoundingMode.HALF_UP)
				.doubleValue() / max;
	}

	public Map<IDate, Integer> getMapping()
	{
		return mapping;
	}

	public double normalizedDotProduct(FrequencyOverTime b)
	{
		return this.dotProduct(b) / this.norm() / b.norm();
	}

	private double norm()
	{
		return Math.sqrt(this.dotProduct(this));
	}

	private long dotProduct(FrequencyOverTime b)
	{
		if (this.frequency.length != b.frequency.length)
			throw new IllegalArgumentException();

		long ret = 0;
		for (int i = 0; i < frequency.length; i++)
		{
			ret += frequency[i] * b.frequency[i];
		}
		return ret;
	}
}
