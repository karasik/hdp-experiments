package de.uni_leipzig.informatik.asv.hdp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Tmp
{
	public static void convertDates() throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream("data/input.txt")));
		PrintWriter out = new PrintWriter("data/input-new.txt");

		while (in.ready())
		{
			String line = in.readLine();
			String[] p = line.split(" \\| ");

			out.format("%s | %s %s | %s\n", p[0], p[1].split(" ")[2],
					p[1].split(" ")[3].split(":")[0], p[2]);

		}

		in.close();
		out.flush();
		out.close();
	}
}
