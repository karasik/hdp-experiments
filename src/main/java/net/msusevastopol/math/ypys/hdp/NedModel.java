package net.msusevastopol.math.ypys.hdp;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.msusevastopol.math.ypys.utils.CollectionUtils;

public class NedModel
{
	private static int N_LAST = 1000;
	private static double TRESHOLD = 0.3;

	private int[][] documents;
	private int vocabularySize;
	private ICorpus corpus;

	private Map<Integer, String> codeToWord = CollectionUtils.newMap();
	private Map<String, Integer> wordToCode = CollectionUtils.newMap();

	private int nLast;
	private double treshold;

	public NedModel(ICorpus corpus, int nLast, double treshold)
	{
		this.nLast = nLast;
		this.treshold = treshold;
		this.corpus = corpus;

		documents = new int[corpus.getDocuments().size()][];
		vocabularySize = 0;
		for (int d = 0; d < documents.length; d++)
		{
			int documentLength = corpus.getDocuments().get(d).getWords().length;
			documents[d] = new int[documentLength];
			for (int w = 0; w < documentLength; w++)
			{
				String word = corpus.getDocuments().get(d).getWords()[w];
				if (wordToCode.containsKey(word))
				{
					documents[d][w] = wordToCode.get(word);
				}
				else
				{
					wordToCode.put(word, vocabularySize);
					codeToWord.put(vocabularySize, word);
					vocabularySize++;
				}
			}
		}
	}

	public NedModel(ICorpus corpus)
	{
		this(corpus, N_LAST, TRESHOLD);
	}

	public IDocument[] run()
	{
		List<IDocument> result = CollectionUtils.newList();
		int[] df = new int[vocabularySize];
		Map<Integer, Double>[] weights = new Map[documents.length];
		for (int d = 0; d < documents.length; d++)
		{
			Map<Integer, Double> weight = CollectionUtils.newMap();
			double z = 0;

			Set<Integer> words = CollectionUtils.newSet();
			for (int word : documents[d])
				words.add(word);

			for (int word : words)
			{
				int tf = 0;
				for (int w1 = 0; w1 < documents[d].length; w1++)
					if (word == documents[d][w1])
						tf++;

				df[word]++;

				double tfidf = tf * Math.log(d / df[word]);
				weight.put(word, tfidf);
				z += tfidf * tfidf;
			}

			z = Math.sqrt(z);
			for (int word : weight.keySet())
				weight.put(word, weight.get(word) / z);

			weights[d] = weight;

			if (d < nLast)
				continue;
			boolean isNew = true;

			for (int q = d - nLast; q < d; q++)
			{
				double dotProd = 0;
				Set<Integer> sharedWords = CollectionUtils.newSet();
				for (int w : documents[d])
					sharedWords.add(w);
				for (int w : documents[q])
					sharedWords.add(w);

				for (Integer word : sharedWords)
				{
					double dv = CollectionUtils.getDefault(weights[d], word,
							0.0);
					double qv = CollectionUtils.getDefault(weights[q], word,
							0.0);

					dotProd += dv * qv;
				}

				if (1 - dotProd > treshold)
				{
					isNew = false;
					break;
				}
			}

			if (isNew)
			{
				result.add(corpus.getDocuments().get(d));
				System.out.println(corpus.getDocuments().get(d));
			}
		}

		return null;
	}
}
