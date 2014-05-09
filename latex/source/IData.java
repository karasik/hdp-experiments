package net.msusevastopol.math.ypys.hdp;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface IData
{
	public void save(String filename) throws IOException, FileNotFoundException;

	public void load(String filename) throws IOException, FileNotFoundException;
}
