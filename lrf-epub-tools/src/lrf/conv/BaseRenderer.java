package lrf.conv;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import lrf.RecurseDirs;
import lrf.objects.BBObj;
import lrf.objects.tags.Tag;

public abstract class BaseRenderer implements Renderer {
	public static String embeddedFont=null;
	public static String ttcNumber=null;
	public static boolean noPageBreakEmit=false;
	protected Hashtable<String, Tag> current = new Hashtable<String, Tag>();
	protected boolean emptyParagraph = true;
	public boolean isFooter=false;
	public boolean isHeader=false;
	public String localDestination;
	Stack<Hashtable<String, Tag>> pila = new Stack<Hashtable<String, Tag>>();
	public Hashtable<String, String>subst=new Hashtable<String, String>();
	
	public BaseRenderer(Hashtable<String, String>repl){
		subst=repl;
	}
	
	public Tag getTag(String name){
		return current.get(name);
	}
	
	public void setSubstPairs(Hashtable<String, String>repl){
		subst=repl;
	}
	
	public String substitute(String este){
		if(subst==null)
			return este;
		for(Enumeration<String> tgte=subst.keys();tgte.hasMoreElements();){
			String tgt=tgte.nextElement();
			este=este.replace(tgt, subst.get(tgt));
		}
		return este;
	}
	
	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#get(java.lang.String)
	 */
	public Tag get(String name) {
		Tag retval = current.get(name);
		if (retval != null)
			return retval;
		// Valores por defecto.
		if (name.equalsIgnoreCase("BlockAlignment")) {
			retval = new Tag(1, null);
			retval.addValue(1, 4);
		}
		return retval;
	}

	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#getTagVal(java.lang.String)
	 */
	public int getTagVal(String name){
		Tag retval=current.get(name);
		if(retval!=null){
			return retval.getValueAt(0);
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#getTagValAsString(java.lang.String)
	 */
	public String getTagValAsString(String name){
		if(name.equalsIgnoreCase("fontFaceName")){
			if(BaseRenderer.embeddedFont!=null)
				return "user";
		}
		Tag rv=current.get(name);
		if(rv!=null){
			return rv.getStringVal();
		}
		if(name.equalsIgnoreCase("fontFaceName")){
			return "Dutch801 Rm BT Roman";
		}
		return null;
	}
	public static boolean isEndOfParagraph(String t) {
		if (RecurseDirs.catpar==null)
			return true;
		if (t == null || t.length() == 0)
			return true;
		switch (t.charAt(t.length() - 1)) {
		case '.':
		case '?':
		case '!':
			return true;
		}
		return false;
	}
	
	public static boolean isBeginOfParagraph(String t){
		if(t.length()==0)
			return false;
		if(Character.isUpperCase(t.charAt(0)))
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#pop()
	 */
	public void pop() {
		current = pila.pop();
	}
	
	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#push(lrf.objects.BBObj)
	 */
	public void push(BBObj obj) {
		push(obj.getType(), obj.getTags());
	}
	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#push(java.util.Vector)
	 */
	public void push(int objType, Vector<Tag> nt) {
		pushCurrent();
		for (int i = 0; i < nt.size(); i++){
			Tag t=nt.elementAt(i);
			String name=t.getName();
			current.put(name, t);
		}
	}
	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#pushCurrent()
	 */
	public void pushCurrent() {
		Hashtable<String, Tag> old = new Hashtable<String, Tag>();
		for (Enumeration<String> key = current.keys(); key.hasMoreElements();) {
			String val = key.nextElement();
			old.put(val, current.get(val));
		}
		pila.push(old);
	}

	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#setTagVal(java.lang.String, int)
	 */
	public void setTagVal(String name, int val){
		int tipe = tagTypeFromName(name);
		Tag dummy=new Tag(tipe,null);
		dummy.addValue(val, 2);
		current.put(name, dummy);
	}

	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#setTagValAsString(java.lang.String, java.lang.String)
	 */
	public void setTagValAsString(String name, String value){
		int tipe = tagTypeFromName(name);
		Tag dummy=new Tag(tipe,null);
		dummy.setStringVal(value);
		current.put(name, dummy);
	}

	private int tagTypeFromName(String name) {
		int tipe=100;
		for(int i=0;i<Tag.tagNames.length;i++){
			if(name.toLowerCase().equalsIgnoreCase(Tag.tagNames[i])){
				tipe=i;
				break;
			}
		}
		if(name.equalsIgnoreCase("Italic"))
			tipe=0x81;
		if(name.equalsIgnoreCase("bold"))
			tipe=0x15;
		return tipe;
	}
	
}
