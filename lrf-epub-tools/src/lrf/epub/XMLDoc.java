package lrf.epub;

public class XMLDoc extends XMLNode {
	public static final String 
	dtNCX="<!DOCTYPE ncx PUBLIC \"-//NISO//DTD ncx 2005-1//EN\" \"http://www.da" +
			"isy.org/z3986/2005/ncx-2005-1.dtd\">";
	
	String doctype=null;
	public XMLDoc(String tag, String doctype) {
		super(tag, null,null,false);
		this.doctype=doctype;
	}

	@Override
	public String dump() {
		String ret="<?xml version=\"1.0\"?>\n";
		if(doctype!=null)
			ret+=doctype;
		ret+=super.dump();
		return ret;
	}

}
