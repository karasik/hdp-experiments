/*
 * Copyright 2011 Arnim Bleier, Andreas Niekler and Patrick Jaehnichen
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 */

package net.msusevastopol.math.ypys.hdp;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.msusevastopol.math.ypys.utils.CollectionUtils;
import net.msusevastopol.math.ypys.utils.ProbUtils;

/**
 * Hierarchical Dirichlet Processes Chinese Restaurant Franchise Sampler
 * 
 * For more information on the algorithm see: Hierarchical Bayesian
 * Nonparametric Models with Applications. Y.W. Teh and M.I. Jordan. Bayesian
 * Nonparametrics, 2010. Cambridge University Press.
 * http://www.gatsby.ucl.ac.uk/~ywteh/research/npbayes/TehJor2010a.pdf
 * 
 * For other known implementations see README.txt
 * 
 * @author <a href="mailto:arnim.bleier+hdp@gmail.com">Arnim Bleier</a>;
 *         Modified by Yukhym Pyshnohraiev.
 * 
 */
public class HDPGibbsSampler implements Serializable
{
	private boolean isDocumentAdditional(int d)
	{
		return corpus.getDocuments().get(d).isAdditional();
	}

	private static final long serialVersionUID = 1L;
	private double beta = 0.5; // default only
	private double gamma = 2.0;
	private double alpha = 1.0;

	private Random random = new Random();
	private double[] p;
	private double[] f;

	private DOCState[] docStates;
	private int[] numberOfTablesByTopic;
	private int[] wordCountByTopic;
	private int[][] wordCountByTopicAndTerm;

	private int sizeOfVocabulary;
	private int numberOfTopics;
	private int totalNumberOfTables;

	private Map<Integer, String> codesToWords;

	private ICorpus corpus;

	public HDPGibbsSampler(ICorpus corpus)
	{
		addInstances(corpus);
	}

	public void run(int maxIter) throws IOException
	{
		run(0, maxIter, System.out);
		setTopicsToCorpus();
	}

	private void addInstances(ICorpus corpus)
	{
		this.corpus = corpus;
		Map<String, Integer> codesAssignment = CollectionUtils.newMap();
		codesToWords = CollectionUtils.newMap();

		int[][] documents = new int[corpus.getDocuments().size()][];
		int code = 0;
		for (int d = 0; d < corpus.getDocuments().size(); d++)
		{
			documents[d] = new int[corpus.getDocuments().get(d).getWords().length];
			for (int w = 0; w < corpus.getDocuments().get(d).getWords().length; w++)
			{
				String word = corpus.getDocuments().get(d).getWords()[w];
				if (codesAssignment.containsKey(word))
				{
					documents[d][w] = codesAssignment.get(word);
				}
				else
				{
					codesAssignment.put(word, code);
					codesToWords.put(code, word);
					documents[d][w] = code++;
				}
			}
		}

		addInstances(documents, code);
	}

	private void setTopicsToCorpus()
	{
		for (int d = 0; d < docStates.length; d++)
			corpus.getDocuments().get(d).setTopic(docStates[d].resolveTopic());
	}

	/**
	 * Initially assign the words to tables and topics
	 * 
	 * @param corpus
	 *            {@link CLDACorpus} on which to fit the model
	 */
	private void addInstances(int[][] documentsInput, int V)
	{
		sizeOfVocabulary = V;
		numberOfTopics = 1;
		docStates = new DOCState[documentsInput.length];
		for (int d = 0; d < documentsInput.length; d++)
		{
			docStates[d] = new DOCState(documentsInput[d], d);
		}
		int k, i, j;
		DOCState docState;
		p = new double[20];
		f = new double[20];
		numberOfTablesByTopic = new int[getNumberOfTopics() + 1];
		wordCountByTopic = new int[getNumberOfTopics() + 1];
		wordCountByTopicAndTerm = new int[getNumberOfTopics() + 1][];
		for (k = 0; k <= getNumberOfTopics(); k++)
			// var initialization done
			wordCountByTopicAndTerm[k] = new int[sizeOfVocabulary];
		for (k = 0; k < getNumberOfTopics(); k++)
		{
			docState = docStates[k];
			for (i = 0; i < docState.documentLength; i++)
				addWord(docState.docID, i, 0, k);
		} // all topics have now one document
		for (j = getNumberOfTopics(); j < docStates.length; j++)
		{
			docState = docStates[j];
			k = random.nextInt(getNumberOfTopics());
			for (i = 0; i < docState.documentLength; i++)
				addWord(docState.docID, i, 0, k);
		} // the words in the remaining documents are now assigned too
	}

