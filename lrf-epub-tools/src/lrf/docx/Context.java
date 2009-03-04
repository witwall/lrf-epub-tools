package lrf.docx;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import lrf.conv.BaseRenderer;
import lrf.docx.states.STDrawing;
import lrf.docx.states.STMain;
import lrf.docx.states.STNumbering;
import lrf.docx.states.STTable;
import lrf.docx.states.State;
import lrf.epub.EPUBMetaData;

import org.xml.sax.Attributes;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

//manage state change
public class Context extends EPUBMetaData{
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
	public static final int sizeOfChains=40;
	
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

	public void addData(String str) {
		arrayList.add(count, str);
		count++;
	}
	
	private int numpages=0;
	public void addPageBreak(){
		numpages++;
		if(!BaseRenderer.noPageBreakEmit)
			addData("<div style=\"page-break-before:always\"/>","<div");
		if(numpages>0 && numpages%sizeOfChains==0)
			splitOutput();
	}
	
	Vector<Integer> pageBreaks=new Vector<Integer>();
	public void splitOutput(){
		pageBreaks.add(count);
	}
	
	public void addData(String str, String after){
		for(int i=count-1;i>=0;i--){
			if(arrayList.get(i).startsWith(after)){
				if(arrayList.get(i).equals(str))
					return;
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
		if(RecurseDirs.noo && ef.exists())
			return;
		parent=new File(fnout).getParentFile();
		tmpDir=new File(parent,"lrfTools-qmTmp");
		tmpDir.mkdirs();

		// Parse Document XML file with default handler
		byte docxml[]=getZipOSNamed("word/document.xml");
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		factory.setNamespaceAware(true);
		parser = factory.newSAXParser();
		SHDocument docp=new SHDocument(this);
		
		init(ef.getCanonicalPath());
		parser.parse(new ByteArrayInputStream(docxml), docp);
		
		int pi=0,pf=pageBreaks.get(0),pk=0;
		do{
			ByteOutputStream bos=new ByteOutputStream();
			for(int i=pi;i<pf;i++){
				bos.write(arrayList.get(i).getBytes());
			}
			String contenido=htmlToXhtml(bos.newInputStream());
			ByteArrayInputStream bais=new ByteArrayInputStream(contenido.getBytes("UTF-8"));
			processFile(bais, "chain-"+pk+".xhtml");
			pi=pf;
			pk++;
			if(pk>=pageBreaks.size())
				break;
			pf=pageBreaks.get(pk);
		}while(true);
		
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