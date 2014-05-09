package net.msusevastopol.math.ypys.utils;

import net.msusevastopol.math.ypys.hdp.WordProp;

public class MiscUtils
{
	// Prevent instantiating.
	private MiscUtils()
	{
	}
	
	public static String implode(String separator, String... elements)
	{
		StringBuilder sb = new StringBuilder();
		if (elements.length > 0)
		{
			sb.append(elements[0]);
			for (int i = 1; i < elements.length; i++)
				sb.append(" ").append(elements[i]);
			return sb.toString();
		}
		return "";
	}

	public static void normalize(WordProp[] prop)
	{
		double sum = 0.0;
		for (WordProp wp : prop)
			sum += wp.prop;
		for (WordProp wp : prop)
			wp.prop /= sum;
	}
}