	/**
	 * Step one step ahead
	 * 
	 */
	private void nextGibbsSweep()
	{
		for (int d = 0; d < docStates.length; d++)
		{
			for (int i = 0; i < docStates[d].documentLength; i++)
			{
				if (isDocumentAdditional(d))
					sweepForAdditionalDocument(d, i);
				else
					sweepForNormalDocument(d, i);
				// Table
			}
		}
		defragment();
	}

	private void sweepForNormalDocument(int d, int i)
	{
		removeWord(d, i); // remove the word i from the state
		int table = sampleTable(d, i);
		if (table == docStates[d].numberOfTables) // new Table
		{
			int topic = sampleTopic();
			addWord(d, i, table, topic); // sampling its
		}
		else
			addWord(d, i, table, docStates[d].tableToTopic[table]); // existing
	}

	// What we should do for additional document (fictional)
	private void sweepForAdditionalDocument(int d, int i)
	{
		removeWord(d, i); // remove the word i from the state

		int table = 1;
		if (table == docStates[d].numberOfTables) // new Table
		{
			int topic = 1;
			addWord(d, i, table, topic); // sampling its
		}
		else
			addWord(d, i, table, docStates[d].tableToTopic[table]); // existing
	}

	/**
	 * Decide at which topic the table should be assigned to
	 * 
	 * @return the index of the topic
	 */
	private int sampleTopic()
	{
		double u, pSum = 0.0;
		int k;
		p = ensureCapacity(p, getNumberOfTopics());
		for (k = 0; k < getNumberOfTopics(); k++)
		{
			pSum += numberOfTablesByTopic[k] * f[k];
			p[k] = pSum;
		}
		pSum += gamma / sizeOfVocabulary;
		p[getNumberOfTopics()] = pSum;
		u = random.nextDouble() * pSum;
		for (k = 0; k <= getNumberOfTopics(); k++)
			if (u < p[k])
				break;
		return k;
	}

	/**
	 * Decide at which table the word should be assigned to
	 * 
	 * @param docID
	 *            the index of the document of the current word
	 * @param i
	 *            the index of the current word
	 * @return the index of the table
	 */
	private int sampleTable(int docID, int i)
	{
		int k, j;
		double pSum = 0.0, vb = sizeOfVocabulary * beta, fNew, u;
		DOCState docState = docStates[docID];
		f = ensureCapacity(f, getNumberOfTopics());
		p = ensureCapacity(p, docState.numberOfTables);
		fNew = gamma / sizeOfVocabulary;
		for (k = 0; k < getNumberOfTopics(); k++)
		{
			f[k] = (wordCountByTopicAndTerm[k][docState.words[i].termIndex] + beta)
					/ (wordCountByTopic[k] + vb);
			fNew += numberOfTablesByTopic[k] * f[k];
		}
		for (j = 0; j < docState.numberOfTables; j++)
		{
			if (docState.wordCountByTable[j] > 0)
				pSum += docState.wordCountByTable[j]
						* f[docState.tableToTopic[j]];
			p[j] = pSum;
		}
		pSum += alpha * fNew / (totalNumberOfTables + gamma); // Probability for
																// t = tNew
		p[docState.numberOfTables] = pSum;
		u = random.nextDouble() * pSum;
		for (j = 0; j <= docState.numberOfTables; j++)
			if (u < p[j])
				break; // decided which table the word i is assigned to
		return j;
	}

