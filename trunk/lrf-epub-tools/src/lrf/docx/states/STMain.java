package lrf.docx.states;

import java.util.ArrayList;

import lrf.conv.BaseRenderer;
import lrf.docx.Context;
import lrf.docx.SHDocument;
import lrf.docx.SHRelations;

import org.xml.sax.Attributes;

public class STMain implements State {
	private ArrayList<String> element;// temporary localName list
	private static STMain singleton = new STMain();
	private final int FONTSZ = 7;

	private STMain() {
		element = new ArrayList<String>();
	}

	public static STMain getInstance() {
		return singleton;
	}

	public void startDoc(Context context) {
		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "
				+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">" + "<head>"
				+ "<meta http-equiv=\"Content-Type\" content=\"text/html;\" />"
				+ "<title></title>" + "</head>" + "<body>";
		context.addData(str);
	}

	public void startEle(Context context, SHRelations rels, String uri,
			String lName, String qName, Attributes attributes) {
		if (!uri.equals(SHDocument.WORDPROCESSINGML))
			return;
		if (lName.equals("jc")
				&& !attributes.getValue(uri, "val").equals("left")) {
			context.addData("<div style=\"text-align:"
					+ attributes.getValue(uri, "val") + ";\"> ");
			element.add(lName);
		} else if (lName.equals("r")) {
			element.add(lName);
		} else if (lName.equals("rPr") && element.contains("r")) {
			element.add(lName);
		} else if (lName.equals("color") && element.contains("rPr")) {
			element.add(lName);
			context.addData("<font color=\"#" + attributes.getValue(uri, "val")
					+ "\">");
		} else if (lName.equals("sz") && element.contains("rPr")) {
			element.add(lName);
			int fontsize = Integer.parseInt(attributes.getValue(uri, "val"))
					/ FONTSZ;
			context.addData("<font size=\"" + fontsize + "\">");
		} else if(lName.equals("lastRenderedPageBreak")){
			context.addPageBreak();
		} else if(lName.equals("instrText")){
			context.avoidCharsEmits=true;
		}
	}

	public void endEle(Context context, String uri, String lName,
			String qName) {
		if (!uri.equals(SHDocument.WORDPROCESSINGML))
			return;
		if (lName.equals("pPr")) {
			String tmp = STNumbering.getInstance().getnumPr();
			if (tmp != null) {
				context.addData(tmp + " ");
				STNumbering.getInstance().setnumPr(null);
			}
		} else if (lName.equals("r")) {
			if (element.contains("rPr") && element.contains("color")) {
				context.addData("</font>");
			}
			if (element.contains("rPr") && element.contains("sz")) {
				context.addData("</font>");
			}
		} else if (lName.equals("p")) {
			if (element.contains("jc")) {
				context.addData("</div>");
				element.remove(element.indexOf("jc"));
			} else {
				context.addData("<br/>");
			}
			initList();
		} else if(lName.equals("instrText")){
			context.avoidCharsEmits=false;
		}
	}

	public void endDoc(Context context) {
		context.addData("</body></html>");
		context.addPageBreak();
	}

	public void initList() {
		element.removeAll(element);
	}
}