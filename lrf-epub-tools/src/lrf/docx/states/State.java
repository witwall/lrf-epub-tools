package lrf.docx.states;

import lrf.docx.Context;
import lrf.docx.SHRelations;

import org.xml.sax.Attributes;

public interface State {
	public abstract void startDoc(Context ctx);

	public abstract void startEle(Context ctx, SHRelations rels, String uri, String lName, String qName, Attributes attr);

	public abstract void endEle(Context ctx, String uri, String lName, String qName);

	public abstract void endDoc(Context ctx);
}
