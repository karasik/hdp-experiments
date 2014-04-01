package de.uni_leipzig.informatik.asv.hdp;

import java.util.List;

public interface ICorpus extends IData
{
	public List<IDocument> getDocuments();
	
	public void addAdditionalDocument(IDocument document);
	
	public void clearAdditionalDocuments();
}
