package lrf.conv;

import java.util.Vector;

import lrf.objects.BBObj;
import lrf.objects.tags.Tag;

import com.lowagie.text.Image;

public interface Renderer {

	public int getTagVal(String name);

	public Tag getTag(String name);
	
	public void push(int objType, Vector<Tag> nt);

	public void setTagVal(String name, int val);

	public String getTagValAsString(String name);

	public void setTagValAsString(String name, String value);

	public Tag get(String name);

	public void pop();

	public void push(BBObj obj);

	public void pushCurrent();

	public void forceNewParagraph() throws Exception;

	public void newParagraph() throws Exception;

	public void createParagraph();

	public void emitText(Tag tag) throws Exception;

	public void newPage(boolean prsSize);

	public Vector<String> getImages();

	public int getPageNumber();

	public void setFooter();

	public void setHeader();

	public void resetFooters();

	public void resetHeaders();

	public void addImage(Image img, String extension, byte[] b)
			throws Exception;
	
	public boolean isHeader();
	
	public boolean isFooter();
	
	public void setFooter(boolean is);
	
	public void setHeader(boolean is);
	
	public void setLocalDestination(String s);
	
}