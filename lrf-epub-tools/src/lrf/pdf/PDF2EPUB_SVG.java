package lrf.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import lrf.epub.EPUBMetaData;

public class PDF2EPUB_SVG extends EPUBMetaData {
	String title,author,id;
	
	public PDF2EPUB_SVG(String tit, String aut, String lang){
		super(lang);
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

	public void addPage(int i, int width, int height) throws IOException{
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		PrintWriter pw=new PrintWriter(baos);
		width=600;
		height=800;
		pw.print(
			"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">"+
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">"+
            "<head>"+
             "<title>Pagina "+i+"</title>"+
            "</head>"+
            "<body>"+
            "<img src=\"images/"+i+".svg\" width=\"600\" height=\"800\" alt=\"svg\"/>"+
            "</body>");
		pw.close();
		processFile(baos, "page"+i+".html");
	}
}