	/**
	 * Method to call for fitting the model.
	 * 
	 * @param doShuffle
	 * @param shuffleLag
	 * @param maxIter
	 *            number of iterations to run
	 * @param saveLag
	 *            save interval
	 * @param wordAssignmentsWriter
	 *            {@link WordAssignmentsWriter}
	 * @param topicsWriter
	 *            {@link TopicsWriter}
	 * @throws IOException
	 */
	private void run(int shuffleLag, int maxIter, PrintStream log)
			throws IOException
	{
		for (int iter = 0; iter < maxIter; iter++)
		{
			if ((shuffleLag > 0) && (iter > 0) && (iter % shuffleLag == 0))
				doShuffle();
			nextGibbsSweep();
			if (iter % 10 == 0)
				log.println("iter = " + iter + " #topics = "
						+ getNumberOfTopics() + ", #tables = "
						+ totalNumberOfTables);
		}
	}

	/**
	 * Removes a word from the bookkeeping
	 * 
	 * @param docID
	 *            the id of the document the word belongs to
	 * @param i
	 *            the index of the word
	 */
	private void removeWord(int docID, int i)
	{
		DOCState docState = docStates[docID];
		int table = docState.words[i].tableAssignment;
		int k = docState.tableToTopic[table];
		docState.wordCountByTable[table]--;
		wordCountByTopic[k]--;
		wordCountByTopicAndTerm[k][docState.words[i].termIndex]--;
		if (docState.wordCountByTable[table] == 0)
		{ // table is removed
			totalNumberOfTables--;
			numberOfTablesByTopic[k]--;
			docState.tableToTopic[table]--;
		}
	}

	/**
	 * Add a word to the bookkeeping
	 * 
	 * @param docID
	 *            docID the id of the document the word belongs to
	 * @param i
	 *            the index of the word
	 * @param table
	 *            the table to which the word is assigned to
	 * @param topic
	 *            the topic to which the word is assigned to
	 */
	private void addWord(int docID, int i, int table, int k)
	{
		DOCState docState = docStates[docID];
		docState.words[i].tableAssignment = table;
		docState.wordCountByTable[table]++;
		wordCountByTopic[k]++;
		wordCountByTopicAndTerm[k][docState.words[i].termIndex]++;
		if (docState.wordCountByTable[table] == 1)
		{ // a new table is created
			docState.numberOfTables++;
			docState.tableToTopic[table] = k;
			totalNumberOfTables++;
			numberOfTablesByTopic[k]++;
			docState.tableToTopic = ensureCapacity(docState.tableToTopic,
					docState.numberOfTables);
			docState.wordCountByTable = ensureCapacity(
					docState.wordCountByTable, docState.numberOfTables);
			if (k == numberOfTopics)
			{ // a new topic is created
				numberOfTopics++;
				numberOfTablesByTopic = ensureCapacity(numberOfTablesByTopic,
						numberOfTopics);
				wordCountByTopic = ensureCapacity(wordCountByTopic,
						numberOfTopics);
				wordCountByTopicAndTerm = add(wordCountByTopicAndTerm,
						new int[sizeOfVocabulary], numberOfTopics);
			}
		}
	}

	/**
	 * Removes topics from the bookkeeping that have no words assigned to
	 */
	private void defragment()
	{
		int[] kOldToKNew = new int[getNumberOfTopics()];
		int k, newNumberOfTopics = 0;
		for (k = 0; k < getNumberOfTopics(); k++)
		{
			if (wordCountByTopic[k] > 0)
			{
				kOldToKNew[k] = newNumberOfTopics;
				swap(wordCountByTopic, newNumberOfTopics, k);
				swap(numberOfTablesByTopic, newNumberOfTopics, k);
				swap(wordCountByTopicAndTerm, newNumberOfTopics, k);
				newNumberOfTopics++;
			}
		}
		numberOfTopics = newNumberOfTopics;
		for (int j = 0; j < docStates.length; j++)
			docStates[j].defragment(kOldToKNew);
	}

	/**
	 * Permute the ordering of documents and words in the bookkeeping
	 */
	private void doShuffle()
	{
		List<DOCState> h = Arrays.asList(docStates);
		Collections.shuffle(h);
		docStates = h.toArray(new DOCState[h.size()]);
		for (int j = 0; j < docStates.length; j++)
		{
			List<WordState> h2 = Arrays.asList(docStates[j].words);
			Collections.shuffle(h2);
			docStates[j].words = h2.toArray(new WordState[h2.size()]);
		}
	}

