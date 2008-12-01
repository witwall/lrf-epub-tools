package lrf.html;

import lrf.Utils;



public class Emitter {
	
	public static String body(HtmlStyle st){
		return "<body style=\""+
				st.getStyleContent(StyleItem.st_body)+
				"\">";
	}
	
	public static String img(String fn,String alt,String w, String h){
		return "<div style=\"text-align:center\">"+
				"<img src=\"images/" +fn+"\"" +
				" align=\"Center\"" +
				" alt=\"" +alt+"\"" +
				" width=\"" +w+"\" " +
				" height=\"" +h+"\"/>"+
			   "</div>\n";
	}

	public static String head(String auth, String id, String title,String cssFileName){
		cssFileName=Utils.toUnhandText(cssFileName);
		return
		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"+
		"<head>\n" +
		  " <meta name=\"generator\" content=\"LRFTools\"/>\n" +
		  " <title>"+title+"</title>\n" +
		  " <meta name=\"author\" content=\"" +auth+ "\"/>\n" +
		  " <meta name=\"BookID\" content=\"" +id+"\"/>\n" +
		  " <link href=\"" +cssFileName+"\" rel=\"stylesheet\" type=\"text/css\" />\n" +
		"</head>";
	}
	
	public static String divOpen(HtmlStyle ds){
		if(ds==null)
			return "<div>\n";
		return "<div style=\""+ds.getStyleContent(StyleItem.st_div)+"\">\n";
	}
	
	public static String divClose(){
		return "</div>\n";
	}

	public static String spanOpen(HtmlStyle ss){
		if(ss==null)
			return "<span>";
		return "<span style=\""+ss.getStyleContent(StyleItem.st_span)+"\">";
	}
	
	public static String spanClose(){
		return "</span>";
	}
	
	public static String anchorDest(String name){
		return "<a name=\""+name+"\"/>";
	}
	
	public static String anchorOrig(String href, boolean open){
		if(open)
			return	"<a href=\"#"+href+"\">";
		return "</a>";
	}
}
