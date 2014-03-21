package de.uni_leipzig.informatik.asv.hdp;

public class DateUtils
{
	private static final int OFFSET = 12 + 4 * 24;

	public static int toCode(IDate date)
	{
		return date.getDay() * 24 + date.getHour() - OFFSET;
	}

	public static IDate fromCode(int code)
	{
		code += OFFSET;
		return new Date(code / 24, code % 24);
	}

	public static int diffHours(IDate a, IDate b)
	{
		return Math.abs(toCode(a) - toCode(b));
	}
}
