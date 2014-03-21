package de.uni_leipzig.informatik.asv.hdp;

public interface IDocument
{
	public String[] getWords();

	public Integer getTopic();

	public void setTopic(Integer topic);
	
	public IDate getDate();

}
