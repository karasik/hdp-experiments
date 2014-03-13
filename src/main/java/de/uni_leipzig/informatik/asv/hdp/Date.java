package de.uni_leipzig.informatik.asv.hdp;

public class Date implements IDate
{
	private int day;
	private int hour;
	
	public Date(String line)
	{
		day = Integer.parseInt(line.split(" ")[0]);
		hour = Integer.parseInt(line.split(" ")[1]);
	}

	public int getDay()
	{
		return day;
	}

	public int getHour()
	{
		return hour;
	}

	@Override
	public String toString()
	{
		return "Date [day=" + day + ", hour=" + hour + "]";
	}
}