	private static void swap(int[] arr, int arg1, int arg2)
	{
		int t = arr[arg1];
		arr[arg1] = arr[arg2];
		arr[arg2] = t;
	}

	private static void swap(int[][] arr, int arg1, int arg2)
	{
		int[] t = arr[arg1];
		arr[arg1] = arr[arg2];
		arr[arg2] = t;
	}

	private static double[] ensureCapacity(double[] arr, int min)
	{
		int length = arr.length;
		if (min < length)
			return arr;
		double[] arr2 = new double[min * 2];
		for (int i = 0; i < length; i++)
			arr2[i] = arr[i];
		return arr2;
	}

	private static int[] ensureCapacity(int[] arr, int min)
	{
		int length = arr.length;
		if (min < length)
			return arr;
		int[] arr2 = new int[min * 2];
		for (int i = 0; i < length; i++)
			arr2[i] = arr[i];
		return arr2;
	}

	private static int[][] add(int[][] arr, int[] newElement, int index)
	{
		int length = arr.length;
		if (length <= index)
		{
			int[][] arr2 = new int[index * 2][];
			for (int i = 0; i < length; i++)
				arr2[i] = arr[i];
			arr = arr2;
		}
		arr[index] = newElement;
		return arr;
	}

	public int getNumberOfTopics()
	{
		return numberOfTopics;
	}

	private class DOCState implements Serializable
	{

		private static final long serialVersionUID = 1L;
		public int docID, documentLength, numberOfTables;
		public int[] tableToTopic;
		public int[] wordCountByTable;
		public WordState[] words;

		public DOCState(int[] instance, int docID)
		{
			this.docID = docID;
			numberOfTables = 0;
			documentLength = instance.length;
			words = new WordState[documentLength];
			wordCountByTable = new int[2];
			tableToTopic = new int[2];
			for (int position = 0; position < documentLength; position++)
				words[position] = new WordState(instance[position], -1);
		}

		public void defragment(int[] kOldToKNew)
		{
			int[] tOldToTNew = new int[numberOfTables];
			int t, newNumberOfTables = 0;
			for (t = 0; t < numberOfTables; t++)
			{
				if (wordCountByTable[t] > 0)
				{
					tOldToTNew[t] = newNumberOfTables;
					tableToTopic[newNumberOfTables] = kOldToKNew[tableToTopic[t]];
					swap(wordCountByTable, newNumberOfTables, t);
					newNumberOfTables++;
				}
				else
					tableToTopic[t] = -1;
			}
			numberOfTables = newNumberOfTables;
			for (int i = 0; i < documentLength; i++)
				words[i].tableAssignment = tOldToTNew[words[i].tableAssignment];
		}

		public int resolveTopic()
		{
			int[] topicProp = new int[getNumberOfTopics()];
			for (WordState w : words)
			{
				int table = w.tableAssignment;
				topicProp[tableToTopic[table]]++;
			}
			return ProbUtils.sampleFromProportions(topicProp, random);
		}
	}

	private class WordState implements Serializable
	{
		private static final long serialVersionUID = 1L;
		public int termIndex;
		public int tableAssignment;

		public WordState(int wordIndex, int tableAssignment)
		{
			this.termIndex = wordIndex;
			this.tableAssignment = tableAssignment;
		}
	}

	public List<String> getTopTopicWords(int topic, int top)
	{
		List<String> result = CollectionUtils.newList();
		List<Pair<String, Integer>> words = CollectionUtils.newList();

		for (int i = 0; i < sizeOfVocabulary; i++)
			words.add(new Pair<String, Integer>(codesToWords.get(i),
					wordCountByTopicAndTerm[topic][i]));

		Collections.sort(words, new Comparator<Pair<String, Integer>>()
		{
			public int compare(Pair<String, Integer> a, Pair<String, Integer> b)
			{
				return b.second.compareTo(a.second);
			}
		});

		for (int i = 0; i < Math.min(top, words.size()); i++)
		{
			result.add(words.get(i).first);
		}

		return result;
	}

	public void removeAdditional()
	{
		for (int d = 0; d < docStates.length; d++)
			if (isDocumentAdditional(d))
				for (int i = 0; i < docStates[d].words.length; i++)
					removeWord(d, i);
	}
}