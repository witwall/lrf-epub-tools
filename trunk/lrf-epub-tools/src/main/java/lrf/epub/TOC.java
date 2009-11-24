package lrf.epub;



public class TOC extends XMLDoc{

	public TOC(EPUBMetaData epb) {
		super("ncx", XMLDoc.dtNCX);
		addAtr("xmlns", "http://www.daisy.org/z3986/2005/ncx/");
		addAtr("version", "2005-1");
		
		XMLNode main;
		XMLNode nodo;
		
		main=new XMLNode("head",null,this,false);
		nodo=new XMLNode("meta",null,main,false);
		
		nodo.addAtr("name", "dtb:uid");
		nodo.addAtr("content", epb.getIdentifier());
		
		nodo=new XMLNode("meta",null,main,false);
		nodo.addAtr("name", "dtb:depth");
		nodo.addAtr("content", ""+epb.getTOCDepth());
		
		nodo=new XMLNode("meta",null,main,false);
		nodo.addAtr("name", "dtb:totalPageCount");
		nodo.addAtr("content", "0");
		
		nodo=new XMLNode("meta",null,main,false);
		nodo.addAtr("name", "dtb:maxPageNumber");
		nodo.addAtr("content", "0");
		
		
		nodo=new XMLNode("docTitle",null,this,true);
		new XMLNode("text",epb.getTitle(),nodo,true);
		
		addChild(epb.getNavMap());
	}

}
