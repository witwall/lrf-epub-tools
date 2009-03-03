package lrf.docx.states;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import lrf.docx.Context;
import lrf.docx.SHDocument;
import lrf.docx.SHRelations;

import org.xml.sax.Attributes;

public class STNumbering implements State {
	private String ilvl_val, numId_val, abstractNumId_val, start_val,
			numPr = null;
	private ArrayList<String> elementList;// temporary localName list
	public int num_count = 0;
	private static STNumbering singleton = new STNumbering();

	private STNumbering() {
		elementList = new ArrayList<String>();
		initList();
		ilvl_val = "";
		numId_val = "";
	}

	public static STNumbering getInstance() {
		return singleton;
	}

	public void startDoc(Context context) {
	}

	public void startEle(Context context, SHRelations rels, String uri,
			String localName, String qName, Attributes attributes) {
		if (localName.equals("ilvl")) {
			if (!ilvl_val.equals(attributes.getValue(uri, "val"))) {
				ilvl_val = attributes.getValue(uri, "val");
				num_count = 0;// reset count
			}
		} else if (localName.equals("numId")) {
			if (!numId_val.equals(attributes.getValue(uri, "val"))) {
				numId_val = attributes.getValue(uri, "val");
				num_count = 0;// reset count
			}
			readNumbering();// read numbering.xml for the 1st time
		} else if (localName.equals("num")) {// numbering.xml
			if (attributes.getValue(uri, "numId").equals(numId_val)) {
				elementList.add("num");
			}
		} else if (localName.equals("abstractNumId")
				&& elementList.contains("num")) {// numbering.xml
			abstractNumId_val = attributes.getValue(uri, "val");
		} else if (localName.equals("abstractNum")) {
			if (attributes.getValue(uri, "abstractNumId").equals(
					abstractNumId_val)) {
				elementList.add("lvl");
			}
		} else if (localName.equals("lvl") && elementList.contains("lvl")) {
			if (attributes.getValue(uri, "ilvl").equals(ilvl_val)) {
				elementList.add("ilvl");
			}
		} else if (localName.equals("start") && elementList.contains("ilvl")) {
			start_val = attributes.getValue(uri, "val");
		} else if (localName.equals("numFmt") && elementList.contains("ilvl")) {
			elementList.add(attributes.getValue(uri, "val"));
		}
	}

	public void endEle(Context context, String uri, String localName,
			String qName) {
		if (localName.equals("num") && elementList.contains("num")) {
			elementList.remove(elementList.indexOf("num"));// delete redundant
															// data
		} else if (localName.equals("lvl") && elementList.contains("lvl")) {
			if (elementList.contains("ilvl")) {// delete redundant data
				elementList.remove(elementList.indexOf("lvl"));
				elementList.remove(elementList.indexOf("ilvl"));
			}
		} else if (localName.equals("numPr")) {
			readNumbering();// read numbering.xml for the 2nd time
			if (num_count == 0) {
				num_count = Integer.parseInt(start_val);
			} else {
				num_count++;
			}
			if (elementList.contains("bullet")) {
				numPr = "-";
			} else {
				numPr = String.valueOf(num_count);
			}
			initList();
		}
	}

	public void endDoc(Context context) {
	}

	public void readNumbering() {
		try {

			// Create Sax Parser
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser parser = factory.newSAXParser();

			// Parse XML file with default handler
			/*
			ZipInputStream instream = new ZipInputStream(new FileInputStream(
					"sample/sample.docx"));
			ZipEntry entry = null;
			while ((entry = instream.getNextEntry()) != null) {
				if (entry.getName().equals("word/numbering.xml")) {
					parser.parse(instream, new SHDocument());
					break;
				}
			}
			*/

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setnumPr(String numpr) {
		numPr = numpr;
	}

	public String getnumPr() {
		return numPr;
	}

	public void initList() {
		elementList.removeAll(elementList);
		abstractNumId_val = "";
		start_val = "";
	}
}