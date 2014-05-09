package net.msusevastopol.math.ypys.hdp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.msusevastopol.math.ypys.utils.CollectionUtils;
import net.msusevastopol.math.ypys.utils.MiscUtils;

public class Corpus implements ICorpus
{

	/*
	 * [THEME) ]WORD1 WORD2 ... WORDN | DAY HOUR | AUTHOR
	 */

	private List<IDocument> documents = CollectionUtils.newList();

	public Corpus()
	{
	}

	public Corpus(String filename)
	{
		try
		{
			load(filename);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void save(String filename) throws IOException, FileNotFoundException
	{
		PrintWriter out = new PrintWriter(filename);

		for (IDocument document : documents)
		{
			if (document.getTopic() == null)
				out.format("%s | %d %d | %s\n", MiscUtils.implode(" ",
						document.getWords()), document.getDate().getDay(),
						document.getDate().getHour(), document.getAuthor());
			else
				out.format("%d) %s | %d %d | %s\n", document.getTopic(),
						MiscUtils.implode(" ", document.getWords()), document
								.getDate().getDay(), document.getDate()
								.getHour(), document.getAuthor());
		}

		out.flush();
		out.close();
	}

	@Override
	public void load(String filename) throws IOException, FileNotFoundException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(filename)));

		while (in.ready())
		{
			String line = in.readLine();
			String[] words = ParsingUtils.extractWords(line);
			Integer topic = ParsingUtils.extractTheme(line);
			IDate date = ParsingUtils.extractDate(line);
			String author = ParsingUtils.extractAuthor(line);

			documents.add(new Document(words, topic, date, author));
		}

		in.close();
	}

	@Override
	public List<IDocument> getDocuments()
	{
		return documents;
	}

	private static class ParsingUtils
	{
		// Prevent instantiating.
		private ParsingUtils()
		{
		}

		public static IDate extractDate(String line)
		{
			return new Date(line.split(" \\| ")[1]);
		}

		public static String extractAuthor(String line)
		{
			return line.split(" \\| ")[2];
		}

		public static String[] extractWords(String line)
		{
			if (line.indexOf(")") >= 0)
				return line.split("\\)")[1].split(" \\| ")[0].trim().split(" ");
			return line.split(" \\| ")[0].trim().split(" ");
		}

		public static Integer extractTheme(String line)
		{
			if (line.indexOf(")") >= 0)
				return Integer.parseInt(line.split("\\)")[0]);
			return null;
		}
	}

	@Override
	public void addAdditionalDocument(IDocument document)
	{
		documents.add(0, document);
	}

	@Override
	public void clearAdditionalDocuments()
	{
		for (Iterator<IDocument> it = documents.iterator(); it.hasNext();)
		{
			IDocument document = it.next();
			if (document.isAdditional())
				it.remove();
			else
				break;
		}
	}

	@Override
	public double printUniqueAuthorsProportionForTopic(Integer topic)
	{
		int documentsCount = 0;
		Set<String> authors = CollectionUtils.newSet();
		for (IDocument document : this.getDocuments())
		{
			if (topic == null || topic.equals(document.getTopic()))
			{
				authors.add(document.getAuthor());
				documentsCount++;
			}
		}

		return ((double) authors.size()) / documentsCount;
	}
}
