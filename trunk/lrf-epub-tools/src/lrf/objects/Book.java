package lrf.objects;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.InflaterInputStream;

import lrf.Utils;
import lrf.buffer.MappedReader;
import lrf.buffer.Reader;
import lrf.conv.Renderer;
import lrf.conv.RendererEPUB;
import lrf.conv.RendererIText;
import lrf.epub.EPUBMetaData;
import lrf.html.HtmlDoc;
import lrf.html.HtmlOptimizer;
import lrf.parse.ParseException;

import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.HtmlWriter;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;


/**
 * Representa un eBook BBeB
 * 
 * @author elinares
 * 
 */
public class Book extends EPUBMetaData implements Serializable {
	public static final String mainFontName="LinLibertine_Re-2.8.14.otf";
	public static final String secoFontName="LinLibertine_Re-2.8.14.otf";
	public static Hashtable<String, BaseFont> bfonts = new Hashtable<String, BaseFont>();
	private static final long serialVersionUID = -2392308085622379053L;
	static {
		BaseFont bf;
		try {
			bf = BaseFont.createFont("fonts/"+mainFontName,BaseFont.CP1252, BaseFont.EMBEDDED);
			bfonts.put("Dutch801 Rm BT Roman", bf);
			bf = BaseFont.createFont("fonts/"+secoFontName,BaseFont.CP1252, BaseFont.EMBEDDED);
			bfonts.put("Swis721 BT Roman", bf);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) { 
			try {
				InputStream is;
				ByteOutputStream bos;
				is = new String().getClass().getResourceAsStream("/fonts/"+mainFontName);
				bos = new ByteOutputStream();
				bos.write(is);
				bf = BaseFont.createFont(
						mainFontName,BaseFont.CP1252, BaseFont.EMBEDDED, 
						true, bos.getBytes(), null);
				bfonts.put("Dutch801 Rm BT Roman", bf);
				is = new String().getClass().getResourceAsStream("/fonts/"+secoFontName);
				bos = new ByteOutputStream();
				bos.write(is);
				bf = BaseFont.createFont(
						secoFontName,BaseFont.CP1252, BaseFont.EMBEDDED, 
						true, bos.getBytes(), null);
				bfonts.put("Swis721 BT Roman", bf);
			} catch (DocumentException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	private int currentPrintPosition=0;
	byte[] gifData;
	public final int LRF_CINFOBLOCK_DATA = 0x58;
	// Constantes
	public final int LRF_CINFOBLOCK_LEN = 0x4C;
	public final int LRF_GIF_LEN = 0x50;
	public final int LRF_UINFOBLOCK_LEN = 0x54;
	File lrfFile;
	boolean LRFPageSize=true;
	String metaData = null;
	int nObjects;
	Hashtable<Integer, BBObj> Objs = new Hashtable<Integer, BBObj>();
	int offObjectTable;
	int rootObjectId;
	boolean sorted=true;

	//TOC
	TOC toc=null;

	int version;

	int xorKey = 0;

	public Book(File fname) throws Exception {
		super("");
		lrfFile=fname;
		MappedReader pb=new MappedReader(fname);
		getMetaData(pb);
		// Ahora cargamos los objetos
		loadObjects(pb);
		language=getFromMD("</Language>");
	}
	public String getAuth() {
		return getFromMD("</Author>");
	}
	
	public String getBookID() {
		return getFromMD("</BookID>");
	}

	@Override
	public String getCreator() {
		return getAuth();
	}
	
	public void getEPUB(String outf, Hashtable<String, String> repl, String catpar) 
	throws Exception {
		init(outf);
		//Nombre del fichero en html
		String fileName=Utils.toUnhandText(lrfFile.getName());
		if(fileName.toLowerCase().endsWith(".lrf")){
			fileName=fileName.substring(0,fileName.length()-".lrf".length());
		}
		//Creamos espacio temporal
		File tmpfDir=File.createTempFile("lrft", "tmp");
		tmpfDir.delete();
		tmpfDir.mkdirs();
		//Convertimos a HTML
		HtmlDoc htmldoc=new HtmlDoc(fileName,getTitle(),getAuth(),"LRFTools",getBookID(),tmpfDir);
		sorted=false;
		Renderer render=new RendererEPUB(htmldoc,repl);
		getObject(rootObjectId).render(render);
		htmldoc.createEPUB(this,catpar);
		sorted=true;
		//Optimizamos y creamos CSS
		HtmlOptimizer opt=new HtmlOptimizer(htmldoc,tmpfDir);
		opt.setPaginateKB(150);
		int pages=opt.optimize(true);
		opt.ratStyles(true);
		buildCSS(fileName+".css", opt.getStyles(),false);
		//Generamos epub
		//xhtml
		for(int i=0;i<pages;i++){
			File f=new File(tmpfDir,fileName+"-"+(1+i)+".html");
			processFile(f, fileName+"-"+(1+i)+".html");
		}
		//Borramos directorio temporal
		File list[]=tmpfDir.listFiles();
		for(int i=0;i<list.length;i++)
			list[i].delete();
		tmpfDir.delete();
		//Comprobamos si tiene TOC
		if(toc!=null && toc.ent!=null){
			for(int i=0;i<toc.ent.size();i++){
				TOC.Entry t=toc.ent.elementAt(i);
				createNavPoint(Utils.toTOCText(t.lab), opt.getRealDest("P"+t.pag), getNavMap());
			}
		}else{
			createNavPoint("Top", fileName+"-1.xhtml", getNavMap());
		}
		close();
	}

	public String getFromMD(String s) {
		int end = metaData.indexOf(s);
		int beg = end;
		for (; beg >= 0; beg--)
			if (metaData.charAt(beg) == '>')
				break;
		if (beg >= 0)
			return metaData.substring(beg+1, end);
		return null;
	}
	
	public Renderer getHTML(OutputStream os,File img, String imgpath, Hashtable<String, String> repl) throws Exception {
		Document doc = new Document();
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		HtmlWriter pw = HtmlWriter.getInstance(doc, baos);
		pw.setCloseStream(false);
		String au=getAuth();
		doc.addAuthor(au==null?"No Author":au);
		au=getTitle();
		doc.addTitle(au==null?"No title":au);
		if(getBookID()!=null)
			doc.addHeader("BookID", getBookID());
		doc.open();
		sorted=false;
		RendererIText render=new RendererIText(pw,doc,img,imgpath,repl);
		getObject(rootObjectId).render(render);
		sorted=true;
		doc.close();
		
		String html=new String(baos.toByteArray());
		html=html.replace("img src=\"file:", "img src=\"");
		os.write(html.getBytes());
		return render;
	}

	@Override
	public String getIdentifier() {
		return getBookID();
	}
	
	private void getMetaData(Reader pb) throws IOException,
			UnsupportedEncodingException {		
		version = pb.getShort(8);
		xorKey = pb.getShort(10);
		rootObjectId = pb.getInt(12);
		nObjects = pb.getInt(16);
		offObjectTable = pb.getInt(24);
		//Obtenemos MetaData
		int cbibd = pb.getShort(LRF_CINFOBLOCK_LEN); //Compressed bookInfo Data Len
		byte compressed[] = new byte[cbibd - 4];
		pb.copy(LRF_CINFOBLOCK_DATA, compressed, 0, compressed.length);
		int ubibd = pb.getInt(LRF_UINFOBLOCK_LEN);
		byte mdb[] = new byte[ubibd - 2];
		InputStream in = new ByteArrayInputStream(compressed);
		InflaterInputStream inflater = new InflaterInputStream(in);
		inflater.read(mdb);
		inflater.close();
		in.close();
		metaData = new String(mdb, "UTF-16LE");
		//Obtenemos Thumbnail
		int gifLen = pb.getInt(LRF_GIF_LEN);
		gifData = new byte[gifLen];
		pb.copy((LRF_UINFOBLOCK_LEN + cbibd), gifData, 0, gifData.length);
		
	}

	public BBObj getObject(int id) {
		BBObj ret=Objs.get(id);
		if(ret==null){
			return ret;
		}
		if(!sorted && ret.printPosition==0){
			ret.printPosition=++currentPrintPosition;
		}
		return ret;
	}

	public String getPDF(OutputStream os, boolean prsSize, Hashtable<String, String> repl) throws Exception {
		Document doc = new Document();
		PdfWriter pw = PdfWriter.getInstance(doc, os);
		pw.setCompressionLevel(9);
		pw.setCloseStream(false);
		String au=getAuth();
		String ti=getTitle();
		doc.addAuthor(au == null ? "No Author" : au );
		doc.addTitle( ti == null ? "No Title" : ti);
		if(getBookID()!=null)
			doc.addHeader("BookID", getBookID());
		if(prsSize){
			doc.setPageSize(new Rectangle(600*RendererIText.xconv, 800*RendererIText.yconv));
			doc.setMargins(10, 10, 10, 10);
		}
		LRFPageSize=prsSize;
		doc.open();
		sorted=false;
		RendererIText render=new RendererIText(pw,doc,null,null,repl);
		pw.setStrictImageSequence(true);
		getObject(rootObjectId).render(render);
		sorted=true;
		try {
			doc.close();
		}catch(RuntimeException rte){
			return rte.getMessage();
		}
		return "OK";
	}

	@Override
	public String getPublisher() {
		return getFromMD("</Publisher>");
	}
	
	@Override
	public String getRights() {
		return "free";
	}


	public void getRTF(OutputStream os, Hashtable<String, String> repl) throws Exception {
		Document doc = new Document();
		DocWriter pw = RtfWriter2.getInstance(doc, os);
		pw.setCloseStream(false);
		doc.addAuthor(getAuth()==null?"No Author":getAuth());
		doc.addTitle(getTitle()==null?"No Title":getTitle());
		if(getBookID()!=null)
			doc.addHeader("BookID", getBookID());
		doc.setPageSize(new Rectangle(600*RendererIText.xconv, 800*RendererIText.yconv));
		doc.setMargins(10, 10, 10, 10);
		doc.open();
		sorted=false;
		RendererIText r=new RendererIText(pw,doc,null,null,repl);
		getObject(rootObjectId).render(r);
		sorted=true;
		doc.close();
	}

	@Override
	public Vector<String> getSubject() {
		return new Vector<String>();
	}

	public String getTitle() {
		return getFromMD("</Title>");
	}

	public void getXML(OutputStream os, Hashtable<String, String> repl) throws IOException{
		String replaced=metaData.substring(1);
		replaced=replaced.replace("?>", "?>\n<BBeB>");
		os.write(replaced.getBytes());
		StringBuffer sb=new StringBuffer(2*1024*1024);
		getObject(rootObjectId).toXML(sb, 0);
		os.write(sb.toString().getBytes());
		os.write("</BBeB>".getBytes());
	}

	public int getXorKey() {
		return xorKey;
	}

	void loadObjects(Reader main) throws ParseException {
		for (int i = 0; i < nObjects; i++) {
			// Reseteamos el puntero
			main.reset();
			int objectID =     main.getInt(offObjectTable + (i * 16));
			int objectOffset = main.getInt(offObjectTable + (i * 16) + 4);
			int objectSize =   main.getInt(offObjectTable + (i * 16) + 8);
			// Posicionamos al principio del objeto
			Reader pb=main.getSubReader(objectOffset, objectSize);
			if (pb.getShort() != 0xf500) {
				throw new ParseException("Object missing 0xf500 tag at : "
						+ objectOffset);
			}
			int objectTypeValue = pb.getShort(6);
			BBObj bo = null;
			switch (objectTypeValue) {
			case BBObj.ot_INVALID_00:
			case BBObj.ot_INVALID_0f:
			case BBObj.ot_INVALID_10:
			case BBObj.ot_INVALID_1b:
			case BBObj.ot_Invalid_18:
				bo = new BBObj(this, objectID, objectTypeValue, pb);
				break;
			case BBObj.ot_Block:
				bo = new Block(this, objectID, pb);
				break;
			case BBObj.ot_BlockAtr:
				bo = new BlockAtr(this, objectID, pb);
				break;
			case BBObj.ot_MiniPage:
				bo = new MiniPage(this, objectID, pb);
				break;
			case BBObj.ot_BlockList:
				bo = new BlockList(this, objectID, pb);
				break;
			case BBObj.ot_Text:
				bo = new Text(this, objectID, pb);
				break;
			case BBObj.ot_TextAtr:
				bo = new TextAtr(this, objectID, pb);
				break;
			case BBObj.ot_Image:
				bo = new OBImage(this, objectID, pb);
				break;
			case BBObj.ot_Canvas:
				bo = new Canvas(this, objectID, pb);
				break;
			case BBObj.ot_ParagraphAtr:
				bo = new ParagraphAtr(this, objectID, pb);
				break;
			case BBObj.ot_ImageStream:
				bo = new ImageStream(this, objectID, pb);
				break;
			case BBObj.ot_Import:
				bo = new Import(this, objectID, pb);
				break;
			case BBObj.ot_Buttom:
				bo = new Button(this, objectID, pb);
				break;
			case BBObj.ot_Window:
				bo = new Window(this, objectID, pb);
				break;
			case BBObj.ot_PopUpWin:
				bo = new PopUpWindow(this, objectID, pb);
				break;
			case BBObj.ot_Sound:
				bo = new Sound(this, objectID, pb);
				break;
			case BBObj.ot_PlaneStream:
				bo = new PlaneStream(this, objectID, pb);
				break;
			case BBObj.ot_Font:
				bo = new FontIn(this, objectID, pb);
				break;
			case BBObj.ot_ObjectInfo:
				bo = new ObjectInfo(this, objectID, pb);
				break;
			case BBObj.ot_BookAtr:
				bo = new BookAtr(this, objectID, pb);
				break;
			case BBObj.ot_SimpleText:
				bo = new SimpleText(this, objectID, pb);
				break;
			case BBObj.ot_TOC:
				bo = new TOC(this, objectID, pb);
				break;
			case BBObj.ot_PageTree:
				bo = new PageTree(this, objectID, pb);
				break;
			case BBObj.ot_Page:
				bo = new Page(this, objectID, pb);
				break;
			case BBObj.ot_Header:
				bo = new Header(this, objectID, pb);
				break;
			case BBObj.ot_Footer:
				bo = new Footer(this, objectID, pb);
				break;
			case BBObj.ot_PageAtr:
				bo = new PageAtr(this, objectID, pb);
				break;
			}
			Objs.put(objectID, bo);
		}
	}

}
