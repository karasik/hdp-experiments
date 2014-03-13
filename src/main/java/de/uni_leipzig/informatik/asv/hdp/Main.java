package de.uni_leipzig.informatik.asv.hdp;

import java.io.IOException;
import java.util.Arrays;

public class Main
{
	private static final boolean FORCE_RECALCULATE = true;
	private static final int ITERATIONS = 500;
	private static final String SUFFIX = "Ex";

	private static final int CHUNK_SIZE = 3000;
	private static final int OBSERVING_THEME = -1;
	private static final String INPUT_EX = "data/inputEx.txt";

//	static
//	{
//		try
//		{
//			initFrequencyAll();
//		}
//		catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public static void main(String[] args) throws IOException
	{
		ICorpus corpus = new Corpus(INPUT_EX);
		Frequency freq = Frequency.getFromCorpusAll(corpus, null);
		
		System.out.println(Arrays.toString(freq.getTopWords(10)));
	}

	
//	
//	private static void initFrequencyAll() throws IOException
//	{
//		String input = "inputEx.txt";
//		BufferedReader in = new BufferedReader(new InputStreamReader(
//				new FileInputStream("data/" + input)));
//
//		List<Integer> all = CollectionUtils.newList();
//
//		while (in.ready())
//		{
//			String line = in.readLine();
//			Integer day = ParsingUtils.extractDay(line), hour = ParsingUtils
//					.extractHour(line);
//			Integer code = new DayHour(day, hour).toCode();
//
//			if (code == all.size())
//				all.add(0);
//			else
//				all.set(all.size() - 1, all.get(all.size() - 1) + 1);
//		}
//
//		in.close();
//
//		totalHours = all.size();
//		freqAll = new int[totalHours];
//		for (int i=0; i<all.size(); i++)
//	}
//
//	private static int getMean()
//	{
//		int total = 0;
//		for (Integer count : freqAll)
//			total += count;
//		return 1 + total / totalHours;
//	}
//
//	private static void initFrequency() throws IOException
//	{
//		String input = "DocumentToTopicAssignmentEx.txt";
//		BufferedReader in = new BufferedReader(new InputStreamReader(
//				new FileInputStream("data/" + input)));
//
//		Map<Integer, Map<Integer, Integer>> all = CollectionUtils.newMap();
//
//		while (in.ready())
//		{
//			String line = in.readLine();
//			Integer day = ParsingUtils.extractDay(line), hour = ParsingUtils
//					.extractHour(line), theme = ParsingUtils.extractTheme(line);
//			Integer code = new DayHour(day, hour).toCode();
//
//			CollectionUtils.initMap(all, code,
//					CollectionUtils.<Integer, Integer> newMap());
//			CollectionUtils.initMap(all.get(code), theme, 0);
//			all.get(code).put(theme, 1 + all.get(code).get(theme));
//		}
//
//		in.close();
//
//		totalHours = all.size();
//
//		freq = new Map[totalHours];
//		freqAll = new int[totalHours];
//		for (Integer code : all.keySet())
//		{
//			freq[code] = all.get(code);
//			Integer totalCount = 0;
//			for (Integer count : all.get(code).values())
//				totalCount += count;
//			freqAll[code] = totalCount;
//		}
//	}
//
//	private static Proportion getProportion() throws IOException
//	{
//		return getProportion(null, null);
//	}
//
//	private static Proportion getProportion(Integer targetDay,
//			Integer targetHour) throws IOException
//	{
//		String input = "DocumentToTopicAssignmentEx.txt";
//		BufferedReader in = new BufferedReader(new InputStreamReader(
//				new FileInputStream("data/" + input)));
//
//		Proportion ret = new Proportion();
//
//		while (in.ready())
//		{
//			String line = in.readLine();
//			Integer day = ParsingUtils.extractDay(line), hour = ParsingUtils
//					.extractHour(line);
//			if (targetDay == null && targetHour == null
//					|| (targetDay == day && targetHour == hour))
//			{
//				for (String word : ParsingUtils.extractWords(line))
//					ret.add(word);
//			}
//
//		}
//
//		in.close();
//
//		return ret;
//	}
//
//	private static void outputTop() throws IOException
//	{
//		printTopWordsByTheme(OBSERVING_THEME, 16, 15, "data/topWordsByTheme"
//				+ OBSERVING_THEME + "Peak.txt",
//				"DocumentToTopicAssignmentEx.txt");
//	}
//
//	private static void observeTheme() throws IOException
//	{
//		outputTopicByDay(OBSERVING_THEME, "plotInput.txt", "plotOutput"
//				+ OBSERVING_THEME + ".txt");
//	}
//
//	private static void runModel(CLDACorpus corpus, 
//			HDPGibbsSampler hdp) throws FileNotFoundException, IOException
//	{
//		String hdpFile = "data/model" + SUFFIX + ".bin";
//		String inputFile = "data/input" + SUFFIX + ".txt";
//
//		corpus = new CLDACorpus(new FileInputStream(inputFile));
//		try
//		{
//			if (FORCE_RECALCULATE)
//				throw new Exception();
//			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
//					hdpFile));
//			hdp = (HDPGibbsSampler) in.readObject();
//			in.close();
//		}
//		catch (Exception e)
//		{
//			hdp = new HDPGibbsSampler();
//			hdp.addInstances(corpus.getDocuments(), corpus.getVocabularySize(),
//					corpus.getDateList(), corpus.getAuthorList());
//			hdp.run(0, ITERATIONS, System.out);
//			ObjectOutputStream out = new ObjectOutputStream(
//					new FileOutputStream(hdpFile));
//			out.writeObject(hdp);
//			out.close();
//		}
//
//		printTopWordsByTopicFile(hdp, corpus, "data/TopWordsByTopic" + SUFFIX
//				+ ".txt");
//		printDocumentToTopicAssignment(hdp, corpus,
//				"data/DocumentToTopicAssignment" + SUFFIX + ".txt", true);
//	}
//
//	private static void outputTopicByDay(Integer topic, String input,
//			String output) throws IOException
//	{
//		BufferedReader in = new BufferedReader(new InputStreamReader(
//				new FileInputStream("data/" + input)));
//		PrintWriter out = new PrintWriter("data/" + output);
//
//		int maxCount = 0;
//		DayHour maxDayHour = null;
//
//		for (int i = 0; i < totalHours; i++)
//		{
//			int count;
//			if (topic == -1)
//				count = freqAll[i];
//			else
//				count = freq[i].get(topic);
//
//			if (count > maxCount)
//			{
//				maxCount = count;
//				maxDayHour = new DayHour(i);
//			}
//		}
//
//		System.out.println(maxDayHour.day + " " + maxDayHour.hour);
//
//		out.flush();
//		in.close();
//		out.close();
//	}
//
//	private static void printTopWordsByTheme(int observingTheme, int maxDay,
//			int maxHour, String topWordsByTheme, String inputFile)
//			throws IOException
//	{
//		System.out.println("printing " + topWordsByTheme);
//		PrintStream file = new PrintStream(topWordsByTheme);
//		BufferedReader in = new BufferedReader(new InputStreamReader(
//				new FileInputStream("data/" + inputFile)));
//		int top = 15;
//
//		HashMap<String, Integer> words = new HashMap<String, Integer>();
//
//		while (in.ready())
//		{
//			String[] line = in.readLine().split(" \\| ");
//			String day = line[1].split(" ")[2];
//			String hour = line[1].split(" ")[3].split(":")[0];
//
//			if (Integer.parseInt(day) == maxDay
//					&& Integer.parseInt(hour) == maxHour)
//			{
//				String theme = line[0].split("\\)")[0];
//				if (-1 == observingTheme || theme.equals(observingTheme + ""))
//				{
//					String[] wordsInDocument = line[0].split("\\)")[1].trim()
//							.split(" ");
//					for (String word : wordsInDocument)
//					{
//						if (words.containsKey(word))
//							words.put(word, words.get(word) + 1);
//						else
//							words.put(word, 1);
//					}
//				}
//			}
//		}
//
//		WordCount[] count = new WordCount[words.size()];
//		int index = 0;
//		for (String word : words.keySet())
//			count[index++] = new WordCount(0, 0, words.get(word), word);
//
//		Arrays.sort(count, new Comparator<WordCount>()
//		{
//
//			public int compare(WordCount o1, WordCount o2)
//			{
//				return o2.prop - o1.prop;
//			}
//		});
//
//		for (int i = 0; i < top; i++)
//		{
//			System.out.print(count[i].sword + " ");
//		}
//		System.out.println();
//
//		file.close();
//		System.out.println("done");
//	}
//
//	private static void outputTopic(String topic, String input, String output)
//			throws IOException
//	{
//		BufferedReader in = new BufferedReader(new InputStreamReader(
//				new FileInputStream("data/" + input)));
//		PrintWriter out = new PrintWriter("data/" + output);
//
//		int[] themeIndicators = new int[CHUNK_SIZE];
//		int index = 0, count = 0;
//		while (in.ready())
//		{
//			themeIndicators[index] = in.readLine().equals(topic) ? 1 : 0;
//			count += themeIndicators[index];
//			index++;
//			if (index == CHUNK_SIZE)
//				break;
//		}
//
//		if (index == CHUNK_SIZE)
//		{
//			index = 0;
//			while (in.ready())
//			{
//				out.print(count + " ");
//				count -= themeIndicators[index];
//				themeIndicators[index] = in.readLine().equals(topic) ? 1 : 0;
//				count += themeIndicators[index];
//				index++;
//				if (index == CHUNK_SIZE)
//					index = 0;
//			}
//		}
//
//		out.println();
//		out.flush();
//		in.close();
//		out.close();
//	}
//
//	private static void printDocumentToTopicAssignment(HDPGibbsSampler hdp,
//			CLDACorpus corpus, String documentToTopicAssignment,
//			boolean printDate) throws IOException
//	{
//		System.out.println("printing " + documentToTopicAssignment);
//		PrintStream file = new PrintStream(documentToTopicAssignment);
//		for (DOCState d : hdp.docStates)
//		{
//			file.print(d.resolveTopic() + ") ");
//
//			for (WordState w : d.words)
//				file.print(corpus.getWordStrings()[w.termIndex] + " ");
//			if (printDate)
//				file.print("| " + d.date);
//			file.println();
//		}
//		file.close();
//		System.out.println("done");
//	}
//
//	private static class WordCount implements Serializable
//	{
//		private static final long serialVersionUID = 1L;
//		public int word;
//		public double prob;
//		public int prop;
//		public String sword;
//
//		public WordCount(int word, double prob, int prop, String sword)
//		{
//			this.word = word;
//			this.prob = prob;
//			this.prop = prop;
//			this.sword = sword;
//		}
//	}
//
//	private static void printTopWordsByTopicFile(HDPGibbsSampler hdp,
//			CLDACorpus corpus, String topWordsByTopic) throws IOException
//	{
//		System.out.println("printing " + topWordsByTopic);
//		PrintStream file = new PrintStream(topWordsByTopic);
//		int top = 10;
//		for (int t = 0; t < hdp.numberOfTopics; t++)
//		{
//			WordCount[] words = new WordCount[hdp.sizeOfVocabulary];
//			for (int w = 0; w < hdp.sizeOfVocabulary; w++)
//				words[w] = new WordCount(w,
//						((double) hdp.wordCountByTopicAndTerm[t][w])
//								/ hdp.wordCountByTopic[t], 0, null);
//
//			Arrays.sort(words, new Comparator<WordCount>()
//			{
//				public int compare(WordCount a, WordCount b)
//				{
//					return Double.valueOf(b.prob).compareTo(a.prob);
//				}
//			});
//
//			file.print(t + ": ");
//			for (int w = 0; w < top; w++)
//			{
//				file.print(String.format("%s(%.4f) ",
//						corpus.getWordStrings()[words[w].word], words[w].prob));
//			}
//			file.println();
//		}
//		file.close();
//		System.out.println("done");
//	}
//
//	private static class DayHour
//	{
//		private int day;
//		private int hour;
//
//		public DayHour(int day, int hour)
//		{
//			this.day = day;
//			this.hour = hour;
//		}
//
//		public DayHour(String day, String hour)
//		{
//			this(Integer.parseInt(day), Integer.parseInt(hour));
//		}
//
//		public DayHour(int code)
//		{
//			DayHour dh = DayHour.fromCode(code);
//			this.day = dh.day;
//			this.hour = dh.hour;
//		}
//
//		public int toCode()
//		{
//			return (day * 24 + hour) - (4 * 24 + 12);
//		}
//
//		public static DayHour fromCode(int code)
//		{
//			code += 12 + 24 * 4;
//			return new DayHour(code / 24, code % 24);
//		}
//	}
//	
}
