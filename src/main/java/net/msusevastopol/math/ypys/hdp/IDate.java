package net.msusevastopol.math.ypys.hdp;

import net.msusevastopol.math.ypys.utils.DateUtils;

public interface IDate
{
	public static final IDate ZERO = DateUtils.fromCode(0);

	public int getDay();

	public int getHour();

	public String toShortString();
}
