package lrf.docx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EmptyStackException;
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

//manage state change
public class Context extends EPUBMetaData{
	private ArrayList<String> emits;// transformed XHTML data
	private Stack<State> stack;// state info: table, numbering, main, etc
	private int count;// index of arrayList
	private State state;// table, numbering, main, etc
	SHRelations rels=null;
	SHCore core=null;
	SHStyles styles=null;
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
			emits = new ArrayList<String>();
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
			//parse styles
			docxml=getZipOSNamed("word/styles.xml");
			if(docxml!=null){
				parser=factory.newSAXParser();
				styles=new SHStyles();
				parser.parse(new ByteArrayInputStream(docxml),styles);
			}
			
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
		emits.add(count, str);
		count++;
	}
	
	public void addDataAt(String str, int i){
		emits.add(i, str);
		count++;
	}
	
	private int numpages=0;
	public void addPageBreak(){
		numpages++;
		if(!BaseRenderer.noPageBreakEmit)
			addData("<div style=\"page-break-before:always\"/>","<div");
	}
	
	public void addData(String str, String after){
		for(int i=count-1;i>=0;i--){
			if(emits.get(i).startsWith(after)){
				if(emits.get(i).equals(str))
					return;
				emits.add(i+1, str);
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
		emits.remove(i);
		emits.add(i, str);
	}

	public void removeData(int i) {
		emits.remove(i);
		count--;
	}

	public void initArrayList() {
		count = 0;
		emits=new ArrayList<String>();
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
		
		serialize();
		
		close();
		
		removeTmpDir(tmpDir);
	}

	private void serialize() throws IOException, FileNotFoundException {
		int pk=0;
		String st=styles.getCSS();
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		serializeHead(bos,st!=null);
		Stack<String> pila=new Stack<String>();
		for(int i=0;i<emits.size();i++){
			String line=emits.get(i);
			boolean bypass=false;
			if(line.startsWith("</")){
				try {
					pila.pop();
				}catch(EmptyStackException e1){
					bypass=true; //Incredible, emitting more closing tags than needed
				}
			}else if(line.startsWith("<") &&!line.endsWith("/>")){
				pila.push(line);
			}
			if(!bypass)
				bos.write(emits.get(i).getBytes());
			if(bos.size()>150*1024){
				//Emitir archivo
				//Cerramos pila en orden inverso
				for(int j=pila.size()-1;j>=0;j--){
					String opened=pila.elementAt(j);
					String closed="</"+opened.substring(1);
					int pos=closed.indexOf(" ");
					if(pos>0){
						closed=closed.substring(0,pos)+">";
					}
					bos.write(closed.getBytes());
				}
				//Emitimos tail
				serializeTail(bos);
				//procesamos en epub
				processFile(bos, "chain-"+pk+".xhtml");
				//Siguiente archivo
				pk++;
				//Inicializamos 
				bos.reset();
				//Nueva cabecera
				serializeHead(bos,st!=null);
				//Abrimos pila en el mismo orden
				for(int j=0;j<pila.size();j++){
					bos.write(pila.elementAt(j).getBytes());
				}
			}
		}
		//El ultimo tenemos que cerrarlo
		//Emitimos tail
		serializeTail(bos);
		//procesamos en epub
		processFile(bos, "chain-"+pk+".xhtml");
		//Si hay estilos, tambien
		if(st!=null){
			processFile(st, "styles.css");
		}
		
	}

	private void serializeTail(ByteArrayOutputStream bos) throws IOException {
		String str;
		str="</body></html>";
		bos.write(str.getBytes());
	}

	private void serializeHead(ByteArrayOutputStream bos, boolean withStyles) throws IOException {
		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "
			+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
			+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">" + "<head>"
			+ "<meta http-equiv=\"Content-Type\" content=\"text/html;\"/>"
			+ "<title></title>" +
			(withStyles?"\n<link href=\"styles.css\" rel=\"stylesheet\" type=\"text/css\"/>":"")+
			"</head>" + "<body>";
		bos.write(str.getBytes());
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
	
	public SHStyles getStyles(){
		return styles;
	}
	
	public int getEmitLineCount(){
		return count;
	}
}