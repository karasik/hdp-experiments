package net.msusevastopol.math.ypys.hdp;

public interface IDocument
{
	public String[] getWords();

	public Integer getTopic();

	public void setTopic(Integer topic);
	
	public IDate getDate();
	
	public boolean isAdditional();
	
	public String getAuthor();
}
