package lrf.docx;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import lrf.RecurseDirs;
import lrf.Utils;
import lrf.docx.states.STDrawing;
import lrf.docx.states.STMain;
import lrf.docx.states.STNumbering;
import lrf.docx.states.STTable;
import lrf.docx.states.State;
import lrf.epub.EPUBMetaData;

import org.xml.sax.Attributes;

//manage state change
public class Context extends EPUBMetaData{
	BufferedOutputStream outStream = null;
	private ArrayList<String> arrayList;// transformed XHTML data
	private Stack<State> stack;// state info: table, numbering, main, etc
	private int count;// index of arrayList
	private State state;// table, numbering, main, etc
	SHRelations rels=null;
	SHCore core=null;
	File parent=null;
	String zName;
	String fnout;
	File tmpDir;
	String identifier=null;
	public boolean avoidCharsEmits=false;
	
	public Context(String filein) {
		super("en");
		try {
			int c=filein.lastIndexOf(".");
			if(c>=0){
				fnout=filein.substring(0,c)+".html";
			}else {
				fnout=filein+".html";
			}
			zName=filein;
			arrayList = new ArrayList<String>();
			stack = new Stack<State>();
			state = STMain.getInstance();// default state
			stack.push(state);// set default state

			// Create Sax Parser
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser;
			factory.setNamespaceAware(true);
			// Parse Document relations
			byte docxml[];
			docxml=getZipOSNamed("word/_rels/document.xml.rels");
			parser=factory.newSAXParser();
			SHRelations rels=new SHRelations(this);
			parser.parse(new ByteArrayInputStream(docxml), rels);
			// Parse core
			docxml=getZipOSNamed("docProps/core.xml");
			parser=factory.newSAXParser();
			core=new SHCore();
			parser.parse(new ByteArrayInputStream(docxml), core);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		initArrayList();
	}

	public void setRels(SHRelations r){
		rels=r;
	}
	
	public String getFNOut(){
		return fnout;
	}
	
	public byte[] getZipOSNamed(String eName) {
		byte baos[] = null;
		try {
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zName));
			ZipEntry ze = null;
			while ((ze = zis.getNextEntry()) != null) {
				if (ze.getName().equals(eName)) {
					baos = new byte[(int) ze.getSize()];
					int readed=0;
					while(readed!=baos.length){
						readed+=zis.read(baos,readed,baos.length-readed);
					}
					break;
				}
			}
			zis.close();
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		return baos;
	}
	public void doStartDocument() {
		state.startDoc(this);
	}

	public void doStartElement(String uri, String localName, String qName, Attributes attr) {
		state.startEle(this, rels, uri, localName, qName, attr);
	}

	public void doCharacters(String charstr) {
		if(!avoidCharsEmits)
			addData(Utils.toXMLText(charstr));
		else
			addData(" ");
	}

	public void doEndElement(String uri, String localName, String qName) {
		state.endEle(this, uri, localName, qName);
	}

	public void doEndDocument() {
		state.endDoc(this);
	}

	public void writeStream(String str) {
		try {
			outStream.write(str.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeStream() {
		try {
			if (outStream != null) {
				outStream.flush();
				outStream.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeArrayList() {
		StringBuffer sb=new StringBuffer("");
		for (int i = 0; i < count; i++) {
			sb.append(arrayList.get(i));
		}
		writeStream(sb.toString());
		initArrayList();
	}

	public void addData(String str) {
		arrayList.add(count, str);
		count++;
	}
	
	public void addData(String str, String after){
		for(int i=count-1;i>=0;i--){
			if(arrayList.get(i).startsWith(after)){
				arrayList.add(i+1, str);
				count++;
				break;
			}
		}
	}
	
	public void addBytes(String name, byte[] content) {
		try {
			ByteArrayInputStream bais=new ByteArrayInputStream(content);
			processFile(bais, name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void rewriteData(int i, String str) {
		arrayList.remove(i);
		arrayList.add(i, str);
	}

	public void removeData(int i) {
		arrayList.remove(i);
		count--;
	}

	public void initArrayList() {
		count = 0;
		arrayList=new ArrayList<String>();
	}

	public int getCount() {
		return count;
	}

	public void setStartState(String str) {
		if (str.equals("p")) {
			state = STMain.getInstance();
			stack.push(state);
		} else if (str.equals("tbl")) {
			state = STTable.getInstance();
			stack.push(state);
		} else if (str.equals("numPr")) {
			state = STNumbering.getInstance();
			stack.push(state);
		} else if(str.equals("drawing")) {
			state = STDrawing.getInstance();
			stack.push(state);
		}
	}

	public void setEndState(String str) {
		if (str.equals("p") || 
				str.equals("tbl") || 
				str.equals("numPr") || 
				str.equals("drawing")) {
			stack.pop();
			state = stack.peek();// set stack top state to current state
		}
	}
	
	public void parse(File ef) throws Exception {
		File fout=new File(fnout);
		parent=new File(fnout).getParentFile();
		tmpDir=new File(parent,"lrfTools-qmTmp");
		tmpDir.mkdirs();
		File f=new File(tmpDir,fout.getName()+".xhtml");
		outStream = new BufferedOutputStream(new FileOutputStream(f));

		// Parse Document XML file with default handler
		byte docxml[]=getZipOSNamed("word/document.xml");
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		factory.setNamespaceAware(true);
		parser = factory.newSAXParser();
		SHDocument docp=new SHDocument(this);
		
		File docxFile=new File(zName);
		String lastDocxPart=docxFile.getName().substring(0,docxFile.getName().length()-5);
		if(RecurseDirs.noo && ef.exists())
			return;
		init(ef.getCanonicalPath());
		parser.parse(new ByteArrayInputStream(docxml), docp);
		String contenido=htmlToXhtml(new FileInputStream(f));
		
		ByteArrayInputStream bais=new ByteArrayInputStream(contenido.getBytes("UTF-8"));
		String name=fout.getName();
		processFile(bais, name.substring(0,name.length()-5)+".xhtml");
		
		close();
		
		removeTmpDir(tmpDir);
	}
	
	public void removeTmpDir(File tmp){
		File list[]=tmp.listFiles();
		for(File f:list){
			f.delete();
		}
		tmp.delete();
	}

	@Override
	public String getCreator() {
		return core.creator;
	}

	@Override
	public String getIdentifier() {
		if(identifier==null)
			identifier=createRandomIdentifier();
		return identifier;
	}

	@Override
	public String getPublisher() {
		return "LRFTools";
	}

	@Override
	public String getRights() {
		return "none";
	}

	@Override
	public Vector<String> getSubject() {
		return null;
	}

	@Override
	public String getTitle() {
		return core.title;
	}
}