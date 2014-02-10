package de.uni_leipzig.informatik.asv.hdp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import de.uni_leipzig.informatik.asv.hdp.HDPGibbsSampler.DOCState;
import de.uni_leipzig.informatik.asv.hdp.HDPGibbsSampler.WordState;
import de.uni_leipzig.informatik.asv.utils.CLDACorpus;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		final boolean FORCE_RECALCULATE = true;
		final int ITERATIONS = 300;
		final String SUFFIX = "Ex";
		// =====================================================

		String hdpFile = "data/model" + SUFFIX + ".bin";
		String inputFile = "data/input" + SUFFIX + ".txt";

		HDPGibbsSampler hdp = null;
		CLDACorpus corpus = new CLDACorpus(new FileInputStream(inputFile));
		try
		{
			if (FORCE_RECALCULATE)
				throw new Exception();
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
					hdpFile));
			hdp = (HDPGibbsSampler) in.readObject();
			in.close();
		}
		catch (Exception e)
		{
			hdp = new HDPGibbsSampler();
			hdp.addInstances(corpus.getDocuments(), corpus.getVocabularySize());
			hdp.run(0, ITERATIONS, System.out);
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(hdpFile));
			out.writeObject(hdp);
			out.close();
		}

		String topWordsByTopic = "data/TopWordsByTopic.txt";
		String documentToTopicAssignment = "data/DocumentToTopicAssignment.txt";

		PrintStream file = new PrintStream(topWordsByTopic);
		int top = 10;
		for (int t = 0; t < hdp.numberOfTopics; t++)
		{
			WordCount[] words = new WordCount[hdp.sizeOfVocabulary];
			for (int w = 0; w < hdp.sizeOfVocabulary; w++)
				words[w] = new WordCount(w,
						((double) hdp.wordCountByTopicAndTerm[t][w])
								/ hdp.wordCountByTopic[t]);

			Arrays.sort(words, new Comparator<WordCount>()
			{
				public int compare(WordCount a, WordCount b)
				{
					return Double.valueOf(b.prob).compareTo(a.prob);
				}
			});

			file.print(t + ": ");
			for (int w = 0; w < top; w++)
			{
				file.print(corpus.getWordStrings()[words[w].word] + "("
						+ words[w].prob + ") ");
			}
			file.println();
		}
		file.close();

		file = new PrintStream(documentToTopicAssignment);
		for (DOCState d : hdp.docStates)
		{
			file.print(d.resolveTopic() + ") ");

			for (WordState w : d.words)
				file.print(corpus.getWordStrings()[w.termIndex] + " ");
			file.println();
		}
		file.close();
	}

	private static class WordCount implements Serializable
	{
		public int word;
		public double prob;

		public WordCount(int word, double prob)
		{
			this.word = word;
			this.prob = prob;
		}
	}
}
