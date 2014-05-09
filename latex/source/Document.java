package net.msusevastopol.math.ypys.hdp;

import java.util.Arrays;
import java.util.List;

import net.msusevastopol.math.ypys.utils.CollectionUtils;
import net.msusevastopol.math.ypys.utils.MiscUtils;

public class Document implements IDocument
{
	private String[] words;
	private Integer topic;
	private IDate date;
	private String author;
	private static String ADDITIONAL = "Additional";

	public Document(String[] words, Integer topic, IDate date, String author)
	{
		this.words = words;
		this.setTopic(topic);
		this.date = date;
		this.author = author;
	}

	public Document(String[] words, IDate date)
	{
		this(words, null, date, "Default");
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
		return "Document [words=" + Arrays.toString(words) + ", topic="
				+ getTopic() + ", date=" + date + "]";
	}

	public static IDocument getFromProp(WordProp[] prop, int length)
	{
		MiscUtils.normalize(prop);
		List<String> words = CollectionUtils.newList();
		for (WordProp wp : prop)
		{
			int amount = (int) Math.round(wp.prop * length);
			for (int i = 0; i < amount; i++)
				words.add(wp.word);
		}

		return new Document(words.toArray(new String[0]), null, IDate.ZERO,
				ADDITIONAL);
	}

	@Override
	public boolean isAdditional()
	{
		return ADDITIONAL.equals(author);
	}

	public String getAuthor()
	{
		return author;
	}
}
