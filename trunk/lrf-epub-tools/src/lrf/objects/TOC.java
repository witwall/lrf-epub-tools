package lrf.objects;

import java.util.Vector;

import lrf.buffer.Reader;
import lrf.objects.tags.Tag;
import lrf.parse.ParseException;

public class TOC extends BBObj {

	public class Entry {
		int pag;
		int ref;
		String lab;

		public Entry(int p, int r, String l) {
			pag = p;
			ref = r;
			lab = l.replace("\\n", " ");
			if(ent==null)
				ent=new Vector<Entry>();
			ent.add(this);
		}
	}

	Vector<Entry> ent;

	public TOC(Book b, int id, Reader pb)
			throws ParseException {
		super(b, id, ot_TOC, pb);
		b.toc=this;
	}

	public void toXML(StringBuffer sb,int level) {
		Tag.pad(sb, "<Obj id=\"" + id + "\" objType=\""+ getObjectTypeName(objType).substring(12) + "\">", false);
		for (int i = 0; i < tags.size(); i++) {
			tags.elementAt(i).toXML(sb,level+1);
		}
		if(ent!=null){
			for (int i = 0; i < ent.size(); i++) {
				Entry e = ent.elementAt(i);
				Tag.pad(sb, " <TOCEntry label=\"" + e.lab + "\" Page=\"" + e.pag
						+ "\" ObjRef=\"" + e.ref + "\"/>\n", false);
			}
		}
		Tag.pad(sb, "</Obj>\n", false);
	}
}
