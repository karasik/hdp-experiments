package net.msusevastopol.math.ypys.hdp;

import java.io.IOException;
import java.io.PrintWriter;
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
	private static final double SD2_TRESHOLD = 3.0;
	private static final double DOT_PROD_TRESHOLD = 0.95;

	public static void main(String[] args) throws IOException
	{
		PrintWriter log = new PrintWriter(System.out, true);
		ICorpus corpus = new Corpus(Filename.INPUT_EX);
		FrequencyOverTime freq = new FrequencyOverTime(corpus);
//		NedModel ned = new NedModel(corpus, log);
//		
//		ICorpus nedResult = ned.run();
//		nedResult.save(Filename.NED_FILE);
		

		int n = 30;
		IDate[] result = new MaxPeakDetector(n, 9).detectPeaks(freq);
		int index = 1;
		FrequencyOverTime[] results = new FrequencyOverTime[n];
		for (IDate date : result)
		{
			log.println("Found peak #" + index + ": " + date);
			FrequencyOverWords keywords = FrequencyOverWords.getFromCorpus(
					corpus, null, date, 0);

			String[] topWords = keywords.getTopWords(FILENAME_TOP_COUNT);
			log.println("Keywords for the peak: " + Arrays.toString(topWords));

			WordProp[] top = keywords.getTopWordsExtended(TOP_COUNT);
			IDocument additionalDocument = Document.getFromProp(top,
					WORDS_IN_ADD_DOC);

			for (int i = 0; i < ADD_DOCS; i++)
				corpus.addAdditionalDocument(additionalDocument);
			HDPGibbsSampler hdp = new HDPGibbsSampler(corpus, log);
			hdp.run(HDP_ITERATIONS);
			String suffix = "" + index;
			corpus.save(Filename.getDocumentsToTopicAssignment(suffix));

			hdp.removeAdditional();

			List<String> topTopicWords = hdp.getTopTopicWords(TARGET_TOPIC,
					KEYWORDS_TO_PRINT);

			log.println("Topic  keywords are: " + topTopicWords);

			FrequencyOverTime freqOverTime = new FrequencyOverTime(corpus,
					TARGET_TOPIC);

			freqOverTime.save(Filename.getPlotOutput(suffix + "-"
					+ TARGET_TOPIC));

			double sd2 = freqOverTime.getSD2();
			log.println("SD^2: " + sd2);
			if (sd2 > SD2_TRESHOLD)
				log.println("Note: sd > sd_0");

			corpus.clearAdditionalDocuments();

			double authorProp = corpus
					.printUniqueAuthorsProportionForTopic(TARGET_TOPIC);
			log.println("Unique authors proportion: " + authorProp);

			results[index - 1] = freqOverTime;

			index++;
		}

		for (int i = 0; i < results.length; i++)
		{
			for (int j = 0; j < i; j++)
			{
				double value = results[i].normalizedDotProduct(results[j]);
				log.println(i + " " + j + " " + value);
				if (value > DOT_PROD_TRESHOLD)
					log.println("Note: events " + i + " and " + j
							+ " are simular");
			}
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
		public static final String NED_FILE = "data/out/NedAssignment.txt";
		public static final String TMP_CORPUS = "data/out/TmpAssignment.txt";
		public static final String TMP_CORPUS2 = "data/out/TmpAssignment2.txt";
	}
}
