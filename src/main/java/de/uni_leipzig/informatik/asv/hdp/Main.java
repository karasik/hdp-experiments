package de.uni_leipzig.informatik.asv.hdp;

import java.io.IOException;
import java.util.Arrays;

public class Main
{

	public static void main(String[] args) throws IOException
	{
		ICorpus corpus = new Corpus(Filename.INPUT_EX);
		// HDPGibbsSampler hdp = new HDPGibbsSampler(corpus);
		// hdp.run(50);
		// corpus.save(Filename.DOCUMENT_TO_TOPIC_ASSIGNMENT);

		System.out.println(Arrays.toString(FrequencyOverWords.getFromCorpusWithTfIdf(
				corpus, null, new Date(26, 05), 1).getTopWords(15)));

		FrequencyOverTime freq = new FrequencyOverTime(corpus, null);
		freq.save(Filename.PLOT_OUTPUT);

		IDate[] result = new MaxPeakDetector(7, 6).detectPeaks(freq);
		for (IDate date : result)
		{
			System.out.println(date);
			FrequencyOverWords keywords = FrequencyOverWords.getFromCorpus(
					corpus, null, date, 1);
			System.out.println(Arrays.toString(keywords.getTopWords(15)));
		}
	}

	private static class Filename
	{
		// Prevent instantiation.
		private Filename()
		{
		}

		private static final String INPUT_EX = "data/inputEx.txt";
		private static final String PLOT_OUTPUT = "data/plotOutput.txt";
		private static final String DOCUMENT_TO_TOPIC_ASSIGNMENT = "data/DocumentToTopicAssignmentEx.txt";
	}
}
