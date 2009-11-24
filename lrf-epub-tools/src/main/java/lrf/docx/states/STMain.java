package lrf.docx.states;

import java.util.ArrayList;

import lrf.docx.Context;
import lrf.docx.SHDocument;
import lrf.docx.SHRelations;
import lrf.docx.SHStyles;
import lrf.epub.XMLNode;

import org.xml.sax.Attributes;

public class STMain implements State {
	private ArrayList<String> element;// temporary localName list
	private static STMain singleton = new STMain();
	private final int FONTSZ = 1;

	private STMain() {
		element = new ArrayList<String>();
	}

	public static STMain getInstance() {
		return singleton;
	}

	public void startDoc(Context context) {
	}

	public int lastMarkedDIV=-1;
	public void startEle(Context c, SHRelations rels, String uri,
			String lName, String qName, Attributes at) {
		if (!uri.equals(SHDocument.WORDPROCESSINGML))
			return;
		if (lName.equals("jc") && !at.getValue(uri, "val").equals("left")) {
			String justif=at.getValue(uri, "val");
			if(justif.equals("both"))
				justif="justify";
			c.addData("<div style=\"text-align:"+ justif + ";\">");
			element.add(lName);
		} else if (lName.equals("r")) {
			element.add(lName);
		} else if (lName.equals("rPr") ) {
			if(element.contains("r")){
				element.add(lName);
			}
			if(!element.contains("pStyle"))
				lastMarkedDIV=c.getEmitLineCount();
		} else if (lName.equals("color") && element.contains("rPr")) {
			element.add(lName);
			c.addData("<span style=\"font-color=#" + at.getValue(uri, "val")+ "\">");
		} else if (lName.equals("sz") && element.contains("rPr")) {
			element.add(lName);
			int fontsz = Integer.parseInt(at.getValue(uri, "val")) / FONTSZ;
			c.addData("<span style=\"font-size:" + SHStyles.getFontSize(fontsz) + "\">");
		} else if (lName.equals("b") && element.contains("rPr")) {
			element.add(lName);
			c.addData("<span style=\"font-weight:bold\">");
		} else if (lName.equals("i") && element.contains("rPr")) {
			element.add(lName);
			c.addData("<span style=\"font-style:italic\">");
		} else if (lName.equals("condense") && element.contains("rPr")) {
			element.add(lName);
			c.addData("<span style=\"font-stretch:condensed\">");
		} else if (lName.equals("rStyle") && element.contains("rPr")) {
			element.add(lName);
			c.addData("<span class=\"character"+at.getValue(uri, "val")+"\">");
		} else if(lName.equals("lastRenderedPageBreak")){
			c.addPageBreak();
		} else if(lName.equals("instrText")){
			c.avoidCharsEmits=true;
		} else if(lName.equals("pStyle")){
			String styleId=at.getValue(uri,"val");
			c.addData("<div class=\"paragraph"+styleId+"\">");
			if(!c.getStyles().getHeading("paragraph"+styleId).toLowerCase().startsWith("toc"))
				c.addData(Context.command+"np");
			element.add(lName);
		} else if(lName.equals("hyperlink")){
			c.addData(Context.command+"hl "+at.getValue(uri,"anchor"));
		} else if(lName.equals("bookmarkStart")){
			c.addData(Context.command+"bms "+at.getValue(uri,"name"));
		} else if(lName.equals("bookmarkEnd")){
			c.addData(Context.command+"bme ");
		}
	}

	private void checkFontProp(Context context, String fp){
		if (element.contains("rPr") && element.contains(fp)) {
			context.addData("</span>");
			element.remove(fp);
		}
	}
	
	public void endEle(Context context, String uri, String lName,String qName) {
		if (!uri.equals(SHDocument.WORDPROCESSINGML))
			return;
		if (lName.equals("r")) {
			checkFontProp(context, "color");
			checkFontProp(context, "sz");
			checkFontProp(context, "i");
			checkFontProp(context, "b");
			checkFontProp(context, "condense");
			checkFontProp(context, "rStyle");
		} else if (lName.equals("p")) {
			if(element.contains("jc")||element.contains("pStyle")){
				if(element.contains("jc")){
					context.addData("</div>");
					element.remove("jc");
				}
				if(element.contains("pStyle")){
					context.addData("</div>");
					element.remove("pStyle");
				}
			} else {
				context.addData("<br/>");
			}
			initList();
		} else if(lName.equals("instrText")){
			context.avoidCharsEmits=false;
		} 
	}

	public void endDoc(Context context) {
	}

	public void initList() {
		element.removeAll(element);
	}
}