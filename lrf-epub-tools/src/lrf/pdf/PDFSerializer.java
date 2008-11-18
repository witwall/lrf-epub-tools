package lrf.pdf;

import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import lrf.epub.EPUBMetaData;
import lrf.html.HtmlDoc;
import lrf.pdf.flow.Flower;

import org.pdfbox.pdfviewer.PageDrawer;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import com.lowagie.text.pdf.PdfReader;

public class PDFSerializer {

	static File dirOrig=new File("D:\\tmp\\booksPDF");
	static File dirDest=new File("D:\\tmp\\booksPDF");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		recurse(dirOrig);
	}

	public static void recurse(File dir){
		File list[]=dir.listFiles();
		for(int i=0;i<list.length;i++){
			if(list[i].isDirectory()){
				recurse(list[i]);
			}else if(list[i].getName().toLowerCase().endsWith(".pdf")){
				procPDF(list[i]);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void procPDF(File pdfFile){
		GraphicsHook gh=null;
		boolean ok=true;
		String x=null,y=null;
		File dest=null;
		try {
			x=pdfFile.getCanonicalPath();
			y=x.substring(dirOrig.getCanonicalPath().length());
			dest=new File(dirDest,y.substring(0,y.length()-4)+".xml");
			//Creamos diectorios padre
			dest.getParentFile().mkdirs();
			PageDrawer pdr=new PageDrawer();
			//Este graphicsHook lo usa pagedrawer
			gh = new GraphicsHook(new FileOutputStream(dest));
			//Titulo y autor
			PdfReader pdfReader=new PdfReader(pdfFile.getCanonicalPath());
			HashMap<String, String> info=pdfReader.getInfo();
			String title=info.get("Title");
			String author=info.get("Author");
			pdfReader.close();
			//Paginas del documento
			PDDocument doc=PDDocument.load(pdfFile);
			List<PDPage> pages=doc.getDocumentCatalog().getAllPages();
			for(int i=0;i<pages.size();i++){
				gh.newPage();
				Dimension dim=new Dimension(600,800);
				pdr.drawPage(gh, pages.get(i), dim);
			}
			doc.close();
			gh.close();
			//Volcamos ahora a un EPUB
			HtmlDoc htm=new HtmlDoc(
					new File(dirDest,y.substring(0,y.length()-4)+".epub").getCanonicalPath(),
					title,
					author,
					"LRFTools",
					EPUBMetaData.createRandomIdentifier(),
					new File(dirDest,y.substring(0,y.length()-4))
					);
			Flower flw=gh.getFlower();
			
			System.err.println("Processed "+pdfFile.getName());
		} catch (Exception e) {
			ok=false;
		}finally{
			if(gh!=null){
				gh.close();
			}
			if(!ok){
				dest.renameTo(new File(dirDest,y.substring(0,y.length()-4)+".err"));
			}
		}
	}
	
	public static void showMetaData(PDDocument doc){
		PDDocumentOutline root = doc.getDocumentCatalog().getDocumentOutline();
		if(root==null)
			return;
		PDOutlineItem item = root.getFirstChild();
		while( item != null )
		{
			try {
				System.out.println( "Item:" + item.getTitle()+":"+item.findDestinationPage(doc));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			PDOutlineItem child = item.getFirstChild();
			while( child != null )
			{
              System.out.println( "    Child:" + child.getTitle() );
              child = child.getNextSibling();
			}
			item = item.getNextSibling();
		}

	}
}
