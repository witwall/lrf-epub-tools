package lrf.epub;

import java.util.Vector;

public class Content extends XMLDoc {
	public Content(EPUBMetaData epb) {
		super("package", null);
		addAtr("xmlns", "http://www.idpf.org/2007/opf");
		addAtr("unique-identifier", "bookid");
		addAtr("version", "2.0");
		
		XMLNode main;
		XMLNode nodo;
		
		main=new XMLNode("metadata",null,this,true);
		main.addAtr("xmlns:dc", "http://purl.org/dc/elements/1.1/");
		new XMLNode("dc:title"     ,epb.getTitle()     ,main,true);
		new XMLNode("dc:publisher" ,epb.getPublisher() ,main,true);
		new XMLNode("dc:creator"   ,epb.getCreator()   ,main,true);
		Vector<String> subjects=epb.getSubject();
		if(subjects!=null)
			for(int i=0;i<subjects.size();i++)
				new XMLNode("dc:subject"    ,subjects.get(i),main,true);
		new XMLNode("dc:rights"    ,epb.getRights()     ,main,true);
		new XMLNode("dc:identifier","urn:uuid:"+epb.getIdentifier() ,main,false);
		new XMLNode("dc:language"  ,epb.getLanguage()   ,main, false);
		
		main=new XMLNode("manifest",null,this,false);
		XMLNode toc=new XMLNode("item",null,main,true);
		// <item id="ncx" href="toc.ncx" media-type="application/x-dtbncx+xml"/>
		toc.addAtr("id", "ncx");
		toc.addAtr("href", "toc.ncx");
		toc.addAtr("media-type", "application/x-dtbncx+xml");
		Vector<XMLNode> mftItems=epb.getManifestItems();
		for(int i=0;i<mftItems.size();i++)
			main.addChild(mftItems.get(i));
		
		main=new XMLNode("spine",null,this,false);
		main.addAtr("toc", "ncx");
		Vector<XMLNode> spineItems=epb.getSpineOrder();
		for(int i=0;i<spineItems.size();i++){
			nodo=new XMLNode("itemref",null,main,false);
			nodo.addAtr("idref", spineItems.get(i).getAtr("id"));
		}
	}

	public final static String mime[][]={
		{"ncx",  "application/x-dtbncx+xml"},
		{"xhtml","application/xhtml+xml"},
		{"html", "application/xhtml+xml"}, //Suponemos que se ha convertido
		{"svg",  "image/svg+xml"},
		{"jpg",  "image/jpeg"},
		{"jpeg", "image/jpeg"},
		{"png",  "image/png"},
		{"bmp",  "image/bmp"},
		{"gif",  "image/gif"},
		{"xpgt", "application/adobe-page-template+xml"}, 
		{"css",  "text/css"},
		{"ttf",  "font/opentype"},
		{"otf",  "font/opentype"},
		{"xml",  "text/xml"}
	};

	public static String getMime(String t){
		for(int i=0;i<mime.length;i++){
			if(mime[i][0].equalsIgnoreCase(t))
				return mime[i][1];
		}
		return null;
	}
	

}
