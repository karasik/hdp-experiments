package de.uni_leipzig.informatik.asv.hdp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import de.uni_leipzig.informatik.asv.utils.CollectionUtils;
import de.uni_leipzig.informatik.asv.utils.MiscUtils;

public class Corpus implements ICorpus
{

	/*
	 * [THEME) ]WORD1 WORD2 ... WORDN | DAY HOUR | AUTHOR
	 */

	private IDocument[] documents;

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
						document.getDate().getHour(), "Dowan");
			else
				out.format("%d) %s | %d %d | %s\n", document.getTopic(),
						MiscUtils.implode(" ", document.getWords()), document
								.getDate().getDay(), document.getDate()
								.getHour(), "Duvessa");
		}

		out.flush();
		out.close();
	}

	@Override
	public void load(String filename) throws IOException, FileNotFoundException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(filename)));

		List<IDocument> documents = CollectionUtils.newList();
		while (in.ready())
		{
			String line = in.readLine();
			String[] words = ParsingUtils.extractWords(line);
			Integer topic = ParsingUtils.extractTheme(line);
			IDate date = ParsingUtils.extractDate(line);

			documents.add(new Document(words, topic, date));
		}

		in.close();

		this.documents = new IDocument[documents.size()];
		for (int i = 0; i < documents.size(); i++)
			this.documents[i] = documents.get(i);
	}

	@Override
	public IDocument[] getDocuments()
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
}
