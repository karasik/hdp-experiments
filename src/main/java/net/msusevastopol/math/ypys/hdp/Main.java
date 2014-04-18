package net.msusevastopol.math.ypys.hdp;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main
{
	private static final int WORDS_IN_ADD_DOC = 50;
	private static final int KEYWORDS_TO_PRINT = 50;
	private static final int ADD_DOCS = 10000;
	private static final int TOP_COUNT = 50;
	private static final int TARGET_TOPIC = 1;
	private static final int FILENAME_TOP_COUNT = 50;
	private static final int HDP_ITERATIONS = 300;

	public static void main(String[] args) throws IOException
	{
		ICorpus corpus = new Corpus(Filename.INPUT_EX);
		// HDPGibbsSampler hdp = new HDPGibbsSampler(corpus);
		// hdp.run(50);
		// corpus.save(Filename.DOCUMENT_TO_TOPIC_ASSIGNMENT);

		FrequencyOverTime freq = new FrequencyOverTime(corpus);
		freq.save(Filename.getPlotOutput("all"));

		IDate[] result = new MaxPeakDetector(15, 9).detectPeaks(freq);
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

			for (int i = 0; i < ADD_DOCS; i++)
				corpus.addAdditionalDocument(additionalDocument);
			HDPGibbsSampler hdp = new HDPGibbsSampler(corpus);

			hdp.run(HDP_ITERATIONS);
			String suffix = date.toShortString();
			corpus.save(Filename.getDocumentsToTopicAssignment(suffix));
			
			hdp.removeAdditional();

			List<String> topTopicWords = hdp.getTopTopicWords(1,
					KEYWORDS_TO_PRINT);

			System.out.println("Topic  keywords are: " + topTopicWords);

			FrequencyOverTime freqOverTime = new FrequencyOverTime(corpus,
					TARGET_TOPIC);

			freqOverTime.save(Filename.getPlotOutput(suffix + "-"
					+ TARGET_TOPIC));
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
