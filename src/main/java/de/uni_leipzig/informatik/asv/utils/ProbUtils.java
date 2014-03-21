package de.uni_leipzig.informatik.asv.utils;

import java.util.Random;

public class ProbUtils
{
	// Prevent instantiating.
	private ProbUtils()
	{
	}

	public static int sampleFromProportions(int[] prop, Random r)
	{
		if (prop == null || prop.length == 0)
			throw new IllegalArgumentException(
					"Array must be not-null and its size must be greater than zero");

		int sample = r.nextInt(sum(prop));
		for (int i = 0, temp = 0; i < prop.length; i++)
		{
			temp += prop[i];
			if (temp > sample)
				return i;
		}

		throw new IllegalStateException("This will not happen");
	}

	private static int sum(int[] prop)
	{
		int sum = 0;
		for (int i : prop)
			sum += i;
		return sum;
	}
}
