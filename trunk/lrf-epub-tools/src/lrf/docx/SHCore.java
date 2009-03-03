package lrf.docx;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SHCore extends DefaultHandler {
	public String title=null;
	public String creator=null;
	boolean getTitle=false;
	boolean getCreator=false;
	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if(localName.equals("title")){
			getTitle=true;
		} else if(localName.equals("creator")){
			getCreator=true;
		}
	}
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		if(getTitle){
			title=new String(ch,start,length);
			getTitle=false;
		}
		if(getCreator){
			creator=new String(ch,start,length);
			getCreator=false;
		}
	}
	
	
}
