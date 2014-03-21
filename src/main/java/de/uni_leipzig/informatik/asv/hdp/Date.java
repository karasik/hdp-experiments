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

	public Date(int day, int hour)
	{
		this.day = day;
		this.hour = hour;
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

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result + hour;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Date other = (Date) obj;
		if (day != other.day)
			return false;
		if (hour != other.hour)
			return false;
		return true;
	}

}
