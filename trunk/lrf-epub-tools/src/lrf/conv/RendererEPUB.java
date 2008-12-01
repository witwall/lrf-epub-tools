package lrf.conv;

import java.util.Hashtable;
import java.util.Vector;

import lrf.html.HtmlDoc;
import lrf.html.HtmlStyle;
import lrf.html.StyleItem;
import lrf.objects.BBObj;
import lrf.objects.tags.Tag;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;

public class RendererEPUB extends BaseRenderer {
	HtmlDoc doc;
	
	public RendererEPUB(HtmlDoc doc,Hashtable<String, String> repl) {
		super(repl);
		this.doc=doc;
	}
	
	@Override
	public void setLocalDestination(String s) {
		doc.emitAnchorDest(s);
	}

	@Override
	public void push(BBObj obj) {
		super.push(obj);
		switch(obj.getType()){
		case BBObj.ot_BookAtr: 
			doc.setEstilo(HtmlDoc.es_book, new HtmlStyle(obj.getTags())); 
			break;
		case BBObj.ot_PageAtr: 
			doc.setEstilo(HtmlDoc.es_page, new HtmlStyle(obj.getTags())); 
			break;
		case BBObj.ot_BlockAtr: 
			doc.setEstilo(HtmlDoc.es_block, new HtmlStyle(obj.getTags())); 
			break;
		case BBObj.ot_TextAtr: 
			doc.setEstilo(HtmlDoc.es_text, new HtmlStyle(obj.getTags())); 
			break;
		} 
	}

	@Override
	public void addImage(int id, Image im, String ext, byte[] b)
			throws Exception {
		doc.addImage(id, (int)im.getWidth(),(int)im.getHeight(), ext, b);
	}

	@Override
	public void newPage(boolean prsSize) {
		doc.setTemporaryStyle(new StyleItem("page-break-before","always"));
	}

	@Override
	public void newParagraph() throws DocumentException {
		doc.newParagraph();
	}

	@Override
	public void setTagVal(String name, int val) {
		super.setTagVal(name, val);
		Tag t=getTag(name);
		if(t.getType()==129 && val==0)
			t.setType(130);
		StyleItem si=StyleItem.translate(t);
		if(si!=null)
			doc.setTemporaryStyle(si);
	}

	@Override
	public void setTagValAsString(String name, String value) {
		super.setTagValAsString(name, value);
		Tag t=getTag(name);
		StyleItem si=StyleItem.translate(t);
		if(si!=null)
			doc.setTemporaryStyle(si);
	}


	@Override
	public void forceNewParagraph() throws DocumentException {
		doc.newParagraph();
	}

	@Override
	public void createParagraph() {

	}

	@Override
	public void emitText(Tag tag) throws DocumentException {
		String txt=tag.getStringVal();
		txt=substitute(txt);
		doc.emitText(txt);
	}

	@Override
	public Vector<String> getImages() {
		return doc.getImagenes();
	}

	@Override
	public int getPageNumber() {
		return 0;
	}

	@Override
	public boolean isFooter() {
		return false;
	}

	@Override
	public boolean isHeader() {
		return false;
	}

	@Override
	public void resetFooters() {

	}

	@Override
	public void resetHeaders() {

	}

	@Override
	public void setFooter() {

	}

	@Override
	public void setFooter(boolean is) {

	}

	@Override
	public void setHeader() {

	}

	@Override
	public void setHeader(boolean is) {

	}

}
