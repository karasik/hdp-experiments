package de.uni_leipzig.informatik.asv.hdp;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.informatik.asv.utils.CollectionUtils;
import de.uni_leipzig.informatik.asv.utils.DateUtils;

public class MaxPeakDetector implements IPeakDetector
{
	private int numberOfPeaks;
	private int tresholdRadius;

	public MaxPeakDetector(int numberOfPeaks, int tresholdRadius)
	{
		this.numberOfPeaks = numberOfPeaks;
		this.tresholdRadius = tresholdRadius;
	}

	public IDate[] detectPeaks(FrequencyOverTime freq)
	{
		Map<IDate, Integer> mapping = freq.getMapping();
		List<Pair<IDate, Integer>> list = CollectionUtils.newList();
		for (IDate date : mapping.keySet())
			list.add(new Pair<IDate, Integer>(date, mapping.get(date)));

		Collections.sort(list, new Comparator<Pair<IDate, Integer>>()
		{
			public int compare(Pair<IDate, Integer> a, Pair<IDate, Integer> b)
			{
				return b.second - a.second;
			}
		});

		List<IDate> result = CollectionUtils.newList();
		for (Pair<IDate, Integer> pair : list)
		{
			if (result.size() >= numberOfPeaks)
				break;

			IDate date = pair.first;
			boolean add = true;
			for (IDate rdate : result)
			{
				if (DateUtils.diffHours(date, rdate) <= tresholdRadius)
				{
					add = false;
					break;
				}
			}

			if (add)
				result.add(date);
		}

		return result.toArray(new IDate[0]);
	}
}
