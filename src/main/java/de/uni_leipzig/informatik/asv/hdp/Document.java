package de.uni_leipzig.informatik.asv.hdp;

import java.util.Arrays;

public class Document implements IDocument
{
	private String[] words;
	private Integer topic;
	private IDate date;

	public Document(String[] words, Integer topic, IDate date)
	{
		this.words = words;
		this.setTopic(topic);
		this.date = date;
	}

	public Document(String[] words, IDate date)
	{
		this(words, null, date);
	}

	public String[] getWords()
	{
		return words;
	}

	public Integer getTopic()
	{
		return topic;
	}
	
	public void setTopic(Integer topic)
	{
		this.topic = topic;
	}

	public IDate getDate()
	{
		return date;
	}

	@Override
	public String toString()
	{
		return "Document [words=" + Arrays.toString(words) + ", topic=" + getTopic()
				+ ", date=" + date + "]";
	}
}
