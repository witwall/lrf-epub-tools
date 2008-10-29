package lrf.conv;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import lrf.RecurseDirs;
import lrf.objects.BBObj;
import lrf.objects.Book;
import lrf.objects.tags.Tag;

import com.lowagie.text.Anchor;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.HtmlWriter;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;

public class RendererIText extends BaseRenderer {

	private static final int CTNV=10;
	private static final long serialVersionUID = 2496781384922338145L;

	public final static float xconv=  90.67F * 2.84F / 600F;
	public final static float yconv= 116.58F * 2.84F / 800F;
	private Document doc;
	public Font fu = null;
	Hashtable<Integer,String> imagenes=new Hashtable<Integer,String>();
	public File imagesDir = null;
	String imgPath;
	public int numImages = 0;
	public int pageNumber=1;
	public Paragraph paragraph = null;
	private DocWriter pw;

	
	public String texto = "";

	public RendererIText(DocWriter pw, Document doc, File img, String imgPath,Hashtable<String, String> repl) {
		super(repl);
		imagesDir = img;
		this.imgPath=imgPath;
		this.pw=pw;
		this.doc=doc;
	}

	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#addImage(com.lowagie.text.Image, java.lang.String, byte[])
	 */
	public void addImage(int id, Image img, String extension, byte[] b) throws Exception{
		if(pw instanceof HtmlWriter){
			dumpImage(id,extension, b);
		}else{
			doc.add(img);
		}
	}
	
	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#newParagraph()
	 */
	public void newParagraph() throws DocumentException {
		if (isEndOfParagraph(texto)) {
			forceNewParagraph();
		} else {
			emptyParagraph = false;
		}
	}

	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#forceNewParagraph()
	 */
	public void forceNewParagraph() throws DocumentException {
		if(!emptyParagraph){
			doc.add(paragraph);
			createParagraph();
		}
	}

	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#createParagraph()
	 */
	public void createParagraph() {
		int parIndent=getTagVal("ParIndent");
		if(parIndent<0){
			parIndent=0;
		}
		float leading= getTagVal("BaseLineSkip")/getTagVal("FontSize");
		if(leading<1)
			leading=1F;
		paragraph=new Paragraph(/*leading*CTNV*/);
		float fli=parIndent/CTNV;
		paragraph.setFirstLineIndent(fli);
		paragraph.setSpacingAfter(leading);
		emptyParagraph = true;
	}

	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#dumpImage(java.lang.String, byte[])
	 */
	public void dumpImage(int id, String ext, byte[] b) throws Exception {
		String imgURL=imagenes.get(id);
		if(imgURL==null){
			numImages++;
			imgURL=File.separator+(numImages)+ext;
			if(RecurseDirs.zout!=null){
				throw new DocumentException("convert to html does not support zipfile");
			}else{
				if(!imagesDir.exists())
					imagesDir.mkdirs();
				if(!imagesDir.isDirectory())
					throw new DocumentException(imagesDir.getAbsolutePath()+ "is not a directory");
				FileOutputStream fosi=new FileOutputStream(
						new File(imagesDir.getCanonicalFile()+imgURL));
				fosi.write(b);
				fosi.close();
			}
		}
		com.lowagie.text.Image img=
			com.lowagie.text.Image.getInstance(imagesDir.getCanonicalFile()+imgURL);
		if(img.getAlt()==null || img.getAlt().length()==0)
			img.setAlt(imgURL);
		URL turl=new URL("file:"+imgPath+(numImages)+ext);
		img.setUrl(turl);
		Paragraph pgp=new Paragraph();
		pgp.setAlignment(Paragraph.ALIGN_CENTER);
		pgp.add(img);
		doc.add(pgp);
		imagenes.put(id,imgURL);
	}

	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#emitText(lrf.conv.Renderer, lrf.objects.tags.Tag)
	 */
	public void emitText(Tag tag)
			throws DocumentException {
		if(paragraph==null)
			createParagraph();
		switch (getTagVal("BlockAlignment")) {
		case 4:
			paragraph.setAlignment(Paragraph.ALIGN_CENTER);
			break;
		case 8:
			paragraph.setAlignment(Paragraph.ALIGN_RIGHT);
			break;
		default:
			paragraph.setAlignment(Paragraph.ALIGN_JUSTIFIED);
			break;
		}
		
		int styl = (getTagVal("Italic")==1 ? Font.ITALIC : Font.NORMAL);
		styl    += (getTagVal("Bold")==1 ? Font.BOLD : 0);
		
		BaseFont bf = Book.bfonts.get(getTagValAsString("fontFaceName"));
		Color col = new Color(getTagVal("rgbColor"));
		texto = tag.getStringVal();
		if (getTagVal("wordSpace") > 0) {
			if (pw instanceof PdfWriter) {
				PdfWriter pw2 = (PdfWriter) pw;
				pw2.getDirectContent().setWordSpacing(getTagVal("wordSpace") / CTNV);
			} else if (pw instanceof RtfWriter2) {
				// RtfWriter2 pw2=(RtfWriter2)pw;
			}
		}
		if (getTagVal("buttonRef") > 0) {
			fu = newFont(bf, getTagVal("FontSize") / CTNV, styl+Font.UNDERLINE, col);
			Anchor orig = new Anchor(texto, fu);
			BBObj button = tag.padre.getPadre().getObject(getTagVal("buttonRef"));
			Tag jt = button.getTag("*JumpTo");
			if (jt != null) {
				orig.setReference("#P" + jt.getValueAt(0));
			}
			paragraph.add(orig);
			emptyParagraph = false;
		} else {
			if (texto.trim().length() > 0) {
				fu = newFont(bf, getTagVal("FontSize") / CTNV, styl, col);
				if(pw instanceof HtmlWriter){
					if (localDestination != null) {
						Anchor dest = new Anchor("", fu);
						dest.setName(localDestination);
						localDestination = null;
						paragraph.add(dest);
					} 
					Chunk chunk = new Chunk(texto, fu);
					paragraph.add(chunk);
					emptyParagraph = false;
				}else {
					if (localDestination != null) {
						Anchor dest = new Anchor(texto, fu);
						dest.setName(localDestination);
						localDestination = null;
						paragraph.add(dest);
						emptyParagraph = false;
					} else {
						Chunk chunk = new Chunk(texto, fu);
						paragraph.add(chunk);
						emptyParagraph = false;
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#getImages()
	 */
	public Vector<String> getImages(){
		Vector<String>ret=new Vector<String>();
		for(Enumeration<String>enu=imagenes.elements();enu.hasMoreElements();){
			ret.add(enu.nextElement());
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#getPageNumber()
	 */
	public int getPageNumber(){
		return doc.getPageNumber();
	}

	public boolean isFooter(){
		return isFooter;
	}
	public boolean isHeader(){
		return isHeader;
	}
	private Font newFont(BaseFont bf, int sz, int st, Color co){
		if(sz==0){
			sz=10;
		}
		Font ret=new Font(bf,sz,st,co);
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#newPage(boolean)
	 */
	public void newPage(boolean prsSize){
		if(prsSize){
			float pageWidth=getTagVal("*PageWidth")*xconv;
			float pageHeight=getTagVal("*PageHeight")*yconv;
			float evenSideMargin=getTagVal("EvenSideMargin")*xconv;
			float oddSideMargin=getTagVal("OddSideMargin")*xconv;
			float topMargin=getTagVal("TopMargin")*yconv;
			doc.setPageSize(new Rectangle(pageWidth,pageHeight));
			doc.setMargins(oddSideMargin, evenSideMargin, topMargin, 0);
			doc.newPage();
		}
	}
	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#resetFooters()
	 */
	public void resetFooters(){
		doc.resetFooter();
	}
	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#resetHeaders()
	 */
	public void resetHeaders(){
		doc.resetHeader();
	}
	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#setFooter(com.lowagie.text.HeaderFooter)
	 */
	public void setFooter(){
		doc.setFooter(new HeaderFooter(paragraph,false));
	}
	public void setFooter(boolean is){
		isFooter=is;
	}
	
	/* (non-Javadoc)
	 * @see lrf.conv.Renderer#setHeader(com.lowagie.text.HeaderFooter)
	 */
	public void setHeader(){
		doc.setHeader(new HeaderFooter(paragraph,false));
	}
	
	public void setHeader(boolean is){
		isHeader=is;
	}
	public void setLocalDestination(String ld){
		localDestination=ld;
	}
}
