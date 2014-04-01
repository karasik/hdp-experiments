package de.uni_leipzig.informatik.asv.hdp;

import java.io.IOException;
import java.util.Arrays;

public class Main
{
	private static final int WORDS_IN_ADD_DOC = 100000;
	private static final int TOP_COUNT = 15;
	private static final int FILENAME_TOP_COUNT = 5;
	private static final int HDP_ITERATIONS = 100;

	public static void main(String[] args) throws IOException
	{
		ICorpus corpus = new Corpus(Filename.INPUT_EX);
		// HDPGibbsSampler hdp = new HDPGibbsSampler(corpus);
		// hdp.run(50);
		// corpus.save(Filename.DOCUMENT_TO_TOPIC_ASSIGNMENT);

		FrequencyOverTime freq = new FrequencyOverTime(corpus, null);
		freq.save(Filename.getPlotOutput("all"));

		IDate[] result = new MaxPeakDetector(7, 6).detectPeaks(freq);
		int index = 1;
		for (IDate date : result)
		{
			System.out.println("Found peak #" + index + ": " + date);
			FrequencyOverWords keywords = FrequencyOverWords.getFromCorpus(
					corpus, null, date, 1);

			String[] topWords = keywords.getTopWords(FILENAME_TOP_COUNT);
			System.out.println("Keywords for the peak: "
					+ Arrays.toString(topWords));

			WordProp[] top = keywords.getTopWordsExtended(TOP_COUNT);
			IDocument additionalDocument = Document.getFromProp(top,
					WORDS_IN_ADD_DOC);

			corpus.addAdditionalDocument(additionalDocument);
			HDPGibbsSampler hdp = new HDPGibbsSampler(corpus);
			hdp.run(HDP_ITERATIONS);

			String suffix = date.toShortString();

			corpus.save(Filename.getDocumentsToTopicAssignment(suffix));
			FrequencyOverTime freqOverTime = new FrequencyOverTime(corpus, 0);
			freqOverTime.save(Filename.getPlotOutput(suffix));

			FrequencyOverWords corpusWords = FrequencyOverWords.getFromCorpus(
					corpus, 0, null, 0);
			System.out.println("Topic keywords are "
					+ Arrays.asList(corpusWords.getTopWords(10)));

			corpus.clearAdditionalDocuments();
			index++;
		}
	}

	private static class Filename
	{
		// Prevent instantiation.
		private Filename()
		{
		}

		private static String getPlotOutput(String suffix)
		{
			return append(PLOT_OUTPUT, suffix);
		}

		private static String getDocumentsToTopicAssignment(String suffix)
		{
			return append(DOCUMENT_TO_TOPIC_ASSIGNMENT, suffix);
		}

		private static String append(String filename, String suffix)
		{
			return filename + "-" + suffix + ".txt";
		}

		private static final String INPUT_EX = "data/inputEx.txt";
		private static final String PLOT_OUTPUT = "data/out/plotOutput";
		private static final String DOCUMENT_TO_TOPIC_ASSIGNMENT = "data/out/DocumentToTopicAssignmentEx";
	}
}
