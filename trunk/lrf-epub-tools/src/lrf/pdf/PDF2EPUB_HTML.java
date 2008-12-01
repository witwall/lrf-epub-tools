package lrf.pdf;

import java.util.Vector;

import lrf.epub.EPUBMetaData;

public class PDF2EPUB_HTML extends EPUBMetaData {
	String title,author,id;
	
	public PDF2EPUB_HTML(String tit, String aut){
		title=tit;
		author=aut;
		id=createRandomIdentifier();
	}
	
	@Override
	public String getCreator() {
		return author;
	}

	@Override
	public String getIdentifier() {
		return id;
	}

	@Override
	public String getLanguage() {
		return "en";
	}

	@Override
	public String getPublisher() {
		return "LRFTools";
	}

	@Override
	public String getRights() {
		return "free";
	}

	@Override
	public Vector<String> getSubject() {
		return new Vector<String>();
	}

	@Override
	public String getTitle() {
		return title;
	}
}
