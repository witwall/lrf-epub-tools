package lrf.docx;

import java.util.Hashtable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SHRelations extends DefaultHandler {
	public class Relation {
		public String id;
		public String type;
		public String target;
		public Relation(String i, String ty, String ta){
			id=i;
			type=ty;
			target=ta;
		}
	}
	public Hashtable<String, Relation> relations=new Hashtable<String, Relation>();
	
	@Override
	public void startElement(String uri, String lName, String name, Attributes attr) throws SAXException {
		super.startElement(uri, lName, name, attr);
		if(lName.equals("Relationship")){
			Relation r=new Relation(
					attr.getValue("Id"),
					attr.getValue("Type"),
					attr.getValue("Target"));
			relations.put(r.id,r);
		}
	}

	public Relation getRelation(String name){
		return relations.get(name);
	}
	public SHRelations(Context ctx){
		ctx.setRels(this);
	}
}
