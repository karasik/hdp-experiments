package de.uni_leipzig.informatik.asv.utils;

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
}
