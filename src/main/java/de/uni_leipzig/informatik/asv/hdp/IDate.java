package de.uni_leipzig.informatik.asv.hdp;

import de.uni_leipzig.informatik.asv.utils.DateUtils;

public interface IDate
{
	public static final IDate ZERO = DateUtils.fromCode(0);

	public int getDay();

	public int getHour();

	public String toShortString();
}
