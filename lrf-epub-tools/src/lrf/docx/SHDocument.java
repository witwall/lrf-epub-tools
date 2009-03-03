package lrf.docx;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SHDocument extends DefaultHandler {
	//"http://schemas.openxmlformats.org/wordprocessingml/2006/3/main";
	public static final String WORDPROCESSINGML =
	"http://schemas.openxmlformats.org/wordprocessingml/2006/main";
	public Context context = null;

	public SHDocument(Context ctx) {
		super();
		context=ctx;
	}

	public void startDocument() {
		context.doStartDocument();
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (uri.equals(WORDPROCESSINGML)) {
			context.setStartState(localName);// set state
		}
		context.doStartElement(uri, localName, qName, attributes);
	}

	public void characters(char[] ch, int offset, int length) {
		String charstr = new String(ch, offset, length);
		context.doCharacters(charstr);
	}

	public void endElement(String uri, String localName, String qName) {
		context.doEndElement(uri, localName, qName);
		if (uri.equals(WORDPROCESSINGML)) {
			context.setEndState(localName);// set state
		}
	}

	public void endDocument() {
		context.doEndDocument();
	}
}
